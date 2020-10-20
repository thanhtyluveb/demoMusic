package com.example.musicdemo.views

import android.os.Bundle
import android.view.View
import com.example.musicdemo.R
import com.example.musicdemo.services.MusicService
import kotlinx.android.synthetic.main.activity_player_media.*

class PlayerMediaActivity : BaseActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_media)
        btnPlay.setOnClickListener(this)
        btnNext.setOnClickListener(this)
        btnPrev.setOnClickListener(this)
        btnStop.setOnClickListener(this)
    }

    override fun onHaveBound() {
        super.onHaveBound()
        updateUi()
        mMusicServiceBase?.setServiceCallBack {
            updateUi()
        }
    }

    private fun updateUi() {
        tvSongNameMediaPlayer.text = mMusicServiceBase?.getNameSong()
        btnPlay.text = if (mMusicServiceBase?.mIsPlaying == true) "Pau" else "Play"
    }

    override fun onClick(v: View?) {
        val action = when (v) {
            btnPlay -> MusicService.PLAY_FLAG
            btnNext -> MusicService.NEXT_FLAG
            btnPrev -> MusicService.PREV_FLAG
            else -> ""
        }
        if (mIsBound) {
            mMusicServiceBase?.playMusicWithAction(action = action)
        }
    }
}