package com.example.musicdemo.provider

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import com.example.musicdemo.SongModel

class MediaProvider(var context: Context) {
    fun getListMusicLocal(): ArrayList<SongModel> {
        val listSongsLocal = ArrayList<SongModel>()
        val songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val songCursor: Cursor? = context.contentResolver?.query(songUri, null, null, null, null)
        if (songCursor != null && songCursor.moveToFirst()) {
            val songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songPath = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            do {
                listSongsLocal.add(
                    SongModel(
                        songCursor.getString(songTitle),
                        songCursor.getString(songPath)
                    )
                )
            } while (songCursor.moveToNext())
            songCursor.close()
        }
        return listSongsLocal
    }
}