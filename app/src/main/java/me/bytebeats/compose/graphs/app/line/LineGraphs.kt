package me.bytebeats.compose.graphs.app.line

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.bytebeats.compose.graphs.app.component.RoundRectangle
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

@Composable
fun LineGraph4(lines: List<List<PointF>>, modifier: Modifier) {
    var totalWidth by remember {
        mutableStateOf(0)
    }
    Column(Modifier.onGloballyPositioned {
        totalWidth = it.size.width
    }) {
        var xOffset by remember {
            mutableStateOf(0F)
        }
        var cardWidth by remember {
            mutableStateOf(0)
        }
        var visible by remember {
            mutableStateOf(false)
        }
        var points by remember {
            mutableStateOf(listOf<PointF>())
        }
        val density = LocalDensity.current

        Box(modifier = Modifier.width(180.dp)) {
            if (visible) {
                Surface(
                    modifier = Modifier
                        .width(200.dp)
                        .align(Alignment.BottomCenter)
                        .onGloballyPositioned {
                            cardWidth = it.size.width
                        }
                        .graphicsLayer(translationX = xOffset)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 10.dp)) {
                        if (points.isNotEmpty()) {
                            val (x, y) = points[0]
                            Text(
                                text = "Score at $x:00 hrs",
                                modifier = Modifier.padding(vertical = 10.dp),
                                style = MaterialTheme.typography.subtitle1,
                                color = Color.Gray
                            )
                            ScoreRow(title = "Today", value = points[1].y, color = Color.Blue)
                            ScoreRow(title = "Yesterday", value = y, color = Color.Gray)
                        }
                    }
                }
            }
        }

        val padding = 16.dp

        MaterialTheme(colors = MaterialTheme.colors.copy(surface = Color.White)) {
            LineGraph(
                plot = Plot(
                    listOf(
                        Plot.Line(
                            points = lines[1],
                            connection = Plot.Connection(Color.Gray, 2.dp),
                            intersection = null,
                            highlight = Plot.Highlight { center ->
                                val color = Color.Gray
                                drawCircle(color = color, 9.dp.toPx(), center = center, alpha = .3F)
                                drawCircle(color = color, 6.dp.toPx(), center = center)
                                drawCircle(color = Color.White, 3.dp.toPx(), center = center)
                            }
                        ),
                        Plot.Line(
                            points = lines[0],
                            connection = Plot.Connection(),
                            intersection = Plot.Intersection(),
                            highlight = Plot.Highlight { center ->
                                val color = Color.Blue
                                drawCircle(color = color, 9.dp.toPx(), center = center, alpha = .3F)
                                drawCircle(color = color, 6.dp.toPx(), center = center)
                                drawCircle(color = Color.White, 3.dp.toPx(), center = center)
                            },
                            underline = Plot.Underline(),
                        ),
                    ),
                ),
                modifier = modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(horizontal = padding),
                onSelectionStart = { visible = true },
                onSelectionEnd = { visible = false }
            ) { x, pts ->
                val w = cardWidth.toFloat()
                var center = x + padding.value * density.density
                center = when {
                    center + w / 2 > totalWidth -> totalWidth - w
                    center - w / 2 < 0F -> 0f
                    else -> center - w / 2
                }
                xOffset = center
                points = pts
            }
        }
    }
}

@Composable
private fun ScoreRow(title: String, value: Float, color: Color) {
    val formatted = DecimalFormat("#.#").format(value)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
    ) {
        Row(modifier = Modifier.align(Alignment.CenterStart)) {
            Image(
                painter = ColorPainter(color = color),
                contentDescription = "Line Color",
                modifier = Modifier
                    .align(
                        Alignment.CenterVertically
                    )
                    .padding(end = 4.dp)
                    .size(10.dp)
                    .clip(RoundRectangle)
            )
            Text(text = title, style = MaterialTheme.typography.subtitle1, color = Color.DarkGray)
        }
        Text(
            text = formatted,
            modifier = Modifier
                .padding(end = 10.dp)
                .align(Alignment.CenterEnd),
            style = MaterialTheme.typography.subtitle2,
            color = Color.DarkGray
        )
    }
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
//            LineGraph2(lines = listOf(points1, points2))
//            LineGraph3(lines = listOf(points1))
            LineGraph4(lines = listOf(points1, points2), Modifier)
        }
    }
}