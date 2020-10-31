package com.example.musicdemo.provider

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import com.example.musicdemo.models.SongModel

class MediaProvider(var context: Context) {
    fun getListMusicLocal(): ArrayList<SongModel> {
        var listSongsLocal = ArrayList<SongModel>()
        val songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val songCursor: Cursor? = context.contentResolver?.query(songUri, null, null, null, null)
        if (songCursor != null && songCursor.moveToFirst()) {
            val songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songPath = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val album = songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)
            do {
                listSongsLocal.add(
                    SongModel(
                        songCursor.getString(songTitle),
                        songCursor.getString(songPath),
                        songCursor.getString(album)
                    )
                )
            } while (songCursor.moveToNext())
            songCursor.close()
        }
        val hashMap = listSongsLocal.groupBy { it.album }
        listSongsLocal = ArrayList()
        hashMap.keys.forEach { album ->
            listSongsLocal.add(SongModel(album))
            hashMap[album]?.toList()?.let { listSongsLocal.addAll(it) }
        }
        return listSongsLocal
    }
}