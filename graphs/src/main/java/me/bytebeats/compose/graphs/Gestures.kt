package me.bytebeats.compose.graphs

import androidx.compose.foundation.gestures.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import kotlin.math.absoluteValue

/**
 * Created by bytebeats on 2021/11/29 : 20:24
 * E-mail: happychinapc@gmail.com
 * Quote: Peasant. Educated. Worker
 */

private fun PointerEvent.isPointerUp(pointerId: PointerId): Boolean =
    changes.firstOrNull { it.id == pointerId }?.pressed != true

private suspend fun PointerInputScope.awaitLongPressOrCancellation(
    initDown: PointerInputChange,
    timeout: Long
): PointerInputChange? {
    var longPress: PointerInputChange? = null
    var curDown = initDown
    return try {
        withTimeout(timeout) {// wait for first tap up or long press
            awaitPointerEventScope {
                var finished = false
                while (!finished) {
                    val event = awaitPointerEvent(PointerEventPass.Main)
                    if (event.changes.all { it.changedToUpIgnoreConsumed() }) {//all pointers are up
                        finished = true
                    }
                    if (event.changes.any { it.consumed.downChange || it.isOutOfBounds(size) }) {
                        finished = true// cancelled
                    }
                    /**
                     * check for cancel by position consumption.
                     * We can look on the Final pass of the existing pointer event
                     * because it comes after the Main pass we checked above
                     */
                    val consumeCheck = awaitPointerEvent(PointerEventPass.Final)
                    if (consumeCheck.changes.any { it.positionChangeConsumed() }) {
                        finished = true
                    }
                    if (!event.isPointerUp(curDown.id)) {
                        longPress = event.changes.firstOrNull { it.id == curDown.id }
                    } else {
                        val newPressed = event.changes.firstOrNull { it.pressed }
                        if (newPressed != null) {
                            curDown = newPressed
                            longPress = curDown
                        } else {// should technically never happen as we checked it above
                            finished = true
                        }
                    }
                }
            }

        }
        null
    } catch (ignore: TimeoutCancellationException) {
        longPress ?: initDown
    }
}

internal suspend fun PointerInputScope.detectDragZoomGesture(
    isZoomAllowed: Boolean = false,
    onZoom: (scale: Float) -> Unit,
    isDragAllowed: Boolean = true,
    detectDragTimeout: Long,
    onDragStart: (Offset) -> Unit = {},
    onDragEnd: () -> Unit = {},
    onDrag: (change: PointerInputChange, dragAmount: Offset) -> Unit,
) {
    if (isZoomAllowed || isDragAllowed) {
        forEachGesture {
            val down = awaitPointerEventScope { awaitFirstDown(requireUnconsumed = false) }
            awaitPointerEventScope {
                var scale = 1F
                var pastTouchSlop = false
                val touchSlop = viewConfiguration.touchSlop
                do {
                    val event = awaitPointerEvent()
                    val cancelled = event.changes.any { it.positionChangeConsumed() }
                    if (event.changes.size == 1) {
                        break
                    } else if (event.changes.size == 2) {
                        if (isZoomAllowed) {
                            if (!cancelled) {
                                val zoomChange = event.calculateZoom()
                                if (!pastTouchSlop) {
                                    scale *= zoomChange
                                    val centroidSize =
                                        event.calculateCentroidSize(useCurrent = false)
                                    val zoomMotion = (1 - scale).absoluteValue * centroidSize
                                    if (zoomMotion > touchSlop) {
                                        pastTouchSlop = true
                                    }
                                } else {
                                    if (zoomChange != 1F) {
                                        onZoom.invoke(zoomChange)
                                    }
                                    for (change in event.changes) {
                                        if (change.positionChanged()) {
                                            change.consumeAllChanges()
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        break
                    }
                } while (!cancelled && event.changes.any { it.pressed })
            }
            if (isDragAllowed) {
                try {
                    val drag = awaitLongPressOrCancellation(down, detectDragTimeout)
                    if (drag != null) {
                        onDragStart.invoke(drag.position)
                        awaitPointerEventScope {
                            if (drag(drag.id) {
                                    onDrag.invoke(it, it.positionChange())
                                    it.consumeDownChange()
                                }) {
                                currentEvent.changes.forEach {//consume up if we quit drag gracefully with the up
                                    if (it.changedToUp()) {
                                        it.consumeDownChange()
                                    }
                                }
                                onDragEnd.invoke()
                            } else {
                                onDragEnd.invoke()
                            }
                        }
                    }
                } catch (exp: CancellationException) {
                    onDragEnd.invoke()
                    throw exp
                }
            }
        }
    }
}