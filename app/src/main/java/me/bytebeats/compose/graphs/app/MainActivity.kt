package me.bytebeats.compose.graphs.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.bytebeats.compose.graphs.app.line.*
import me.bytebeats.compose.graphs.app.ui.theme.ComposeGraphsTheme
import me.bytebeats.compose.graphs.app.viewmodel.MainViewModelImpl

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModelImpl by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeGraphsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    LineGraphs(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun LineGraphs(viewModel: MainViewModelImpl) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        itemsIndexed(viewModel.lines.value) { index, lines ->
            when (index) {
                0 -> LineGraph2(lines = lines)
                1 -> Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 15.dp)
                ) {
                    LineGraph4(
                        lines = lines,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                2 -> LineGraph1(lines = lines)
                3 -> LineGraph3(lines = lines)
                4 -> LineGraph5(lines = lines)
            }
        }
    }
}