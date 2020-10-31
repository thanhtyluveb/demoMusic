package com.example.musicdemo.views

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicdemo.R
import com.example.musicdemo.adapters.SongsAdapter
import com.example.musicdemo.models.SongModel
import com.example.musicdemo.provider.MediaProvider
import com.example.musicdemo.services.MusicService
import com.example.musicdemo.stickheaderhelper.StickHeaderItemDecoration
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_player_media.*


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
        mSongAdapter?.setCurrentPlayingSong(mMusicServiceBase?.mCurrentIndexOfSong ?: 0)
    }

    private fun initRecyclerView() {
        mSongAdapter = SongsAdapter()
        mSongAdapter?.let {
            rvListSong.addItemDecoration(StickHeaderItemDecoration(it))
        }
        rvListSong.layoutManager = LinearLayoutManager(this)
        rvListSong.itemAnimator = null
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