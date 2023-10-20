package com.chillle.chillmusic.models

import android.graphics.Bitmap
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata

data class BaseSong(
    val id: String,
    val path: String,
    val uri: Uri,
    val title: String,
)

data class MusicThumb(
    val small: Bitmap,
    val medium: Bitmap,
    val large: Bitmap
)

data class Song(
    val id: String,
    val path: String,
    val uri: Uri,
    val title: String,
    val artist: String? = null,
    val albumTitle: String? = null,
    val albumArtist: String? = null,
    val genre: String? = null,
    val trackNumber: Int? = null,
    val composer: String? = null,
    val writer: String? = null,
    val mimeType: String? = null,
    val bitRate: Long? = null,
    val size: Long? = null,
    val date: String? = null,
    val duration: Long? = null,
    val thumbNail: MusicThumb? = null,
    private val artWorkData: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Song
        return id == other.id
    }
    override fun hashCode() = id.hashCode()
    override fun toString() = title

    fun toMediaItem(isIncludeAlbumArt: Boolean = false, defaultArtWork: ByteArray? = null): MediaItem {
        val metadata = MediaMetadata.Builder().apply {
            setTitle(title)
            setArtist(artist)
            setAlbumTitle(albumTitle)
            setAlbumTitle(albumArtist)
            setGenre(genre)
            setWriter(writer)
            setComposer(composer)
            setTrackNumber(trackNumber)
            if (isIncludeAlbumArt) {
                setArtworkData(artWorkData ?: defaultArtWork, MediaMetadata.PICTURE_TYPE_MEDIA)
            }
        }.build()

        return MediaItem.Builder().apply {
            setUri(uri)
            setMediaId(id)
            setMediaMetadata(metadata)
            setMimeType(mimeType)
        }.build()
    }
}
