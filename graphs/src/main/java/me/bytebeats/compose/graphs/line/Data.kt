package me.bytebeats.compose.graphs.line

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.text.DecimalFormat

/**
 * Created by bytebeats on 2021/11/30 : 12:06
 * E-mail: happychinapc@gmail.com
 * Quote: Peasant. Educated. Worker
 */


private fun Float.string() = DecimalFormat("#.#").format(this)

/**
 * represents a point in the graph
 * @property x          the x coordinate or the number in the x axis
 * @property y          the y coordinate or the number in the y axis
 * @constructor Create a point
 */
data class PointF(val x: Float, val y: Float)

/**
 * X axis
 *
 * Configuration of the X Axis
 *
 * @property stepSize       the distance between two adjacent data points
 * @property steps          the number of values to be drawn in the axis
 * @property unit           Represent the range of values in the x axis. For example if this is 1, then the values in x axis would be (0, 1, 2, 3, ..., steps-1). If this is 0.1, then the values in x axis would be (0, 0.1, 0.2, 0.3, ...)
 * @property paddingTop     the top padding of the X axis
 * @property paddingBottom  the bottom padding of the X axis
 * @property roundToInt     if true, the values is X axis are represented by Integers. If false, the values could be decimal values, with 1 decimal precision in the default implementation
 * @property content        A composable where you could provide how the values should be rendered. The default implementation is to show a [Text] composable. You are provided with the min value in x axis, the offset between two x coordinates and the max value in x axis
 */
