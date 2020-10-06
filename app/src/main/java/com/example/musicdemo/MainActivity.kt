package com.example.musicdemo

import android.content.Intent
import android.database.Cursor
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var mSongAdapter: SongsAdapter? = null

    companion object {
        var mListSongsLocal: ArrayList<SongModel> = ArrayList()
        const val INDEX_SONG_EXTRA = "INDEX_SONG_EXTRA"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getListMusicLocal()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        mSongAdapter = SongsAdapter()
        rvListSong.layoutManager = LinearLayoutManager(this)
        mSongAdapter?.setCallBack { index ->
            val intent = Intent(this, PlayerMediaActivity::class.java)
            intent.putExtra(INDEX_SONG_EXTRA, index)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }
        rvListSong.adapter = mSongAdapter
        mSongAdapter?.setListSongs(mListSongsLocal)
    }

    private fun getListMusicLocal() {
        val songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val songCursor: Cursor? = contentResolver?.query(songUri, null, null, null, null)
        if (songCursor != null && songCursor.moveToFirst()) {
            val songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songPath = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            do {
                mListSongsLocal.add(
                    SongModel(
                        songCursor.getString(songTitle),
                        songCursor.getString(songPath)
                    )
                )
            } while (songCursor.moveToNext())
            songCursor.close()
        }
    }

}