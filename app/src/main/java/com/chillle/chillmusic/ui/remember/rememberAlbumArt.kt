package com.chillle.chillmusic.ui.remember

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import com.chillle.chillmusic.data.repository.DefaultResource
import com.chillle.chillmusic.ulti.toBitmap

@Composable
fun rememberAlbumArt(data: ByteArray?): Bitmap{
    val context = LocalContext.current

    val defaultAlbumArt = remember {
        DefaultResource.getInstance(context.resources).defaultAlbumArtBitmap
    }
    var albumArt by remember {
        mutableStateOf(defaultAlbumArt)
    }

    LaunchedEffect(data) {
        albumArt = data?.let {
            BitmapFactory.decodeByteArray(it, 0, it.size) ?: defaultAlbumArt
        } ?: defaultAlbumArt
    }

    return albumArt
}