data class XAxis(
    val stepSize: Dp = 20.dp,
    val steps: Int = 10,
    val unit: Float = 1F,
    val paddingTop: Dp = 8.dp,
    val paddingBottom: Dp = 8.dp,
    val roundToInt: Boolean = true,
    val content: @Composable (Float, Float, Float) -> Unit = { min, offset, max ->
        for (step in 0 until steps) {
            val value = step * offset + min
            Text(
                text = value.string(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface,
            )
            if (value > max) {
                break
            }
        }
    }
)

/**
 * Y axis
 *
 * Configuration of the Y Axis
 *
 * @property steps          the number of values to be drawn in the axis
 * @property roundToInt     if true, the values is Y axis are represented by Integers. If false, the values could be decimal values, with 1 decimal precision in the default implementation
 * @property paddingStart   the start padding of the Y axis
 * @property paddingEnd     the end padding of the Y axis
 * @property content        A composable where you could provide how the values should be rendered. The default
 * implementation is to show a [Text] composable. You are provided with the min value in y axis, the offset
 * between two y coordinates and the max value in y axis
 */
data class YAxis(
    val steps: Int = 5,
    val roundToInt: Boolean = true,
    val paddingStart: Dp = 16.dp,
    val paddingEnd: Dp = 8.dp,
    val content: @Composable (Float, Float, Float) -> Unit = { min, offset, _ ->
        for (step in 0 until steps) {
            val value = step * offset + min
            Text(
                text = value.string(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface,
            )

        }
    }
)

/**
 * Highlight
 * Represents a selected PointF
 * @property color          The color or fill to be applied to the circle
 * @property radius         The radius of the circle
 * @property alpha          Opacity to be applied to the circle from 0.0f to 1.0f representing fully transparent to fully opaque respectively
 * @property style          Whether the circle is stroked or filled in
 * @property colorFilter    ColorFilter to apply to the [color] when drawn into the destination
 * @property blendMode      Blending algorithm to be applied to the brush
 * @property onDraw         override this to change the default drawCircle implementation. You are provided
 * with the 'center' [Offset]
 */
data class Highlight(
    val color: Color = Color.Black,
    val radius: Dp = 6.dp,
    val alpha: Float = .1F,
    val style: DrawStyle = Fill,
    val colorFilter: ColorFilter? = null,
    val blendMode: BlendMode = DrawScope.DefaultBlendMode,
    val onDraw: DrawScope.(Offset) -> Unit = { center ->
        drawCircle(color, radius.toPx(), center, alpha, style, colorFilter, blendMode)
    }
)

/**
 * Intersection Represents a pointF on the graph
 *
 * @property color          The color or fill to be applied to the circle
 * @property radius         The radius of the circle
 * @property alpha          Opacity to be applied to the circle from 0.0f to 1.0f representing fully transparent to fully opaque respectively
 * @property style          Whether the circle is stroked or filled in
 * @property colorFilter    ColorFilter to apply to the [color] when drawn into the destination
 * @property blendMode      Blending algorithm to be applied to the brush
 * @property onDraw         override this to change the default drawCircle implementation. You are provided
 * with the 'center' [Offset] and the actual [DataPoint] that represents the intersection.
 */
data class Intersection(
    val color: Color = Color.Blue,
    val radius: Dp = 6.dp,
    val alpha: Float = .1F,
    val style: DrawStyle = Fill,
    val colorFilter: ColorFilter? = null,
    val blendMode: BlendMode = DrawScope.DefaultBlendMode,
    val onDraw: DrawScope.(Offset, PointF) -> Unit = { center, _ ->
        drawCircle(color, radius.toPx(), center, alpha, style, colorFilter, blendMode)
    }
)

/**
 * Connection
 * Represents a line between two pointFs
 *
 * @property color          the color to be applied to the line
 * @property strokeWidth    The stroke width to apply to the line
 * @property strokeCap      treatment applied to the ends of the line segment
 * @property pathEffect     optional effect or pattern to apply to the line
 * @property alpha          opacity to be applied to the [color] from 0.0f to 1.0f representing
 * fully transparent to fully opaque respectively
 * @property colorFilter    ColorFilter to apply to the [color] when drawn into the destination
 * @property blendMode      the blending algorithm to apply to the [color]
 * @property onDraw         override this to change the default drawLine implementation. You are provided with
 * the 'start' [Offset] and 'end' [Offset]
 */
data class Connection(
    val color: Color = Color.Blue,
    val strokeWidth: Dp = 3.dp,
    val strokeCap: StrokeCap = Stroke.DefaultCap,
    val pathEffect: PathEffect? = null,
    val alpha: Float = 1F,
    val colorFilter: ColorFilter? = null,
    val blendMode: BlendMode = DrawScope.DefaultBlendMode,
    val onDraw: DrawScope.(Offset, Offset) -> Unit = { start, end ->
        drawLine(
            color,
            start,
            end,
            strokeWidth.toPx(),
            strokeCap,
            pathEffect,
            alpha,
            colorFilter,
            blendMode
        )
    }
)

/**
 * Selection
 * Configuration for the selection operation
 *
 * @property enable         if true, you can touch and drag to select the points. The point currently selected
 * is exposed via the [onSelection] param in the [LineGraph]. If false, the drag gesture is disabled.
 * @property highlight      controls how the selection is represented in the graph. The default implementation
 * is a vertical dashed line. You can override this by supplying your own [Connection]
 * @property detectionTime  the time taken for the touch to be recognised as a drag gesture
 */
data class Selection(
    val enable: Boolean = true,
    val highlight: Connection? = Connection(
        Color.Red, strokeWidth = 2.dp,
        pathEffect = PathEffect.dashPathEffect(
            floatArrayOf(40F, 20F)
        ),
    ),
    val detectionTime: Long = 100L
)

/**
 * Underline Controls the drawing behaviour of the area under the line. This is the region formed by intersection
 * of the Line, x-axis and y-axis.
 *
 * @property color          Color to be applied to the path
 * @property alpha          Opacity to be applied to the path from 0.0f to 1.0f representing
 * fully transparent to fully opaque respectively
 * @property style          Whether the path is stroked or filled in
 * @property colorFilter    ColorFilter to apply to the [color] when drawn into the destination
 * @property blendMode      Blending algorithm to be applied to the path when it is drawn
 * @property onDraw         override this to change the default drawPath implementation. You are provided with
 * the [Path] of the line
 */
data class Underline(
    val color: Color = Color.Blue,
    val alpha: Float = .1F,
    val style: DrawStyle = Fill,
    val colorFilter: ColorFilter? = null,
    val blendMode: BlendMode = DrawScope.DefaultBlendMode,
    val onDraw: DrawScope.(Path) -> Unit = { path ->
        drawPath(
            path,
            color,
            alpha,
            style,
            colorFilter,
            blendMode
        )
    }
)

/**
 * Grid
 * Controls how the grid is drawn on the Graph
 * @property color          the color to be applied
 * @property steps          the number of lines drawn in the grid. The default implementation considers this
 * as the horizontal lines
 * @property lineWidth      the width of the lines
 * @property onDraw         override this to change the default drawLine implementation (which is to draw multiple
 * horizontal lines based on the number of [steps]. You are provided with the [Rect] region available
 * to draw the grid, xOffset (the gap between two points in the x-axis) and the yOffset.
 */
data class Grid(
    val color: Color,
    val steps: Int = 5,
    val lineWidth: Dp = 1.dp,
    val onDraw: DrawScope.(Rect, Float, Float) -> Unit = { region, _, _ ->
        val (start, top, end, bottom) = region
        val availableHeight = bottom - top
        val offset = availableHeight / (if (steps > 1) steps - 1 else 1)
        for (step in 0 until steps) {
            val y = bottom - step * offset
            drawLine(color, Offset(start, y), Offset(end, y), lineWidth.toPx())
        }
    }
)

/**
 * Line
 * Represent a Line in the [LineGraph]
 *
 * @property points         list of points in the line. Note that this list should be sorted by x coordinate
 * from decreasing to increasing value, so that the graph can be drawn properly.
 * @property connection     drawing logic for the line between two adjacent points. If null, no line is drawn.
 * @property intersection   drawing logic to draw the point itself. If null, the point is not drawn.
 * @property highlight      drawing logic to draw the highlight at the point when it is selected. If null, the point
 * won't be highlighted on selection
 * @property underline      drawing logic for the area under the line. This is the region that is formed by the
 * intersection of the line, x-axis and y-axis.
 */
data class Line(
    val points: List<PointF>,
    val connection: Connection?,
    val intersection: Intersection?,
    val highlight: Highlight? = null,
    val underline: Underline? = null
)

/**
 * Plot
 * The configuration for the [LineGraph]
 *
 * @property lines  list of lines to be represented
 * @property grid   rendering logic on how the [Grid] should be drawn. If null, no grid is drawn.
 * @property selection  controls the touch and drag selection behaviour using [Selection]
 * @property xAxis  controls the behaviour, scale and drawing logic of the X Axis
 * @property yAxis  controls the behaviour, scale and drawing logic of the Y Axis
 * @property isZoomAllowed  if true, the graph will zoom on pinch zoom. If false, no zoom action.
 * @property paddingTop adjusts the top padding of the graph. If you want to adjust the bottom padding, adjust
 * the [XAxis.paddingBottom]
 * @property paddingEnd adjust the right padding of the graph. If you want to adjust the left padding, adjust
 * the [YAxis.paddingStart]
 * @property horizontalExtraSpace   gives extra space to draw [Intersection] or [Highlight] at the left and right
 * extremes of the graph. Adjust this if your graph looks like cropped at the left edge or the right edge.
 */
data class Plot(
    val lines: List<Line>,
    val grid: Grid? = null,
    val selection: Selection = Selection(),
    val xAxis: XAxis = XAxis(),
    val yAxis: YAxis = YAxis(),
    val isZoomAllowed: Boolean = true,
    val paddingTop: Dp = 16.dp,
    val paddingEnd: Dp = 0.dp,
    val horizontalExtraSpace: Dp = 6.dp
)