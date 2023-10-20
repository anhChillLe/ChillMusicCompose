package com.chillle.chillmusic.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import androidx.constraintlayout.compose.layoutId
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.Player
import com.chillle.chillmusic.R
import com.chillle.chillmusic.ui.components.AlbumArtImage
import com.chillle.chillmusic.ui.providers.CurrentPlayer
import com.chillle.chillmusic.ui.remember.rememberAlbumArt
import com.chillle.chillmusic.ulti.formatAsDurationTime
import com.chillle.chillmusic.viewmodel.PlayerViewModel

@OptIn(ExperimentalMotionApi::class, ExperimentalFoundationApi::class)
@Composable
fun MusicPlayer(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val viewModel = viewModel<PlayerViewModel>()
    val song = viewModel.currentSong ?: return
    val player = CurrentPlayer.player ?: return
    val interactionSource = remember { MutableInteractionSource() }
    var isExpanded by remember { mutableStateOf(false) }
    val contentScene = remember {
        context.resources
            .openRawResource(R.raw.player_scence)
            .readBytes()
            .decodeToString()
    }

    val motionProgress by animateFloatAsState(
        targetValue = if (isExpanded) 1F else 0F,
        label = "Player State",
        animationSpec = tween(300)
    )


    BackHandler(enabled = isExpanded) {
        isExpanded = false
    }

    MotionLayout(
        motionScene = MotionScene(contentScene),
        progress = motionProgress,
        modifier = modifier
    ) {
        // Container
        Box(
            modifier = Modifier
                .layoutId("container")
                .height(64.dp)
                .border(
                    0.25.dp,
                    viewModel.style.contentColor.copy((1F - motionProgress) / 4),
                    RoundedCornerShape(4.dp)
                )
                .background(viewModel.style.backgroundColor, shape = RoundedCornerShape(4.dp))
                .clickable(
                    enabled = !isExpanded,
                    indication = null,
                    interactionSource = interactionSource
                ) {
                    isExpanded = true
                }
        )

        // Back
        IconButton(
            onClick = {
                isExpanded = false
            },
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = viewModel.style.contentColor,
            ),
            modifier = Modifier.layoutId("back")
        ) {
            Icon(
                painterResource(id = R.drawable.arrow_down),
                contentDescription = "Go back"
            )
        }

        // Album title
        Text(
            text = "All Music",
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Normal,
                color = viewModel.style.contentColor,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .layoutId("album_title")
                .basicMarquee()
        )

        // More
        IconButton(
            onClick = {

            },
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = viewModel.style.contentColor,
            ),
            modifier = Modifier.layoutId("more")
        ) {
            Icon(
                painterResource(R.drawable.more),
                contentDescription = "More action"
            )
        }

        // AlbumArt
        if (song.thumbNail != null)
            Image(
                bitmap = song.thumbNail.large.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .layoutId("album_art")
                    .clip(RoundedCornerShape(4.dp))
                    .aspectRatio(1F)
            )
        else
            Image(
                painter = painterResource(id = R.drawable.default_album_art),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .layoutId("album_art")
                    .clip(RoundedCornerShape(4.dp))
                    .aspectRatio(1F)
            )

        // Title
        Text(
            text = song.title,
            maxLines = 1,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Start,
            color = viewModel.style.contentColor,
            softWrap = false,
            modifier = Modifier
                .layoutId("title")
                .basicMarquee()
        )


        // Artist
        Text(
            text = song.artist ?: "Unknown",
            maxLines = 1,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Start,
            color = viewModel.style.contentColor,
            modifier = Modifier
                .layoutId("artist")
                .basicMarquee()

        )

        // Favorite
        IconButton(
            onClick = { },
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = viewModel.style.contentColor,
            ),
            modifier = Modifier.layoutId("favorite")
        ) {
            Icon(
                painter = painterResource(R.drawable.favorite),
                contentDescription = "Favorite"
            )
        }

        // Previous
        IconButton(
            onClick = {
                player.seekToPrevious()
            },
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = viewModel.style.contentColor,
            ),
            modifier = Modifier.layoutId("previous")
        ) {
            Icon(
                painter = painterResource(R.drawable.previous),
                contentDescription = "Seek to previous"
            )
        }

        // Play or pause
        IconButton(
            onClick = {
                if (viewModel.isPlaying) player.pause()
                else player.play()
            },
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = viewModel.style.contentColor,
            ),
            modifier = Modifier.layoutId("play_or_pause")
        ) {
            if (viewModel.isPlaying) Icon(
                painter = painterResource(R.drawable.pause),
                contentDescription = "Pause"
            )
            else Icon(
                painter = painterResource(R.drawable.play),
                contentDescription = "Play"
            )
        }

        // Next
        IconButton(
            onClick = {
                player.seekToNext()
            },
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = viewModel.style.contentColor,
            ),
            modifier = Modifier.layoutId("next")
        ) {
            Icon(
                painter = painterResource(R.drawable.next),
                contentDescription = "Seek to next"
            )
        }

        // Navigation
        IconButton(
            onClick = {
                if (viewModel.isRandom) {
                    player.shuffleModeEnabled = false
                    player.repeatMode = Player.REPEAT_MODE_OFF
                } else if (viewModel.repeatMode == Player.REPEAT_MODE_ALL)
                    player.repeatMode = Player.REPEAT_MODE_ONE
                else if (viewModel.repeatMode == Player.REPEAT_MODE_ONE)
                    player.shuffleModeEnabled = true
                else
                    player.repeatMode = Player.REPEAT_MODE_ALL

            },
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = viewModel.style.contentColor,
            ),
            modifier = Modifier.layoutId("navigation")
        ) {
            Icon(
                painter = painterResource(
                    if (viewModel.isRandom) R.drawable.random
                    else if (viewModel.repeatMode == Player.REPEAT_MODE_ALL) R.drawable.repeat
                    else if (viewModel.repeatMode == Player.REPEAT_MODE_ONE) R.drawable.repeat_one
                    else R.drawable.change
                ),
                contentDescription = "Seek to next"
            )
        }

        // Duration progress bar
        LinearProgressIndicator(
            progress = viewModel.currentPosition.toFloat() / player.duration,
            modifier = Modifier
                .layoutId("duration")
                .clip(RoundedCornerShape(2.dp)),
            color = viewModel.style.contentColor,
            trackColor = viewModel.style.contentColor.copy(alpha = 0.35F),
        )

        // Current duration
        Text(
            text = viewModel.currentPosition.formatAsDurationTime(),
            maxLines = 1,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Start,
            color = viewModel.style.contentColor.copy(alpha = 0.35F),
            modifier = Modifier.layoutId("duration_current")
        )

        // Max duration
        Text(
            text = player.duration.formatAsDurationTime(),
            maxLines = 1,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.End,
            color = viewModel.style.contentColor.copy(alpha = 0.35F),
            modifier = Modifier.layoutId("duration_max")
        )
    }
}