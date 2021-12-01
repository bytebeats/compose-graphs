package me.bytebeats.compose.graphs.app.line.model

import me.bytebeats.compose.graphs.line.PointF
import kotlin.random.Random


/**
 * Created by bytebeats on 2021/12/1 : 19:24
 * E-mail: happychinapc@gmail.com
 * Quote: Peasant. Educated. Worker
 */

private fun generatePoints(
    offset: Float,
    step: Float,
    count: Int,
    startY: Float,
    endY: Float
): List<PointF> {
    val points = mutableListOf<PointF>()
    for (i in 0 until count) {
        val x = offset + step * i
        val y = Random.Default.nextDouble(startY.toDouble(), endY.toDouble()).toFloat()
        points.add(PointF(x, y))
    }
    return points
}

val points1 = generatePoints(0F, 1F, 24, -100F, 200F)
val points2 = generatePoints(0F, 1F, 24, 0F, 100F)
val points3 = generatePoints(13F, 1F, 10, 0F, 7F)
val points4 = generatePoints(-0.6F, .1F, 24, -1F, 2F)