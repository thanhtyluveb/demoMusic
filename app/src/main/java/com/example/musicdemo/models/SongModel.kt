package com.example.musicdemo.models

data class SongModel(var name: String, var Url: String, var album: String, var itemType: Int = 0) {
    constructor(album: String) : this("", "", album = album) {
        this.album = album
        this.itemType = 1
    }
}