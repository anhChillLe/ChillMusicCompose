package com.chillle.chillmusic.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.media3.common.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.drm.DrmSessionManagerProvider
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.upstream.LoadErrorHandlingPolicy
import androidx.media3.extractor.DefaultExtractorsFactory
import androidx.media3.extractor.Extractor
import androidx.media3.extractor.ExtractorInput
import androidx.media3.extractor.ExtractorOutput
import androidx.media3.extractor.ExtractorsFactory
import androidx.media3.extractor.PositionHolder
import androidx.media3.session.CommandButton
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaNotification
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.MediaStyleNotificationHelper
import androidx.media3.ui.PlayerNotificationManager
import com.chillle.chillmusic.R
import com.chillle.chillmusic.activity.MainActivity
import com.chillle.chillmusic.application.MusicApplication
import com.chillle.chillmusic.data.repository.DefaultResource
import com.chillle.chillmusic.data.repository.MusicStore
import com.google.common.collect.ImmutableList

@UnstableApi
class PlaybackService : MediaSessionService() {
    companion object {
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_PLAY = "ACTION_PLAY"
        const val ACTION_PREVIOUS = "ACTION_PREVIOUS"
        const val ACTION_NEXT = "ACTION_NEXT"
    }

    private lateinit var notificationProvider: MediaNotification.Provider
    private val mediaSession by lazy {
        val player = ExoPlayer.Builder(this)
            .setAudioAttributes(AudioAttributes.DEFAULT, true)
            .build()
        MediaSession.Builder(this, player).build()
    }

    override fun onCreate() {
        super.onCreate()
//        customNotification()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession {
        return mediaSession
    }

    override fun onDestroy() {
        mediaSession.player.release()
        mediaSession.release()
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent ?: return START_NOT_STICKY

        when (intent.action) {
            ACTION_PREVIOUS -> mediaSession.player.seekToPrevious()
            ACTION_PAUSE -> mediaSession.player.pause()
            ACTION_PLAY -> mediaSession.player.play()
            ACTION_NEXT -> mediaSession.player.seekToNext()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun customNotification() {
        notificationProvider = object : MediaNotification.Provider {
            val defaultProvider = DefaultMediaNotificationProvider.Builder(this@PlaybackService).build()
            val notificationId = DefaultMediaNotificationProvider.DEFAULT_NOTIFICATION_ID
            val channelId = DefaultMediaNotificationProvider.DEFAULT_CHANNEL_ID
            val intent = Intent(this@PlaybackService, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(this@PlaybackService, 0, intent, PendingIntent.FLAG_IMMUTABLE)

            override fun createNotification(
                mediaSession: MediaSession,
                customLayout: ImmutableList<CommandButton>,
                actionFactory: MediaNotification.ActionFactory,
                onNotificationChangedCallback: MediaNotification.Provider.Callback
            ): MediaNotification {
                mediaSession.setSessionActivity(pendingIntent)

                val song = mediaSession.player.currentMediaItem?.mediaId?.let { MusicStore.getSong(it) }
                val actions = listOf(
                    actionFactory.createMediaAction(
                        mediaSession,
                        IconCompat.createWithResource(
                            this@PlaybackService,
                            R.drawable.media3_notification_seek_to_previous
                        ),
                        "Previous",
                        Player.COMMAND_SEEK_TO_PREVIOUS
                    ),
                    if (mediaSession.player.isPlaying) {
                        actionFactory.createMediaAction(
                            mediaSession,
                            IconCompat.createWithResource(this@PlaybackService, R.drawable.media3_notification_pause),
                            "Pause",
                            Player.COMMAND_PLAY_PAUSE
                        )
                    } else {
                        actionFactory.createMediaAction(
                            mediaSession,
                            IconCompat.createWithResource(this@PlaybackService, R.drawable.media3_notification_play),
                            "Play",
                            Player.COMMAND_PLAY_PAUSE
                        )
                    },
                    actionFactory.createMediaAction(
                        mediaSession,
                        IconCompat.createWithResource(
                            this@PlaybackService,
                            R.drawable.media3_notification_seek_to_next
                        ),
                        "Next",
                        Player.COMMAND_SEEK_TO_NEXT
                    ),
                    actionFactory.createMediaAction(
                        mediaSession,
                        IconCompat.createWithResource(
                            this@PlaybackService,
                            R.drawable.clear
                        ),
                        "Clear",
                        Player.COMMAND_STOP
                    ),
                )

                val notification =
                    NotificationCompat.Builder(this@PlaybackService, channelId).apply {
                        setSmallIcon(R.drawable.splash_icon)
                        setContentTitle(song?.title)
                        setContentText(song?.artist)
                        if (song?.thumbNail != null)
                            setLargeIcon(song.thumbNail.large)
                        else
                            setLargeIcon(Icon.createWithResource(this@PlaybackService, R.drawable.default_album_art))
                        setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        setContentIntent(pendingIntent)
                        actions.forEach(::addAction)
                        customLayout.forEach {
                            val action = actionFactory.createCustomActionFromCustomCommandButton(mediaSession, it)
                            addAction(it.iconResId, it.displayName, action.actionIntent)
                        }
                        val style = MediaStyleNotificationHelper.MediaStyle(mediaSession)
                        style.setShowActionsInCompactView(0, 1, 2)
                        setStyle(style)
                    }.build()

                val mediaNotification = MediaNotification(notificationId, notification)
                onNotificationChangedCallback.onNotificationChanged(mediaNotification)
                return mediaNotification
            }

            override fun handleCustomCommand(session: MediaSession, action: String, extras: Bundle): Boolean {
                TODO("Not yet implemented")
            }

        }
        setMediaNotificationProvider(notificationProvider)
    }
}