package ru.pakarp.homework1

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log


class PlayerService : Service() {
    var mp: MediaPlayer? = null
    private  val localBinder = LocalBinder()

    override fun onBind(intent: Intent?): IBinder {
        return localBinder
    }

    inner class LocalBinder : Binder() {

        fun getBindService(): PlayerService {
            return this@PlayerService
        }
    }


}