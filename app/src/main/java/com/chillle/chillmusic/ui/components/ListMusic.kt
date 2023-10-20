package com.chillle.chillmusic.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import com.chillle.chillmusic.models.Song
import com.chillle.chillmusic.ulti.MusicStyle

@Composable
fun MusicList(
    modifier: Modifier = Modifier,
    songs: List<Song>,
    currentItemId: String? = null,
    style: MusicStyle = MusicStyle(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onItemPress: (Song, Int) -> Unit = { _, _ -> },
    onItemLongPress: (Song) -> Unit = {},
    allowSelect: Boolean = false,
    listSelected: Set<Song> = setOf(),
    onSelectedChanged: (Boolean, Song) -> Unit = { _, _ -> }
) {
    LazyColumn(
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        itemsIndexed(
            items = songs,
            key = { _, item -> item.id }
        ) { index, item ->
            MusicItem(
                song = item,
                style = style,
                isCurrent = item.id == currentItemId,
                onPress = {
                    if (allowSelect)
                        onSelectedChanged(!listSelected.contains(item), item)
                    else
                        onItemPress(item, index)
                },
                onLongPress = { if (!allowSelect) onItemLongPress(item) },
                selectable = allowSelect,
                isSelected = listSelected.contains(item),
                onSelectedChanged = {
                    onSelectedChanged(it, item)
                }
            )
        }
    }
}