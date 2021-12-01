package me.bytebeats.compose.graphs.line

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import me.bytebeats.compose.graphs.GraphXAxis
import me.bytebeats.compose.graphs.GraphYAxis
import me.bytebeats.compose.graphs.detectDragZoomGesture
import kotlin.math.ceil

/**
 * Created by bytebeats on 2021/12/1 : 15:00
 * E-mail: happychinapc@gmail.com
 * Quote: Peasant. Educated. Worker
 */

private class RowClip(private val paddingStart: Float, private val paddingEnd: Dp) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline = Outline.Rectangle(
        Rect(
            paddingStart,
            0F,
            size.width - paddingEnd.value * density.density,
            size.height
        )
    )
}

private fun maxValueInYAxis(offset: Float, steps: Int): Float =
    offset * (if (steps > 1) steps - 1 else 1)

private fun axisYScale(points: List<PointF>, plot: Plot): Triple<Float, Float, Float> {
    val steps = plot.yAxis.steps
    val minY = points.minOf { it.y }
    val maxY = points.maxOf { it.y }
    val stepRange = maxY - minY
    val stepInterval = stepRange / (if (steps > 1) steps - 1 else 1)
    val scale = if (plot.yAxis.roundToInt) ceil(stepInterval) else stepInterval
    return Triple(minY, maxY, scale)
}

private fun axisXScale(points: List<PointF>, plot: Plot): Triple<Float, Float, Float> {
    val steps = plot.xAxis.steps
    val minX = points.minOf { it.x }
    val maxX = points.maxOf { it.x }
    val stepRange = maxX - minX
    val stepInterval = stepRange / steps
    val scale = if (plot.xAxis.roundToInt) ceil(stepInterval) else stepInterval
    return Triple(minX, maxX, scale)
}

private fun isDragLocked(dragOffset: Float, offset: Offset, xOffset: Float): Boolean =
    dragOffset in (offset.x - xOffset / 2)..(offset.x + xOffset / 2)

/**
 * Line graph
 * A composable that draws a Line graph with the configurations provided by the [Plot]. The graph
 * can be scrolled, zoomed and touch dragged for selection. Every part of the line graph can be customized,
 * by changing the configuration in the [Plot].
 *
 * @param plot              the configuration to render the full graph
 * @param modifier          Modifier
 * @param onSelectionStart  invoked when the selection has started
 * @param onSelectionEnd    invoked when the selection has ended
 * @param onSelection       invoked when selection changes from one point to the next. You are provided
 * with the xOffset where the selection occurred in the graph and the [PointF]s that are selected. If there
 * are multiple lines, you will get multiple data points.
 */
