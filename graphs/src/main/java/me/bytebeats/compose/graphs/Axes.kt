package me.bytebeats.compose.graphs

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Dp

/**
 * Created by bytebeats on 2021/11/29 : 11:33
 * E-mail: happychinapc@gmail.com
 * Quote: Peasant. Educated. Worker
 */

/**
 * Graph y axis
 *
 * Composable that Layouts the child composables in the Y Axis.
 * This does the same thing as a Column composable, but with customisation that takes care of the scale.
 *
 * @param modifier      Modifier
 * @param paddingTop    the top padding
 * @param paddingBottom the bottom padding
 * @param scale         the scale in y axis
 * @param content       the composable that draws the item in the y axis
 * @receiver
 */
@Composable
internal fun GraphYAxis(
    modifier: Modifier,
    paddingTop: Float,
    paddingBottom: Float,
    scale: Float,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val steps = if (measurables.size <= 1) 1 else measurables.size - 1
        val placeables =
            measurables.map { measurable -> measurable.measure(constraints.copy(minHeight = 0)) }
        val width = placeables.maxOf { it.width }
        layout(width, constraints.maxHeight) {
            val yBottom = constraints.maxHeight - paddingBottom
            val availableHeight = yBottom - paddingTop
            var yPosition = yBottom.toInt()
            placeables.forEach { placeable ->
                yPosition -= placeable.height / 2
                placeable.place(x = 0, y = yPosition)
                yPosition -= (availableHeight / steps * scale).toInt() - placeable.height / 2
            }
        }
    }
}

/**
 * Graph x axis
 *
 * Composable that Layouts the child composables in the X Axis.
 * This does the same thing as a Row composable, but with customisation that takes care of the scale.
 *
 * @param modifier
 * @param xStart            the left position where the first child is laid out
 * @param offsetScroll      the offset value that varies based on the scroll
 * @param scale             the scale in x axis
 * @param stepSize          the distance between two adjacent data points
 * @param content           the composable that draws the item in the X axis
 * @receiver
 */
@Composable
internal fun GraphXAxis(
    modifier: Modifier,
    xStart: Float,
    offsetScroll: Float,
    scale: Float,
    stepSize: Dp,
    content: @Composable () -> Unit
) {
    Layout(content = content, modifier = modifier) { measurables, constraints ->
        val placeables =
            measurables.map { measurable -> measurable.measure(constraints.copy(minHeight = 0)) }
        val height = placeables.maxOf { it.height }
        layout(constraints.maxWidth, height) {
            var xPosition = (xStart - offsetScroll).toInt()
            val step = stepSize.toPx()
            placeables.forEach { placeable ->
                xPosition -= placeable.width / 2
                placeable.place(x = xPosition, y = 0)
                xPosition += (step * scale).toInt() + placeable.width / 2
            }
        }
    }
}