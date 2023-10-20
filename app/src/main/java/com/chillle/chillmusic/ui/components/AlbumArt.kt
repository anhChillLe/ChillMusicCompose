package com.chillle.chillmusic.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import com.chillle.chillmusic.ui.remember.rememberAlbumArt

@Composable
fun AlbumArtImage(data: ByteArray?, modifier: Modifier = Modifier) {
    val bitmap = rememberAlbumArt(data)
    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .aspectRatio(1F)
    )
}

@Composable
fun RotationAlbumArt(data: ByteArray?, modifier: Modifier = Modifier) {
    val bitmap = rememberAlbumArt(data)
    val infiniteTransition = rememberInfiniteTransition(label = "AlbumArt")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0F,
        targetValue = 360F,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing)
        ),
        label = "AlbumArt"
    )
    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .aspectRatio(1F)
            .rotate(angle)
    )
}