package com.example.musicdemo.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.musicdemo.R
import com.example.musicdemo.models.SongModel
import com.example.musicdemo.stickheaderhelper.StickHeaderItemDecoration
import kotlinx.android.synthetic.main.album_item.view.*
import kotlinx.android.synthetic.main.song_item.view.*

class SongsAdapter :
    RecyclerView.Adapter<SongsAdapter.MyViewHolder>(),
    StickHeaderItemDecoration.StickyHeaderInterface {
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
        val itemHolderLayout = LayoutInflater.from(parent.context)
            .inflate(if (viewType == 0) R.layout.song_item else R.layout.album_item, parent, false)
        return MyViewHolder(itemHolderLayout)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (holder.itemViewType == 0) {
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
        } else {
            holder.itemView.tvAlbum.text = mListSongs[position].album
        }
    }

    override fun getItemCount() = mListSongs.size
    override fun getItemViewType(position: Int): Int {
        return mListSongs[position].itemType
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun getHeaderPositionForItem(itemPosition: Int): Int {
        var position = itemPosition
        var headerPosition = 0
        do {
            if (isHeader(position)) {
                headerPosition = position
                break
            }
            position -= 1
        } while (position >= 0)
        return headerPosition
    }

    override fun getHeaderLayout(headerPosition: Int): Int {
        return if (mListSongs[headerPosition].itemType == 1)
            R.layout.album_item
        else {
            R.layout.album_item
        }
    }

    override fun bindHeaderData(header: View?, headerPosition: Int) {
        header?.tvAlbum?.text = mListSongs[headerPosition].album
    }

    override fun isHeader(itemPosition: Int): Boolean {
        return mListSongs[itemPosition].itemType == 1
    }

}