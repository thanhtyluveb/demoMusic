package com.example.musicdemo

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.widget.Toast
import com.example.musicdemo.viewmodels.MusicViewModel
import java.util.*
import kotlin.collections.ArrayList


class MusicService : Service(), MediaPlayer.OnPreparedListener {
    private var notificationHelper: NotificationHelper? = null
    private var mIBindService: IBinder = LocalBinder()
    private var mAllowRebind: Boolean = false
    var mListMusic: ArrayList<SongModel> = ArrayList()
    var mMusicViewModel: MusicViewModel? = null

    companion object {
        var mCurrentIndexOfSong = -1
        var mMediaPlayer: MediaPlayer? = null
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
    }

    override fun onBind(intent: Intent): IBinder? {
        return mIBindService
    }

    override fun onCreate() {
        super.onCreate()
        if (mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer()
            notificationHelper = NotificationHelper(this, "ÂM NHẠC")
            startForeground(1, notificationHelper?.builder())
        }
        mMusicViewModel = MusicViewModel()
        mListMusic = mMusicViewModel?.getListLocalSongs(this) ?: arrayListOf()

    }

    fun playMusicWithAction(indexOfSong: Int = mCurrentIndexOfSong, action: String) {
        val intent = Intent()
        intent.action = action
        when (action) {
            START_FLAG, PLAY_FLAG -> {
                if (mMediaPlayer?.isPlaying == true && action == PLAY_FLAG) {
                    pauseMusic()
                    intent.action = PAUSE_FLAG
                } else {
                    intent.action = PLAY_FLAG
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
                intent.action = START_FLAG
                stopMusic()
            }
        }
        sendBroadcast(intent)
    }

    private fun stopMusic() {
        mMediaPlayer?.release()
        mMediaPlayer = null
        stopForeground(true)
        stopSelf()
    }

    private fun pauseMusic() {
        mMediaPlayer?.pause()
    }

    private fun playMusic(indexOfSong: Int) {
        if (indexOfSong in mListMusic.indices) {
            if (indexOfSong == mCurrentIndexOfSong) {
                mMediaPlayer?.start()
                return
            }
            mCurrentIndexOfSong = indexOfSong
            try {
                mMediaPlayer?.let {
                    it.stop()
                    it.reset()
                    it.setDataSource(mListMusic[indexOfSong].Url)
                    it.setOnPreparedListener(this)
                    it.prepareAsync()
                    mMusicViewModel?.setStatusForMediaPlayer(MusicViewModel.PLAY_STATUS)
                }
            } catch (e: Exception) {
                Toast.makeText(applicationContext, "Song not found", Toast.LENGTH_SHORT).show()
                mMusicViewModel?.setStatusForMediaPlayer(MusicViewModel.STOP_STATUS)
            }
        }
    }

    override fun onPrepared(mp: MediaPlayer?) {
        mp?.start()
    }

}
