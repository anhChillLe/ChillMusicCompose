package com.chillle.chillmusic.activity

import android.content.ComponentName
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.chillle.chillmusic.data.repository.DefaultResource
import com.chillle.chillmusic.data.repository.MusicStore
import com.chillle.chillmusic.service.PlaybackService
import com.chillle.chillmusic.ui.App
import com.chillle.chillmusic.ulti.MusicStyle
import com.chillle.chillmusic.viewmodel.PlayerViewModel
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class MainActivity : ComponentActivity() {
    private lateinit var controllerFuture: ListenableFuture<MediaController>
    private var controller by mutableStateOf<MediaController?>(null)
    private val defaultSource by lazy { DefaultResource.getInstance(resources) }
    private val viewModel by viewModels<PlayerViewModel>()
    private val handler = Handler(Looper.getMainLooper())
    private val handlerTask = object : Runnable {
        override fun run() {
            if (controller?.isPlaying == true)
                viewModel.currentPosition = controller!!.currentPosition
            handler.postDelayed(this, 500)
        }
    }
    private val listener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            viewModel.isPlaying = isPlaying
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            super.onShuffleModeEnabledChanged(shuffleModeEnabled)
            viewModel.isRandom = shuffleModeEnabled
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)
            mediaItem ?: return
            val song = MusicStore.getSong(mediaItem.mediaId) ?: return
            val default = defaultSource.defaultAlbumArtBitmap

            viewModel.run {
                currentSong = song
                currentMediaItem = mediaItem
                style = MusicStyle.fromBitmap(song.id, song.thumbNail?.small ?: default)
            }
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            super.onRepeatModeChanged(repeatMode)
            viewModel.repeatMode = repeatMode
        }
    }

    private fun initViewModelState(controller: MediaController) {
        viewModel.run {
            controller.currentMediaItem?.let {
                currentMediaItem = it
                val song = MusicStore.getSong(it.mediaId) ?: return
                viewModel.currentSong = song
                val default = defaultSource.defaultAlbumArtBitmap
                style = MusicStyle.fromBitmap(song.id, song.thumbNail?.small ?: default)
                isPlaying = controller.isPlaying
            }
            isRandom = controller.shuffleModeEnabled
            repeatMode = controller.repeatMode
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModelStore.put("current_player", viewModel)
        setContent {
            App(controller)
        }
    }

    override fun onStart() {
        super.onStart()
        val token = SessionToken(this, ComponentName(this, PlaybackService::class.java))
        controllerFuture = MediaController.Builder(this, token).buildAsync()
        controllerFuture.addListener({
            controller = controllerFuture.get()
            controller?.let {
                initViewModelState(it)
                it.addListener(listener)
                handler.post(handlerTask)
            }
        }, MoreExecutors.directExecutor())
    }

    override fun onStop() {
        super.onStop()
        controller = null
        handler.removeCallbacks(handlerTask)
        MediaController.releaseFuture(controllerFuture)
    }
}