package com.example.musicdemo.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.musicdemo.R
import com.example.musicdemo.models.SongModel
import kotlinx.android.synthetic.main.song_item.view.*

class SongsAdapter() :
    RecyclerView.Adapter<SongsAdapter.MyViewHolder>() {
    private var mListSongs = ArrayList<SongModel>()
    private var mCallback: ((Int) -> Unit)? = null
    private var mLastSong = 0

    fun setListSongs(listSongs: ArrayList<SongModel>) {
        mListSongs = listSongs
        notifyDataSetChanged()
    }

    fun setCurrentPlayingSong(index: Int) {
        notifyItemChanged(mLastSong)
        mLastSong = index
        notifyItemChanged(mLastSong)
    }

    fun setCallBack(callback: ((Int) -> Unit)?) {
        mCallback = callback
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.song_item, parent, false)
        return MyViewHolder(textView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.tvNameSong.text = mListSongs[position].name
        holder.itemView.setOnClickListener {
            mCallback?.invoke(position)
        }
        if (position == mLastSong) {
            holder.itemView.setBackgroundColor(Color.CYAN)
            holder.itemView.tvNameSong.setTextColor(Color.WHITE)
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE)
            holder.itemView.tvNameSong.setTextColor(Color.BLACK)
        }
    }

    override fun getItemCount() = mListSongs.size
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

}