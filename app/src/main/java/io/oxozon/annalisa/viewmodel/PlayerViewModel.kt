package io.oxozon.annalisa.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PlayerViewModel : ViewModel() {

    private val mediaPlayer: MediaPlayer = MediaPlayer()
    private val _currentTitle = MutableStateFlow("Lo estamos pasando muy bien")
    private val _currentTime = MutableLiveData(0f)
    private val _playerDuration = MutableLiveData(0f)
    private val _isPlaying = MutableLiveData(false)
    private val _isBuffering = MutableLiveData(true)

    val currentTitle: StateFlow<String> = _currentTitle
    val currentTime: LiveData<Float> get() = _currentTime
    val playerDuration: LiveData<Float> get() = _playerDuration
    val isPlaying: LiveData<Boolean> get() = _isPlaying
    val isBuffering: LiveData<Boolean> get() = _isBuffering

    init {
        setupMediaPlayer()
    }

    private fun setupMediaPlayer() {
        mediaPlayer.apply {
            setOnPreparedListener {
                _playerDuration.postValue(mediaPlayer.duration.toFloat())
                _isBuffering.postValue(false)
            }
            setOnCompletionListener {
                _isPlaying.postValue(false)
            }
            setDataSource("https://cdn.oxozon.io/prisioneros.mp3")
            prepareAsync() // Asynchronous preparation to avoid blocking main thread
        }
    }

    fun play() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
            _isPlaying.postValue(true)
            startProgressUpdater()
        }
    }

    fun pause() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            _isPlaying.postValue(false)
        }
    }

    fun onProgressChanged(progress: Float) {
        val seekPosition = (progress * mediaPlayer.duration).toInt()
        mediaPlayer.seekTo(seekPosition)
    }

    private fun startProgressUpdater() {
        val updateIntervalMs: Long = 1000 // Update every second

        val runnable = Runnable {
            if (mediaPlayer.isPlaying) {
                _currentTime.postValue(mediaPlayer.currentPosition.toFloat())
                _currentTime.postValue(_currentTime.value?.plus(updateIntervalMs))
                _playerDuration.postValue(mediaPlayer.duration.toFloat())
            }
        }

        // It's important to run this in another thread or a repeating coroutine to avoid blocking the main thread.
        // For simplicity, we're using a basic thread in this example.
        Thread {
            while (mediaPlayer.isPlaying) {
                try {
                    Thread.sleep(updateIntervalMs)
                    runnable.run()
                } catch (e: InterruptedException) {
                    // Handle the interruption
                }
            }
        }.start()
    }

    fun seekTo(position: Float) {
        val positionInMs = (position * 1000).toInt() // convert seconds to milliseconds
        mediaPlayer.seekTo(positionInMs)
    }


    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
    }
}
