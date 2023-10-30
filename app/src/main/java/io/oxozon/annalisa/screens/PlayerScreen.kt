package io.oxozon.annalisa.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.oxozon.annalisa.viewmodel.PlayerViewModel

@Composable
fun PlayerScreen() {
    val viewModel = PlayerViewModel()

    val currentTitle by viewModel.currentTitle.collectAsState()
    val currentTime by viewModel.currentTime.observeAsState(0f)
    val duration by viewModel.playerDuration.observeAsState(0f)
    val isPlaying by viewModel.isPlaying.observeAsState(initial = false)
    val isBuffering by viewModel.isBuffering.observeAsState(false)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = currentTitle)

            Spacer(modifier = Modifier.height(16.dp))

            if (isBuffering) {
                CircularProgressIndicator()
            } else {
                PlaybackControls(
                    isPlaying = isPlaying,
                    onPlayPauseClicked = {
                        if (isPlaying) viewModel.pause() else viewModel.play()
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            PlaybackProgressSlider(
                progress = currentTime / duration,
                onValueChanged = { newProgress ->
                    viewModel.onProgressChanged(newProgress)
                }
            )
        }
    }
}

@Composable
fun PlaybackControls(
    isPlaying: Boolean,
    onPlayPauseClicked: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            imageVector = if (isPlaying) Icons.Filled.Close else Icons.Filled.PlayArrow,
            contentDescription = if (isPlaying) "Pause" else "Play",
            modifier = Modifier
                .size(50.dp)
                .clickable { onPlayPauseClicked() }
                .background(Color.Gray)
        )
    }
}

@Composable
fun PlaybackProgressSlider(
    progress: Float,
    onValueChanged: (Float) -> Unit
) {
    Slider(
        value = progress,
        onValueChange = onValueChanged,
        valueRange = 0f..1f,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}
