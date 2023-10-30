package io.oxozon.annalisa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.oxozon.annalisa.ui.theme.AnnalisaTheme
import io.oxozon.annalisa.viewmodel.PlayerViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AnnalisaTheme {
                PlayerScreen()
            }
        }
    }
}

@Composable
fun PlayerScreen() {
    val viewModel = PlayerViewModel()

    val currentTitle by viewModel.currentTitle.collectAsState()
    val playerDuration by viewModel.currentTime.observeAsState(0f)
    val currentTime by viewModel.currentTime.observeAsState(0f)
    val isPlaying by viewModel.isPlaying.observeAsState(initial = false)

    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = currentTitle, style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(24.dp))

            if (isPlaying) {
                IconButton(onClick = { viewModel.pause() }) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = null)
                }
            } else {
                IconButton(onClick = { viewModel.play() }) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (playerDuration > 0f) {
                Slider(
                    value = currentTime,
                    onValueChange = {
                        // You might want to introduce some kind of debouncing here so seeking doesn't happen too frequently
                        viewModel.seekTo(it)
                    },
                    valueRange = 0f..playerDuration,
                    steps = 1000 // or adjust as necessary
                )
            }
        }
    }
}