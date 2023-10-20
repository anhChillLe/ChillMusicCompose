package com.chillle.chillmusic.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.chillle.chillmusic.models.Song
import com.chillle.chillmusic.ulti.MusicStyle

class PlayerViewModel(application: Application) : AndroidViewModel(application) {
    var currentMediaItem by mutableStateOf<MediaItem?>(null)
    var currentSong by mutableStateOf<Song?>(null)
    var style by mutableStateOf(MusicStyle())
    var isPlaying by mutableStateOf(false)
    var currentPosition by mutableLongStateOf(0L)
    var isRandom by mutableStateOf(false)
    var repeatMode by mutableIntStateOf(0)
}