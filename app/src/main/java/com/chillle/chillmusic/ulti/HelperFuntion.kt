package com.chillle.chillmusic.ulti

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

fun Number.formatAsDurationTime(): String {
    val total = this.toLong() / 1000

    val h = total / 3600
    var s = total % 3600
    val m = s / 60
    s %= 60

    return if (h != 0L) {
        String.format("%02d:%02d:%02d", h, m, s)
    } else {
        String.format("%02d:%02d", m, s)
    }

}

fun ByteArray.toBitmap(): Bitmap{
    return BitmapFactory.decodeByteArray(this, 0, size)
}

fun Bitmap.toByteArray(): ByteArray{
    val stream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, 100, stream)
    return stream.toByteArray()
}