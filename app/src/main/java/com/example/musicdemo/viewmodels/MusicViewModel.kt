package com.example.musicdemo.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.musicdemo.SongModel
import com.example.musicdemo.provider.MediaProvider

class MusicViewModel : ViewModel() {
    companion object {
        const val PAUSE_STATUS = 0
        const val PLAY_STATUS = 1
        const val STOP_STATUS = 2
    }

    var mStatusMediaPlayer = MutableLiveData<Int>()

    init {
    }

    fun setStatusForMediaPlayer(status: Int) {
        mStatusMediaPlayer.value = status
    }

    fun getListLocalSongs(context: Context): ArrayList<SongModel> {
        val mMediaProvider = MediaProvider(context)
        return mMediaProvider.getListMusicLocal()
    }
}