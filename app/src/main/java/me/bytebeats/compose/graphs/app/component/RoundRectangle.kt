package me.bytebeats.compose.graphs.app.component

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/**
 * Created by bytebeats on 2021/12/1 : 21:08
 * E-mail: happychinapc@gmail.com
 * Quote: Peasant. Educated. Worker
 */
object RoundRectangle : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val radius = 8.dp.value * density.density
        return Outline.Rounded(RoundRect(size.toRect(), CornerRadius(radius, radius)))
    }
}