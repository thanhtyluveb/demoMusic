package com.example.musicdemo.services

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.widget.Toast
import com.example.musicdemo.helper.NotificationHelper
import com.example.musicdemo.models.SongModel
import com.example.musicdemo.provider.MediaProvider
import java.util.*
import kotlin.collections.ArrayList


class MusicService : Service(), MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    private var notificationHelper: NotificationHelper? = null
    private var mIBindService: IBinder = LocalBinder()
    private var mAllowRebind: Boolean = false
    var mListMusic: ArrayList<SongModel> = ArrayList()
    var mCurrentIndexOfSong = 0
    var mMediaPlayer: MediaPlayer? = null
    var mCallBack: (() -> Unit?)? = null
    var mMediaProvider = MediaProvider(this)
    var mIsPlaying = false

    companion object {
        const val PLAY_FLAG = "PLAY_FLAG"
        const val PAUSE_FLAG = "PAUSE_FLAG"
        const val START_FLAG = "START_FLAG"
        const val PREV_FLAG = "PREV_FLAG"
        const val NEXT_FLAG = "NEXT_FLAG"
        const val STOP_FLAG = "STOP_FLAG"
    }

    private val mGenerator = Random()

    val randomNumber: Int
        get() = mGenerator.nextInt(mListMusic.size - 1)

    inner class LocalBinder : Binder() {
        val mMusicService: MusicService
            get() = this@MusicService
    }


    override fun onUnbind(intent: Intent): Boolean {
        return mAllowRebind
    }

    override fun onRebind(intent: Intent) {
    }

    override fun onDestroy() {
        unregisterReceiver(musicBroadCast)
    }

    override fun onBind(intent: Intent): IBinder? {
        return mIBindService
    }

    override fun onCreate() {
        super.onCreate()
        mMediaPlayer = MediaPlayer()
        notificationHelper =
            NotificationHelper(this, "ÂM NHẠC")
        startForeground(NotificationHelper.NOTIF_ID, notificationHelper?.builder())
        registerReceiver()
        mListMusic = mMediaProvider.getListMusicLocal()
    }

    fun playMusicWithAction(indexOfSong: Int = mCurrentIndexOfSong, action: String) {
        when (action) {
            START_FLAG, PLAY_FLAG -> {
                if (mMediaPlayer?.isPlaying == true && action == PLAY_FLAG) {
                    pauseMusic()
                } else {
                    playMusic(indexOfSong)
                }
            }
            PREV_FLAG -> {
                playMusic(mCurrentIndexOfSong - 1)
            }
            NEXT_FLAG -> {
                playMusic(mCurrentIndexOfSong + 1)
            }
            else -> {
                stopMusic()
            }
        }
    }

    fun setServiceCallBack(callback: () -> Unit) {
        mCallBack = callback
    }

    private fun stopMusic() {
        mIsPlaying = false
        mMediaPlayer?.release()
        mMediaPlayer = null
        stopForeground(true)
        stopSelf()
    }

    private fun pauseMusic() {
        mIsPlaying = false
        mMediaPlayer?.pause()
        notificationHelper?.updateNotification(false)
        mCallBack?.invoke()
    }

    fun getNameSong() =
        if (mCurrentIndexOfSong in mListMusic.indices) mListMusic[mCurrentIndexOfSong].name else ""

    private fun playMusic(indexOfSong: Int) {
        if (indexOfSong in mListMusic.indices) {
            if (indexOfSong == mCurrentIndexOfSong && mIsPlaying) {
                mMediaPlayer?.start()
            } else {
                try {
                    mMediaPlayer?.let {
                        it.stop()
                        it.reset()
                        it.setDataSource(mListMusic[indexOfSong].Url)
                        it.setOnPreparedListener(this)
                        it.prepareAsync()

                    }
                } catch (e: Exception) {
                    playMusic(indexOfSong + 1)
                    Toast.makeText(applicationContext, "Song not found", Toast.LENGTH_SHORT).show()
                }
            }
            mCurrentIndexOfSong = indexOfSong
            notificationHelper?.updateNotification(true, getNameSong())
            mCallBack?.invoke()
        }
    }

    override fun onPrepared(mp: MediaPlayer?) {
        mp?.start()
        mIsPlaying = true
        mCallBack?.invoke()
    }

    private fun registerReceiver() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(START_FLAG)
        intentFilter.addAction(PAUSE_FLAG)
        intentFilter.addAction(NEXT_FLAG)
        intentFilter.addAction(PREV_FLAG)
        intentFilter.addAction(PLAY_FLAG)
        intentFilter.addAction(STOP_FLAG)
        registerReceiver(musicBroadCast, intentFilter)
    }

    private var musicBroadCast = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            playMusicWithAction(action = intent.action ?: "")
        }
    }

    override fun onCompletion(mp: MediaPlayer?) {
        playMusic(mCurrentIndexOfSong + 1)
    }
}
