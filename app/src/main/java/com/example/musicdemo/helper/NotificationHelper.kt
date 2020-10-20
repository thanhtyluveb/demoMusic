package com.example.musicdemo.helper

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.musicdemo.R
import com.example.musicdemo.services.MusicService
import com.example.musicdemo.views.PlayerMediaActivity

class NotificationHelper(private val context: Context, private val message: String) {
    private var builderNotif: NotificationCompat.Builder? = null
    private var notificationManager: NotificationManager? = null
    private var remoteMusicView: RemoteViews? = null

    fun builder(): Notification? {
        if (builderNotif != null) return builderNotif?.build()
        createNotificationChannel()
        val pendingIntentPlayerActivity: PendingIntent = PendingIntent.getActivity(
            context, 0,
            Intent(context, PlayerMediaActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0
        )
        val prevIntent = createPendingIntent(MusicService.PREV_FLAG)
        val playIntent = createPendingIntent(MusicService.PLAY_FLAG)
        val nextIntent = createPendingIntent(MusicService.NEXT_FLAG)
        val stopIntent = createPendingIntent(MusicService.STOP_FLAG)

        remoteMusicView = RemoteViews(context.packageName,
            R.layout.activity_player_media
        )
        remoteMusicView?.setOnClickPendingIntent(R.id.btnPrev, prevIntent)
        remoteMusicView?.setOnClickPendingIntent(R.id.btnPlay, playIntent)
        remoteMusicView?.setOnClickPendingIntent(R.id.btnNext, nextIntent)
        remoteMusicView?.setOnClickPendingIntent(R.id.btnStop, stopIntent)

        builderNotif =
            NotificationCompat.Builder(context,
                CHANNEL_ID
            )
                .setContentIntent(pendingIntentPlayerActivity)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Foreground Service")
                .setContentText(message)
                .setColor(Color.TRANSPARENT)
                .setSound(null)
                .setCustomContentView(remoteMusicView)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
        builderNotif?.let {
            synchronized(it) {}
        }
        return builderNotif?.build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "1"
            val importance: Int = NotificationManager.IMPORTANCE_LOW
            val channel =
                NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = "description"
            notificationManager = context.getSystemService(
                NotificationManager::class.java
            )
            notificationManager?.deleteNotificationChannel(CHANNEL_ID)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    private fun createPendingIntent(action: String): PendingIntent {
        val switchIntent = Intent(action)
        return PendingIntent.getBroadcast(context, 100, switchIntent, 0)
    }

    fun updateNotification(isPlaying: Boolean, songName: String? = null) {
        remoteMusicView?.setTextViewText(R.id.btnPlay, if (isPlaying) "Pau" else "Play")
        songName?.let {
            remoteMusicView?.setTextViewText(R.id.tvSongNameMediaPlayer, songName)
        }
        builderNotif?.let {
            notificationManager?.notify(NOTIF_ID, it.build())
        }
    }

    companion object {
        private const val CHANNEL_ID = "Foreground"
        const val NOTIF_ID = 1
    }

}