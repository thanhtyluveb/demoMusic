package com.example.musicdemo

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.musicdemo.MainActivity.Companion.INDEX_SONG_EXTRA
import kotlinx.android.synthetic.main.activity_player_media.*

class PlayerMediaActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_media)
        val index = intent.getIntExtra(INDEX_SONG_EXTRA, 0)
        val intentService: Intent = Intent(this, MusicService::class.java)
        intentService.putExtra(INDEX_SONG_EXTRA, index)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intentService)
        } else {
            startService(intentService)
        }
        btnPlay.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            btnPlay -> {
                MusicService.mMediaPlayer?.let {
                    if (it.isPlaying) {
                        it.pause()
                        btnPlay.text = "Pause"
                    } else {
                        it.start()
                        btnPlay.text = "Play"

                    }
                }
            }
        }
    }
}