package com.chillle.chillmusic.data.repository

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.chillle.chillmusic.R
import com.chillle.chillmusic.ulti.MusicStyle
import java.io.ByteArrayOutputStream


class DefaultResource private constructor(resources: Resources) {
    val defaultAlbumArtBitmap: Bitmap
    val defaultAlbumArtByteArray: ByteArray
    private val defaultStyle: MusicStyle

    companion object {
        private var instance: DefaultResource? = null

        @Synchronized
        fun getInstance(resources: Resources): DefaultResource {
            if (instance == null) {
                instance = DefaultResource(resources)
            }
            return instance!!
        }
    }

    init {
        defaultAlbumArtBitmap = BitmapFactory.decodeResource(resources, R.drawable.default_album_art)
        val stream = ByteArrayOutputStream()
        defaultAlbumArtBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        defaultAlbumArtByteArray = stream.toByteArray()
        defaultStyle = MusicStyle.fromBitmap(defaultAlbumArtBitmap)
    }
}