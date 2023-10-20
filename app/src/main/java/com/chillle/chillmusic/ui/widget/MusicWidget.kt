package com.chillle.chillmusic.ui.widget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionStartService
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.chillle.chillmusic.R
import com.chillle.chillmusic.service.PlaybackService

class MusicWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode get() = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            MusicPlayerWidget(
                isPlaying = true,
                title = "Test",
                artist = "Test",
                album = "Test",
            )
        }
    }
}


@Composable
fun MusicPlayerWidget(
    isPlaying: Boolean,
    albumArt: Bitmap? = null,
    title: String,
    artist: String,
    album: String,
) {
    Row(
        modifier = GlanceModifier.fillMaxSize().background(Color.White).padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            provider = albumArt?.let { ImageProvider(bitmap = it) } ?: ImageProvider(R.drawable.default_album_art),
            contentDescription = null,
            modifier = GlanceModifier.fillMaxHeight(),
        )
        Column(
            modifier = GlanceModifier.fillMaxWidth().padding(start = 8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title, style = TextStyle(
                    fontWeight = FontWeight.Medium,
                    fontSize = 22.sp
                )
            )
            Text(
                text = artist, style = TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp
                )
            )
            Text(
                text = album, style = TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp
                )
            )
            Row(
                modifier = GlanceModifier.padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val iconModifier = GlanceModifier.padding(horizontal = 8.dp)
                Image(
                    provider = ImageProvider(R.drawable.previous),
                    contentDescription = null,
                    modifier = iconModifier.clickable(
                        getAction(
                            LocalContext.current,
                            PlaybackService.ACTION_PREVIOUS
                        )
                    ),
                )
                Image(
                    provider = ImageProvider(if (isPlaying) R.drawable.pause else R.drawable.play),
                    contentDescription = null,
                    modifier = iconModifier.clickable(
                        getAction(
                            LocalContext.current,
                            if (isPlaying) PlaybackService.ACTION_PAUSE else PlaybackService.ACTION_PLAY
                        )
                    ),
                )
                Image(
                    provider = ImageProvider(R.drawable.next),
                    contentDescription = null,
                    modifier = iconModifier.clickable(
                        getAction(
                            LocalContext.current,
                            PlaybackService.ACTION_NEXT
                        )
                    ),
                )
            }
        }
    }
}

fun getAction(context: Context, action: String): Action {
    val intent = Intent(context, PlaybackService::class.java)
    intent.action = action
    return actionStartService(intent, true)
}