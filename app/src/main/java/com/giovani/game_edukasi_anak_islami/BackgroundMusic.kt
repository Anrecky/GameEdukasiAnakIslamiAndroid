package com.giovani.game_edukasi_anak_islami

import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.IBinder
import androidx.preference.PreferenceManager

class BackgroundMusic : Service(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var bgMusic: MediaPlayer

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        var prefs = PreferenceManager.getDefaultSharedPreferences(this)
        prefs.registerOnSharedPreferenceChangeListener(this)
        bgMusic = MediaPlayer.create(applicationContext, R.raw.backgroundmusic)
        bgMusic.isLooping = true // Set looping
        bgMusic.setVolume(0.5f, 0.5f)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        bgMusic.start()
        return START_STICKY
    }

    override fun onDestroy() {
        bgMusic.stop()
        bgMusic.release()
    }

    override fun onLowMemory() {

    }

    override fun onSharedPreferenceChanged(sp: SharedPreferences, s: String) {
        if (s == "bgVolume") {
            var bgVolume: Float = (sp.getInt(s, 50).toFloat()) / 100
            bgMusic.setVolume(bgVolume, bgVolume)
        }
    }


}