package me.bytebeats.compose.graphs.app.line

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.bytebeats.compose.graphs.app.line.model.points1
import me.bytebeats.compose.graphs.app.line.model.points2
import me.bytebeats.compose.graphs.app.ui.theme.ComposeGraphsTheme
import me.bytebeats.compose.graphs.app.ui.theme.Green900
import me.bytebeats.compose.graphs.app.ui.theme.LightGreen600
import me.bytebeats.compose.graphs.line.LineGraph
import me.bytebeats.compose.graphs.line.Plot
import me.bytebeats.compose.graphs.line.PointF
import java.text.DecimalFormat

/**
 * Created by bytebeats on 2021/12/1 : 19:24
 * E-mail: happychinapc@gmail.com
 * Quote: Peasant. Educated. Worker
 */

@Composable
fun LineGraph1(lines: List<List<PointF>>) {
    LineGraph(
        plot = Plot(
            lines = listOf(
                Plot.Line(
                    points = lines[0],
                    connection = Plot.Connection(LightGreen600, 2.dp),
                    intersection = Plot.Intersection(LightGreen600, 5.dp),
                    highlight = Plot.Highlight(Green900, 5.dp),
                    underline = Plot.Underline(LightGreen600, 0.3F)
                ),
                Plot.Line(
                    points = lines[1], connection = Plot.Connection(Color.LightGray, 2.dp),
                    intersection = Plot.Intersection { center, point ->
                        val px = 2.dp.toPx()
                        val topStart = Offset(center.x - px, center.y - px)
                        drawRect(Color.LightGray, topLeft = topStart, Size(px * 2, px * 2))
                    },
                ),
            ),
            selection = Plot.Selection(
                highlight = Plot.Connection(
                    Green900, strokeWidth = 2.dp, pathEffect = PathEffect.dashPathEffect(
                        floatArrayOf(40F, 20F)
                    )
                )
            ),
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LineGraphPreview() {
    ComposeGraphsTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            LineGraph1(lines = listOf(points1, points2))
            LineGraph2(lines = listOf(points1, points2))
            LineGraph3(lines = listOf(points1))
        }
    }
}

@Composable
fun LineGraph2(lines: List<List<PointF>>) {
    LineGraph(
        plot = Plot(
            lines = listOf(
                Plot.Line(
                    points = lines[1],
                    connection = Plot.Connection(Color.Cyan, 2.dp),
                    intersection = null,
                    highlight = Plot.Highlight { center ->
                        val color = Color.Gray
                        drawCircle(color = color, 9.dp.toPx(), center = center, alpha = 0.3F)
                        drawCircle(color = color, 6.dp.toPx(), center = center)
                        drawCircle(Color.White, 3.dp.toPx(), center = center)
                    }
                ),
                Plot.Line(
                    points = lines[0],
                    connection = Plot.Connection(Color.Blue, 3.dp),
                    intersection = Plot.Intersection(
                        Color.Blue, 6.dp
                    ) { center, point ->
                        val x = point.x
                        val radius = if (x.rem(4f) == 0F) 6.dp else 3.dp
                        drawCircle(color = Color.Blue, radius = radius.toPx(), center = center)
                    },
                    highlight = Plot.Highlight { center ->
                        val color = Color.Blue
                        drawCircle(color = color, 9.dp.toPx(), center = center, alpha = .3F)
                        drawCircle(color = color, 6.dp.toPx(), center = center)
                        drawCircle(Color.White, 3.dp.toPx(), center = center)
                    },
                    underline = Plot.Underline(Color.Blue, .1F)
                ),
            ),
            grid = Plot.Grid(Color.LightGray),
            paddingEnd = 15.dp,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
    )
}

@Composable
fun LineGraph3(lines: List<List<PointF>>) {
    LineGraph(
        plot = Plot(
            lines = listOf(
                Plot.Line(
                    points = lines[0],
                    connection = Plot.Connection(Color.Blue, 2.dp),
                    intersection = Plot.Intersection(Color.Blue, 4.dp),
                    highlight = Plot.Highlight(Color.Red, 6.dp),
                    underline = Plot.Underline(Color.Blue, .1F)
                ),
            ),
            grid = Plot.Grid(Color.LightGray.copy(0.5F)),
            xAxis = Plot.XAxis(steps = 24) { min, offset, max ->
                for (i in 0 until 24) {
                    val value = min + i * offset
                    Column {
                        val isMajor = value.rem(4) == 0F
                        val radius = if (isMajor) 6F else 3F
                        val color = MaterialTheme.colors.onSurface
                        Canvas(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .height(20.dp),
                            onDraw = {
                                drawCircle(
                                    color = color,
                                    radius = radius * density,
                                    Offset(0f, 10F * density)
                                )
                            }
                        )
                        if (isMajor) {
                            Text(
                                text = DecimalFormat("#.#").format(value),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.caption,
                                color = color
                            )
                        }
                    }
                    if (value > max) {
                        break
                    }
                }
            },
            paddingEnd = 10.dp,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
    )
}