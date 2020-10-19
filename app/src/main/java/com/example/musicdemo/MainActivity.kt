package com.example.musicdemo

import android.content.*
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicdemo.provider.MediaProvider
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var mSongAdapter: SongsAdapter? = null
    private var mListSongsLocal: ArrayList<SongModel> = ArrayList()
    var mMediaProvider = MediaProvider(this)


    private var mMusicService: MusicService? = null
    private var mIsBound: Boolean = false

    val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as MusicService.LocalBinder
            mMusicService = binder.mMusicService
            mIsBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mIsBound = false
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mListSongsLocal = mMediaProvider.getListMusicLocal()
//        initBindService()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        mSongAdapter = SongsAdapter()
        rvListSong.layoutManager = LinearLayoutManager(this)
        mSongAdapter?.setCallBack { index ->
            val intent = Intent(this, PlayerMediaActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            mMusicService?.playMusicWithAction(index, MusicService.START_FLAG)
            startActivity(intent)
        }
        rvListSong.adapter = mSongAdapter
        mSongAdapter?.setListSongs(mListSongsLocal)
    }


    override fun onStart() {
        super.onStart()
        if (mMusicService == null) {
            Intent(this, MusicService::class.java).also { intent ->
                bindService(intent, connection, Context.BIND_AUTO_CREATE)
            }
        }
    }

    //    override fun onDestroy() {
//        super.onDestroy()
//        unbindService(connection)
//        mIsBound = false
//    }
}