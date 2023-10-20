package com.chillle.chillmusic.ulti

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.core.graphics.luminance
import androidx.palette.graphics.Palette
import kotlin.math.abs

class MusicStyle(palette: Palette? = null) {
    var textColor = Color.Gray
    var backgroundColor = Color.White
    var contentColor = Color.Black
    val itemBackGround: Color get() = backgroundColor.container

    init {
        palette?.setColor()
    }

    private fun Palette.setColor() {
        if (swatches.size < 2) return

        val min = swatches.minBy { it.rgb.luminance }
        val max = swatches.maxBy { it.rgb.luminance }
        val medium = dominantSwatch?.rgb ?: return

        if (medium - min.rgb < max.rgb - medium) {
            backgroundColor = Color(min.rgb)
            contentColor = Color(max.rgb)
            textColor = Color(min.titleTextColor)
        } else {
            backgroundColor = Color(max.rgb)
            contentColor = Color(min.rgb)
            textColor = Color(max.titleTextColor)
        }
    }

    val isDarkBackGround: Boolean
        get() = backgroundColor.luminance() < 0.5F

    companion object {
        private val cache = mutableMapOf<String, MusicStyle>()

        private fun Float.factor(dark: Float): Float {
            val value = this * 255

            val factorValue = 0.05F
            val addValue = if (dark < 0.5F)
                abs((128 - value)) * factorValue + 8
            else
                -abs((value - 255)) * factorValue - 8
            return abs((value + addValue) / 255)
        }

        private val Color.container: Color
            get() {
                val dark = this.luminance()
                val a = this.alpha
                val r = this.red.factor(dark)
                val g = this.green.factor(dark)
                val b = this.blue.factor(dark)
                return Color(r, g, b, a)
            }

        fun fromBitmap(bitmap: Bitmap): MusicStyle {
            val palette = Palette.from(bitmap).clearFilters().generate()
            return MusicStyle(palette)
        }

        fun fromBitmap(key: String, bitmap: Bitmap): MusicStyle {
            if (cache.contains(key)) return fromKey(key)
            val palette = Palette.from(bitmap).clearFilters().generate()
            return cache.getOrPut(key) { MusicStyle(palette) }
        }

        fun fromByteArray(key: String, byteArray: ByteArray): MusicStyle {
            if (cache.contains(key)) return fromKey(key)

            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            val palette = Palette.from(bitmap).clearFilters().generate()
            return cache.getOrPut(key) { MusicStyle(palette) }
        }

        private fun fromKey(key: String): MusicStyle {
            return cache[key] ?: throw (Throwable("Key [$key] not found in cache store"))
        }

        fun clearCache() {
            cache.clear()
        }
    }
}