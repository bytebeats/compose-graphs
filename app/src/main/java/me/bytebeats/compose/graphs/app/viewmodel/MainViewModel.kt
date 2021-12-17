package me.bytebeats.compose.graphs.app.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import me.bytebeats.compose.graphs.app.line.model.points1
import me.bytebeats.compose.graphs.app.line.model.points2
import me.bytebeats.compose.graphs.app.line.model.points3
import me.bytebeats.compose.graphs.app.line.model.points4
import me.bytebeats.compose.graphs.line.PointF
import kotlin.random.Random

/**
 * Created by bytebeats on 2021/12/2 : 11:03
 * E-mail: happychinapc@gmail.com
 * Quote: Peasant. Educated. Worker
 */

interface MainViewModel {
    val line: MutableState<List<PointF>>
    val lines: State<List<List<List<PointF>>>>

    fun change()
}

class MainViewModelImpl : ViewModel(), MainViewModel {
    override val line: MutableState<List<PointF>>
        get() = mutableStateOf(points1)

    override val lines: State<List<List<List<PointF>>>>
        get() = mutableStateOf(
            listOf(
                listOf(points1, points2),
                listOf(points2, points1),
                listOf(points1, points2),
                listOf(points1, points2),
                listOf(points4, points3),
            )
        )

    override fun change() {
        val nextX = Random.Default.nextInt(5) + 1
        val nextY = Random.Default.nextInt(5) + 1
        line.value = points1.map { PointF(it.x * nextX, it.y * nextY) }
    }
}