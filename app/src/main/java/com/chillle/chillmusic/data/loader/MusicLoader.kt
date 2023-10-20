package com.chillle.chillmusic.data.loader

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import com.chillle.chillmusic.models.BaseSong
import com.chillle.chillmusic.models.MusicThumb
import com.chillle.chillmusic.models.Song
import java.io.File
import java.util.stream.Collectors

class MusicLoader(private val context: Context) {

    companion object {
        const val DURATION = MediaMetadataRetriever.METADATA_KEY_DURATION
        const val TITLE = MediaMetadataRetriever.METADATA_KEY_TITLE
        const val ALBUM = MediaMetadataRetriever.METADATA_KEY_ALBUM
        const val ARTIST = MediaMetadataRetriever.METADATA_KEY_ARTIST
        const val ALBUM_ARTIST = MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST
        const val GENRE = MediaMetadataRetriever.METADATA_KEY_GENRE
        const val BITRATE = MediaMetadataRetriever.METADATA_KEY_BITRATE
        const val DATE = MediaMetadataRetriever.METADATA_KEY_DATE
        const val TRACKS = MediaMetadataRetriever.METADATA_KEY_NUM_TRACKS
        const val COMPOSER = MediaMetadataRetriever.METADATA_KEY_COMPOSER
        const val WRITER = MediaMetadataRetriever.METADATA_KEY_WRITER
        const val MIME_TYPE = MediaMetadataRetriever.METADATA_KEY_MIMETYPE
    }


    fun getListSong(): List<Song> {
        val resolver = context.contentResolver
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DISPLAY_NAME,
        )
        val collection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} ASC"
        val query = resolver.query(collection, projection, null, null, sortOrder)
        val songs = mutableListOf<BaseSong>()
        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val path = cursor.getString(pathColumn)
                val fileName = cursor.getString(nameColumn)
                val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                songs.add(BaseSong(id.toString(), path, uri, fileName))
            }
            cursor.close()
        }

        return songs
            .parallelStream()
            .map {
                val metadataRetriever = MediaMetadataRetriever()
                metadataRetriever.extractSong(it)
            }
            .collect(Collectors.toList())
            .filterNotNull()
    }

    private fun MediaMetadataRetriever.extractSong(base: BaseSong): Song? {
        try {
            setDataSource(context, base.uri)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            return null
        }
        val thumb = getThumb(embeddedPicture)

        return Song(
            id = base.id,
            path = base.path,
            uri = base.uri,
            title = extractMetadata(TITLE) ?: getFileName(base.uri),
            artist = extractMetadata(ARTIST),
            albumTitle = extractMetadata(ALBUM),
            albumArtist = extractMetadata(ALBUM_ARTIST),
            genre = extractMetadata(GENRE),
            trackNumber = extractMetadata(TRACKS)?.toInt(),
            composer = extractMetadata(COMPOSER),
            writer = extractMetadata(WRITER),
            mimeType = extractMetadata(MIME_TYPE),
            bitRate = extractMetadata(BITRATE)?.toLong(),
            size = getFileSize(base.uri),
            date = extractMetadata(DATE),
            duration = extractMetadata(DURATION)?.toLong(),
            artWorkData = embeddedPicture,
            thumbNail = thumb
        )
    }

    private fun getThumb(data: ByteArray?): MusicThumb? {
        return data?.let {
            val large = BitmapFactory.decodeByteArray(it, 0, it.size)
            val medium = large.scaleByHeight(256)
            val small = large.scaleByHeight(128)
            MusicThumb(small, medium, large)
        }
    }

    private fun Bitmap.scaleByHeight(newHeight: Int): Bitmap {
        if(width == 0 || height == 0) return this
        val ratio = width.toFloat() / height
        val calculatedWidth = (newHeight * ratio).toInt()
        return Bitmap.createScaledBitmap(this, calculatedWidth, newHeight, false)
    }

    private fun getFileName(uri: Uri): String {
        context.contentResolver.query(uri, null, null, null)?.use { cursor ->
            val nameColumn = cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            return File(cursor.getString(nameColumn)).nameWithoutExtension
        }

        return "Unknown"
    }

    private fun getFileSize(uri: Uri): Long {
        context.contentResolver.query(uri, null, null, null)?.use { cursor ->
            val sizeColumn = cursor.getColumnIndexOrThrow(OpenableColumns.SIZE)
            cursor.moveToFirst()
            return cursor.getLong(sizeColumn)
        }

        return 0L
    }
}