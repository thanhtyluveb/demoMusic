package com.example.musicdemo

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import com.example.musicdemo.MainActivity.Companion.INDEX_SONG_EXTRA
import com.example.musicdemo.MainActivity.Companion.mListSongsLocal

class MusicService : Service(), MediaPlayer.OnPreparedListener {
    private var notificationHelper: NotificationHelper? = null

    companion object {
        var mMediaPlayer: MediaPlayer? = null
        const val PAUSE_FLAG = "PAUSE_FLAG"
        const val PLAY_FLAG = "PLAY_FLAG"
        const val PREV_FLAG = "PREV_FLAG"
        const val NEXT_FLAG = "NEXT_FLAG"
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        mMediaPlayer = MediaPlayer()
        notificationHelper = NotificationHelper(this, "ÂM NHẠC")
        startForeground(1, notificationHelper?.builder())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        val songModelIndex = intent?.getIntExtra(INDEX_SONG_EXTRA, 0) ?: 0
        mMediaPlayer?.let {
            it.stop()
            it.reset()
            it.setDataSource(mListSongsLocal[songModelIndex].Url)
            it.setOnPreparedListener(this)
            it.prepareAsync()
        }
        when (action) {
            PAUSE_FLAG -> {
            }
            PLAY_FLAG -> {
            }
            PREV_FLAG -> {
            }
            NEXT_FLAG -> {
            }
            else -> {
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onPrepared(mp: MediaPlayer?) {
        mp?.start()
    }
}
