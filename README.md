# compose-graphs

[![GitHub latest commit](https://badgen.net/github/last-commit/bytebeats/compose-graphs)](https://github.com/bytebeats/compose-graphs/commit/)
[![GitHub contributors](https://img.shields.io/github/contributors/bytebeats/compose-graphs.svg)](https://github.com/bytebeats/compose-graphs/graphs/contributors/)
[![GitHub issues](https://img.shields.io/github/issues/bytebeats/compose-graphs.svg)](https://github.com/bytebeats/compose-graphs/issues/)
[![Open Source? Yes!](https://badgen.net/badge/Open%20Source%20%3F/Yes%21/blue?icon=github)](https://github.com/bytebeats/compose-graphs/)
[![GitHub forks](https://img.shields.io/github/forks/bytebeats/compose-graphs.svg?style=social&label=Fork&maxAge=2592000)](https://github.com/bytebeats/compose-graphs/network/)
[![GitHub stars](https://img.shields.io/github/stars/bytebeats/compose-graphs.svg?style=social&label=Star&maxAge=2592000)](https://github.com/bytebeats/compose-graphs/stargazers/)
[![GitHub watchers](https://img.shields.io/github/watchers/bytebeats/compose-graphs.svg?style=social&label=Watch&maxAge=2592000)](https://github.com/bytebeats/compose-graphs/watchers/)

Zoomable and Draggable Graphs based-on Jetpack Compose. Supports Android &amp; iOS, Web and Desktop.

## Features

- Full customization of the various parts of the graph (like the point, line between the points, highlight
  when selected, the values in x-axis and y-axis, etc...)
- Supports scrolling, zooming and touch drag selection

## How to use

Just add the `LineGraph` composable and pass it a `Plot` with all your configuration and customisation.
Please take a look at the [app](https://github.com/bytebeats/compose-graphs/tree/master/app) app to see the various
customisations available. Almost every aspect of the graph is customisable. You can even override the default
draw implementations and can draw a `Rectangle` instead of a `Circle`, etc. The below code renders the Orange
graph that you see in the above screenshots.

```
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
```

## Stargazers over time

[![Stargazers over time](https://starchart.cc/bytebeats/compose-graphs.svg)](https://starchart.cc/bytebeats/compose-graphs)

## Github Stars Sparklines

[![Sparkline](https://stars.medv.io/bytebeats/compose-graphs.svg)](https://stars.medv.io/bytebeats/compose-graphs)

## Contributors

[![Contributors over time](https://contributor-graph-api.apiseven.com/contributors-svg?chart=contributorOverTime&repo=bytebeats/compose-graphs)](https://www.apiseven.com/en/contributor-graph?chart=contributorOverTime&repo=bytebeats/compose-graphs)

## MIT License

    Copyright (c) 2021 Chen Pan

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.

