package com.giovani.game_edukasi_anak_islami

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.giovani.game_edukasi_anak_islami.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val bgMusicIntentService = Intent(this,BackgroundMusic::class.java)
        startService(bgMusicIntentService)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}