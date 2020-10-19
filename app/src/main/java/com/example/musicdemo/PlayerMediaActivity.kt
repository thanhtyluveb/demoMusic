package com.example.musicdemo

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.musicdemo.viewmodels.MusicViewModel
import kotlinx.android.synthetic.main.activity_player_media.*

class PlayerMediaActivity : AppCompatActivity(), View.OnClickListener {
    private var mMusicService: MusicService? = null
    private var mIsBound: Boolean = false
    var musicViewModel: MusicViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_media)
        btnPlay.setOnClickListener(this)
        btnNext.setOnClickListener(this)
        btnPrev.setOnClickListener(this)
        btnStop.setOnClickListener(this)
        musicViewModel = MusicViewModel()
        musicViewModel?.mStatusMediaPlayer?.observe(this, Observer {
            when (it) {
                MusicViewModel.PAUSE_STATUS -> {
                    btnPlay.text = "Play"
                }
                MusicViewModel.PLAY_STATUS -> {
                    btnPlay.text = "Pause"
                }
                MusicViewModel.STOP_STATUS -> {
                    btnPlay.text = "Play"
                }
            }
        })
    }

    override fun onClick(v: View?) {
        val action = when (v) {
            btnPlay -> MusicService.PLAY_FLAG
            btnNext -> MusicService.NEXT_FLAG
            btnPrev -> MusicService.PREV_FLAG
            else -> ""
        }
        if (mIsBound) {
            mMusicService?.playMusicWithAction(action = action)
        }
    }

    override fun onStart() {
        super.onStart()
        if (mMusicService == null) {
            val intentFilter = IntentFilter()
            intentFilter.addAction(MusicService.START_FLAG)
            intentFilter.addAction(MusicService.PAUSE_FLAG)
            intentFilter.addAction(MusicService.NEXT_FLAG)
            intentFilter.addAction(MusicService.PREV_FLAG)
            intentFilter.addAction(MusicService.PLAY_FLAG)
            intentFilter.addAction(MusicService.STOP_FLAG)
            registerReceiver(musicBroadCast, intentFilter)
            Intent(this, MusicService::class.java).also { intent ->
                bindService(intent, connection, Context.BIND_AUTO_CREATE)
            }
        }
    }

    val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as MusicService.LocalBinder
            mMusicService = binder.mMusicService
            mIsBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mIsBound = false
            unregisterReceiver(musicBroadCast)
        }
    }

    var musicBroadCast = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                MusicService.PLAY_FLAG -> {
                    musicViewModel?.setStatusForMediaPlayer(MusicViewModel.PLAY_STATUS)
                }
                MusicService.PAUSE_FLAG -> {
                    musicViewModel?.setStatusForMediaPlayer(MusicViewModel.PAUSE_STATUS)
                }
                MusicService.PREV_FLAG -> {
                    musicViewModel?.setStatusForMediaPlayer(MusicViewModel.PLAY_STATUS)
                }
                MusicService.NEXT_FLAG -> {
                    musicViewModel?.setStatusForMediaPlayer(MusicViewModel.PLAY_STATUS)
                }
                MusicService.STOP_FLAG -> {
                    musicViewModel?.setStatusForMediaPlayer(MusicViewModel.STOP_STATUS)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(musicBroadCast)
    }
}