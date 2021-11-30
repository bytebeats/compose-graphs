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
internal data class PointF(val x: Float, val y: Float)

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
internal data class XAxis(
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
internal data class YAxis(
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
 * High light
 * Represents a selected PointF
 * @property color
 * @property radius
 * @property alpha
 * @property style
 * @property colorFilter
 * @property blendMode
 * @property onDraw
 * @constructor Create empty High light
 */
internal data class HighLight(
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

internal data class Intersection(
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

internal data class Connection(
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

internal data class Selection(
    val enable: Boolean = true,
    val highLight: Connection? = Connection(
        Color.Red, strokeWidth = 2.dp,
        pathEffect = PathEffect.dashPathEffect(
            floatArrayOf(40F, 20F)
        ),
    ),
    val detectionTime: Long = 100L
)

internal data class AreaUnderLine(
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

internal data class Grid(
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

internal data class Line(
    val points: List<PointF>,
    val connection: Connection?,
    val intersection: Intersection?,
    val highLight: HighLight? = null,
    val areaUnderLine: AreaUnderLine? = null
)

internal data class Plot(
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