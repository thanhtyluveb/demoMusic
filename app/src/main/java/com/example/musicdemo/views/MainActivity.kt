package com.example.musicdemo.views

import android.content.*
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicdemo.*
import com.example.musicdemo.adapters.SongsAdapter
import com.example.musicdemo.models.SongModel
import com.example.musicdemo.provider.MediaProvider
import com.example.musicdemo.services.MusicService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_player_media.*
import kotlinx.android.synthetic.main.activity_player_media.view.*

class MainActivity : BaseActivity(), View.OnClickListener {
    private var mSongAdapter: SongsAdapter? = null
    private var mListSongsLocal: ArrayList<SongModel> = ArrayList()
    private var mMediaProvider = MediaProvider(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mListSongsLocal = mMediaProvider.getListMusicLocal()
        btnPlay.setOnClickListener(this)
        btnNext.setOnClickListener(this)
        btnPrev.setOnClickListener(this)
        btnStop.setOnClickListener(this)
        initRecyclerView()
    }

    override fun onHaveBound() {
        super.onHaveBound()
        layoutControlMusicMain.apply {
            updateUi()
        }
        mMusicServiceBase?.setServiceCallBack {
            updateUi()
        }
    }

    private fun updateUi() {
        this.tvSongNameMediaPlayer.text = mMusicServiceBase?.getNameSong()
        this.btnPlay.text = if (mMusicServiceBase?.mIsPlaying == true) "Pau" else "Play"
    }

    private fun initRecyclerView() {
        mSongAdapter = SongsAdapter()
        rvListSong.layoutManager = LinearLayoutManager(this)
        mSongAdapter?.setCallBack { index ->
            val intent = Intent(this, PlayerMediaActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            mMusicServiceBase?.playMusicWithAction(
                index,
                MusicService.START_FLAG
            )
            startActivity(intent)
        }
        rvListSong.adapter = mSongAdapter
        mSongAdapter?.setListSongs(mListSongsLocal)
    }


    override fun onStart() {
        super.onStart()
        val intentService = Intent(this, MusicService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intentService)
        } else {
            startService(intentService)
        }
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