@Composable
fun LineGraph(
    plot: Plot,
    modifier: Modifier = Modifier,
    onSelectionStart: () -> Unit = {},
    onSelectionEnd: () -> Unit = {},
    onSelection: ((Float, List<PointF>) -> Unit)? = null
) {
    val paddingTop = plot.paddingTop
    val paddingEnd = plot.paddingEnd
    val horizontalGap = plot.horizontalExtraSpace
    val isZoomAllowed = plot.isZoomAllowed

    val globalXScale = 1F
    val globalYScale = 1F

    val offset = remember { mutableStateOf(0F) }
    val maxScrollOffset = remember { mutableStateOf(0F) }
    val dragOffset = remember { mutableStateOf(0F) }
    val isDragging = remember { mutableStateOf(false) }
    val xScale = remember { mutableStateOf(globalXScale) }
    val rowHeight = remember { mutableStateOf(0f) }
    val columnWidth = remember { mutableStateOf(0f) }
    val backgroundColor = MaterialTheme.colors.surface

    val lines = plot.lines
    val xUnit = plot.xAxis.unit

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Box(modifier = modifier.clipToBounds()) {
            val points = lines.flatMap { it.points }
            val (minX, maxX, xAxisScale) = axisXScale(points = points, plot = plot)
            val (minY, maxY, yAxisScale) = axisYScale(points = points, plot = plot)
            Canvas(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxSize()
                    .background(backgroundColor)
                    .scrollable(
                        state = rememberScrollableState { delta ->
                            offset.value -= delta
                            if (offset.value < 0F) offset.value = 0F
                            if (offset.value > maxScrollOffset.value)
                                offset.value = maxScrollOffset.value
                            delta
                        },
                        orientation = Orientation.Horizontal,
                        enabled = true
                    )
                    .pointerInput(Unit, Unit) {
                        detectDragZoomGesture(
                            isZoomAllowed = isZoomAllowed,
                            isDragAllowed = plot.selection.enable,
                            detectDragTimeout = plot.selection.detectionTime,
                            onDragStart = { offset ->
                                dragOffset.value = offset.x
                                onSelectionStart.invoke()
                                isDragging.value = true
                            },
                            onDragEnd = {
                                isDragging.value = false
                                onSelectionEnd.invoke()
                            },
                            onZoom = { zoom ->
                                xScale.value *= zoom
                            }
                        ) { change, _ ->
                            dragOffset.value = change.position.x
                        }
                    },
                onDraw = {
                    val xStart = columnWidth.value + horizontalGap.toPx()
                    val yBottom = size.height - rowHeight.value
                    val xOffset = 20.dp.toPx() * xScale.value
                    val maxValueInYAxis = maxValueInYAxis(yAxisScale, plot.yAxis.steps)
                    val yOffset = (yBottom - paddingTop.toPx()) / maxValueInYAxis * globalYScale

                    val xLastPoint =
                        (maxX - minX) * xOffset / xUnit + xStart + paddingEnd.toPx() + horizontalGap.toPx()

                    maxScrollOffset.value =
                        if (xLastPoint > size.width) xLastPoint - size.width else 0F

                    val dragLocks = mutableMapOf<Line, Pair<PointF, Offset>>()

                    /*  draw grid lines  */
                    val top = yBottom - (maxY - minY) * yOffset
                    val region = Rect(xStart, top, size.width - paddingEnd.toPx(), yBottom)
                    plot.grid?.onDraw?.invoke(this, region, xOffset / xUnit, yOffset)

                    /*  draw lines and points and underline  */
                    lines.forEach { line ->
                        val intersection = line.intersection
                        val connection = line.connection
                        val underline = line.underline

                        /*  draw area under curve  */
                        underline?.let {
                            val offsets = line.points.map { (x, y) ->
                                val tx = (x - minX) * xOffset / xUnit + xStart - offset.value
                                val ty = yBottom - (y - minY) * yOffset
                                Offset(tx, ty)
                            }
                            val path = Path()
                            offsets.forEachIndexed { index, offset ->
                                if (index == 0) {
                                    path.moveTo(offset.x, yBottom)
                                }
                                path.lineTo(offset.x, offset.y)
                            }
                            val last = offsets.last()
                            val first = offsets.first()
                            path.lineTo(last.x, yBottom)
                            path.lineTo(first.x, yBottom)
                            underline.onDraw(this, path)
                        }

                        /*  dra lines and points  */
                        var curOffset: Offset? = null
                        var nextOffset: Offset? = null
                        for (i in line.points.indices) {
                            if (i == 0) {
                                val (x, y) = line.points[i]
                                val tx = (x - minX) * xOffset / xUnit + xStart - offset.value
                                val ty = yBottom - (y - minY) * yOffset
                                curOffset = Offset(tx, ty)
                            }
                            if (i < line.points.lastIndex) {
                                val (x, y) = line.points[i + 1]
                                val tx = (x - minX) * xOffset / xUnit + xStart - offset.value
                                val ty = yBottom - (y - minY) * yOffset
                                nextOffset = Offset(tx, ty)
                            }
                            if (nextOffset != null && curOffset != null) {
                                connection?.onDraw?.invoke(this, curOffset, nextOffset)
                            }
                            curOffset?.let {
                                if (isDragging.value && isDragLocked(
                                        dragOffset.value,
                                        it,
                                        xOffset
                                    )
                                ) {
                                    dragLocks[line] = line.points[i] to it
                                } else {
                                    intersection?.onDraw?.invoke(this, it, line.points[i])
                                }
                            }
                            curOffset = nextOffset
                        }


                    }
                    /*  draw column  */
                    drawRect(backgroundColor, Offset(0F, 0F), Size(columnWidth.value, size.height))

                    /*  draw padding end  */
                    drawRect(
                        backgroundColor,
                        Offset(size.width - paddingEnd.toPx(), 0F),
                        Size(paddingEnd.toPx(), size.height)
                    )

                    /*  draw drag selection highlight  */
                    if (isDragging.value) {
                        /*  draw drag line highlight  */
                        dragLocks.values.firstOrNull()?.let { (point, location) ->
                            val (x, _) = location
                            if (x >= columnWidth.value && x <= size.width - paddingEnd.toPx()) {
                                plot.selection.highlight?.onDraw?.invoke(
                                    this,
                                    Offset(x, yBottom),
                                    Offset(x, 0F)
                                )
                            }
                        }
                        /*  draw point highlight  */
                        dragLocks.entries.forEach { (line, lock) ->
                            val highlight = line.highlight
                            val (_, location) = lock
                            val x = location.x
                            if (x >= columnWidth.value && x <= size.width - paddingEnd.toPx()) {
                                highlight?.onDraw?.invoke(this, location)
                            }
                        }
                    }

                    /*  onSelection  */
                    if (isDragging.value) {
                        val x = dragLocks.values.firstOrNull()?.second?.x
                        if (x != null) {
                            onSelection?.invoke(x, dragLocks.values.map { it.first })
                        }
                    }
                },
            )

            GraphXAxis(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clip(RowClip(columnWidth.value, paddingEnd))
                    .onGloballyPositioned {
                        rowHeight.value = it.size.height.toFloat()
                    }
                    .padding(bottom = plot.xAxis.paddingBottom, top = plot.xAxis.paddingTop),
                xStart = columnWidth.value + horizontalGap.value * LocalDensity.current.density,
                offsetScroll = offset.value,
                scale = xScale.value * xAxisScale / xUnit,
                stepSize = plot.xAxis.stepSize
            ) {
                plot.xAxis.content.invoke(minX, xAxisScale, maxX)
            }

            GraphYAxis(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .fillMaxHeight()
                    .wrapContentWidth()
                    .onGloballyPositioned {
                        columnWidth.value = it.size.width.toFloat()
                    }
                    .padding(start = plot.yAxis.paddingStart, end = plot.yAxis.paddingEnd),
                paddingTop = paddingTop.value * LocalDensity.current.density,
                paddingBottom = rowHeight.value,
                scale = globalYScale,
            ) {
                plot.yAxis.content.invoke(minY, yAxisScale, maxY)
            }
        }
    }
}