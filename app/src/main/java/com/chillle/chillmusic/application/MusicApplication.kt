package com.chillle.chillmusic.application

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.chillle.chillmusic.R

class MusicApplication : Application() {
    companion object {
        const val CHANNEL_MEDIA_PLAYER_ID = "CHANNEL_MEDIA_PLAYER_ID"
    }

    override fun onCreate() {
        super.onCreate()
//        createNotificationChanel()
    }

    private fun createNotificationChanel(){
        val name = getString(R.string.chanel_music_player)
        val descriptionText = getString(R.string.chanel_music_player_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannel(CHANNEL_MEDIA_PLAYER_ID, name, importance)
        mChannel.description = descriptionText
        mChannel.setSound(null, null)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }
}