package com.example.musicdemo.views

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import com.example.musicdemo.services.MusicService

abstract class BaseActivity : AppCompatActivity() {
    var mMusicServiceBase: MusicService? = null
    var mIsBound: Boolean = false
    private var mIsViewCreated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        if (mMusicServiceBase == null) {
            Intent(this, MusicService::class.java).also { intent ->
                bindService(intent, connection, Context.BIND_AUTO_CREATE)
            }
        }
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as MusicService.LocalBinder
            mMusicServiceBase = binder.mMusicService
            checkServiceReady()
            mIsBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mIsBound = false
        }
    }

    open fun onHaveBound() = Unit

    fun checkServiceReady() {
        if (mMusicServiceBase != null && mIsViewCreated)
            onHaveBound()
    }

    override fun onResume() {
        super.onResume()
        mIsViewCreated = true
        checkServiceReady()
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(connection)
        mIsBound = false
    }

}