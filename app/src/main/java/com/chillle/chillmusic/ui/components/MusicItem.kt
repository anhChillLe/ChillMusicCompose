package com.chillle.chillmusic.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.media3.common.MediaMetadata
import com.chillle.chillmusic.R
import com.chillle.chillmusic.models.Song
import com.chillle.chillmusic.ulti.MusicStyle

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MusicItem(
    modifier: Modifier = Modifier,
    song: Song,
    style: MusicStyle = MusicStyle(),
    isCurrent: Boolean = false,
    selectable: Boolean = false,
    isSelected: Boolean = false,
    onSelectedChanged: (Boolean) -> Unit = {},
    onPress: () -> Unit = {},
    onLongPress: () -> Unit = {},
) {
    val color = if (isCurrent) style.contentColor else style.textColor

    ConstraintLayout(
        modifier = modifier
            .combinedClickable(
                onClick = onPress,
                onLongClick = onLongPress
            )
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(style.itemBackGround)
            .height(72.dp)
            .padding(8.dp),
    ) {
        val (icon, image, title, artist, album) = createRefs()

        val imageModifier = Modifier
            .constrainAs(image) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                height = Dimension.fillToConstraints
            }
            .clip(RoundedCornerShape(4.dp))
            .aspectRatio(1f, true)


        if(song.thumbNail != null){
            Image(
                bitmap = song.thumbNail.small.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = imageModifier
            )
        }else{
            Image(
                painter = painterResource(id = R.drawable.default_album_art),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = imageModifier
            )
        }

        // Title
        Text(
            text = song.title,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = MaterialTheme.typography.titleMedium,
            lineHeight = 24.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Start,
            color = color,
            modifier = Modifier
                .constrainAs(title) {
                    start.linkTo(image.end, margin = 8.dp)
                    end.linkTo(icon.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(artist.top)
                    width = Dimension.fillToConstraints
                }
        )

        // Artist
        Text(
            text = song.artist ?: "Unknown Artist",
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            lineHeight = 16.sp,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Start,
            color = color,
            modifier = Modifier.constrainAs(artist) {
                start.linkTo(image.end, margin = 8.dp)
                end.linkTo(icon.start)
                top.linkTo(title.bottom)
                bottom.linkTo(album.top)
                width = Dimension.fillToConstraints
            }

        )

        // Album
        Text(
            text = song.albumTitle ?: "Unknown Album",
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            lineHeight = 16.sp,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Start,
            color = color,
            modifier = Modifier
                .constrainAs(album) {
                    start.linkTo(image.end, margin = 8.dp)
                    end.linkTo(icon.start)
                    bottom.linkTo(parent.bottom)
                    top.linkTo(artist.bottom)
                    width = Dimension.fillToConstraints
                }

        )

        if (selectable) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = onSelectedChanged,
                colors = CheckboxDefaults.colors(
                    checkedColor = style.contentColor,
                    uncheckedColor = style.textColor,
                    checkmarkColor = style.backgroundColor
                ),
                modifier = Modifier.constrainAs(icon) {
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
            )
        } else {
            IconButton(
                onClick = { },
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = color,
                ),
                modifier = Modifier.constrainAs(icon) {
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }) {
                Icon(Icons.Default.MoreVert, contentDescription = null)
            }
        }
    }
}
