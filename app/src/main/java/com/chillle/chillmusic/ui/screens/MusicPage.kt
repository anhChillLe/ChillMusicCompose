package com.chillle.chillmusic.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.ui.PlayerView
import com.chillle.chillmusic.data.repository.DefaultResource
import com.chillle.chillmusic.models.Song
import com.chillle.chillmusic.ui.components.MusicList
import com.chillle.chillmusic.ui.providers.CurrentPlayer
import com.chillle.chillmusic.viewmodel.PlayerViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.stream.Collectors

@Composable
fun MusicPage(songs: Set<Song>) {
    val context = LocalContext.current
    var allowSelect by remember {
        mutableStateOf(false)
    }
    var listSelected by remember {
        mutableStateOf(setOf<Song>())
    }
    val player = CurrentPlayer.player
    val viewModel = viewModel<PlayerViewModel>()

    val defaultByteArray = remember {
        DefaultResource.getInstance(context.resources).defaultAlbumArtByteArray
    }

    val scope = rememberCoroutineScope()

    BackHandler(enabled = allowSelect) {
        if (allowSelect) {
            allowSelect = false
            listSelected = setOf()
        }
    }

    MusicList(
        songs = songs.toList(),
        currentItemId = viewModel.currentMediaItem?.mediaId.toString(),
        contentPadding = PaddingValues(
            top = 8.dp,
            start = 8.dp,
            end = 8.dp,
            bottom = if (viewModel.currentMediaItem == null) 8.dp else (64 + 4 + 8).dp // player size + player margin + 8.dp
        ),
        onItemLongPress = {
            allowSelect = true
            val newList = listSelected.toMutableSet()
            newList.add(it)
            listSelected = newList
        },
        onItemPress = { song, position ->
            scope.launch {
                player?.run {
                    val items = songs
                        .parallelStream()
                        .map { it.toMediaItem() }
                        .collect(Collectors.toList())
                    setMediaItems(items, position, 0)
                    prepare()
                    play()
                }
            }
        },
        allowSelect = allowSelect,
        listSelected = listSelected,
        onSelectedChanged = { isSelected, song ->
            val newList = listSelected.toMutableSet()
            if (isSelected) newList.add(song)
            else newList.remove(song)
            listSelected = newList
        },
        modifier = Modifier
            .fillMaxSize()
            .background(viewModel.style.backgroundColor),
        style = viewModel.style
    )
}