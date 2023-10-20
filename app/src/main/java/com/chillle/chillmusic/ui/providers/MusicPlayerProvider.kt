package com.chillle.chillmusic.ui.providers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.media3.session.MediaController

@Composable
fun MusicPlayerProvider(
    controller: MediaController?,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        localControllerProvider provides controller,
        content = content
    )
}

private val localControllerProvider = compositionLocalOf<MediaController?> { null }

object CurrentPlayer {
    val player: MediaController?
        @Composable
        get() = localControllerProvider.current
}

