package com.chillle.chillmusic.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelStoreOwner
import androidx.media3.session.MediaController
import com.chillle.chillmusic.ui.components.SystemBar
import com.chillle.chillmusic.ui.providers.MusicPlayerProvider
import com.chillle.chillmusic.ui.screens.MainTab
import com.chillle.chillmusic.ui.screens.MusicPlayer
import com.chillle.chillmusic.ui.theme.AppTheme

@Composable
fun App(controller: MediaController?) {
    AppTheme {
        MusicPlayerProvider(controller) {
            Box(modifier = Modifier.fillMaxSize()) {
                SystemBar()
                MainTab(Modifier.fillMaxSize())
                MusicPlayer(Modifier.fillMaxSize())
            }
        }
    }
}