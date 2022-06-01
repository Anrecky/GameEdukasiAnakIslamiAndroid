package com.giovani.game_edukasi_anak_islami

import android.app.Service
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.os.IBinder

class BackgroundMusic : Service() {

    private lateinit var bgMusic: MediaPlayer

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
    override fun onCreate() {
        super.onCreate()
        bgMusic = MediaPlayer.create(applicationContext,R.raw.backgroundmusic)
        bgMusic.isLooping = true // Set looping
        bgMusic.setVolume(0.5f, 0.5f)

    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        bgMusic.start()
        return START_NOT_STICKY
    }
    @Deprecated("Deprecated in Java")
    override fun onStart(intent: Intent, startId: Int) {
        // TO DO
    }
    fun onStop() {

    }

    fun onPause() {

    }

    override fun onDestroy() {
        bgMusic.stop()
        bgMusic.release()
    }

    override fun onLowMemory() {

    }

    companion object {
        private val TAG: String? = null
    }

}