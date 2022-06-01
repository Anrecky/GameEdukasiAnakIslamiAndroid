package com.giovani.game_edukasi_anak_islami

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LearnDetailActivity : AppCompatActivity() {
    private data class ArabicVocal(val fileName: String, val arabic: String)

    private val arabicVocalList = mutableListOf(
        ArabicVocal("belajar_satu", "١"),
        ArabicVocal("belajar_dua", "٢"),
        ArabicVocal("belajar_tiga", "٣"),
        ArabicVocal("belajar_empat", "٤"),
        ArabicVocal("belajar_lima", "٥"),
        ArabicVocal("belajar_enam", "٦"),
        ArabicVocal("belajar_tujuh", "٧"),
        ArabicVocal("belajar_delapan", "٨"),
        ArabicVocal("belajar_sembilan", "٩"),
        ArabicVocal("belajar_sepuluh", "١٠"),
        ArabicVocal("belajar_alif", "ا"),
        ArabicVocal("belajar_ba", "ب"),
        ArabicVocal("belajar_ta", "ت"),
        ArabicVocal("belajar_tsa", "ث"),
        ArabicVocal("belajar_jim", "ج"),
        ArabicVocal("belajar_hak", "ح"),
        ArabicVocal("belajar_kho", "خ"),
        ArabicVocal("belajar_dal", "د"),
        ArabicVocal("belajar_dzal", "ذ"),
        ArabicVocal("belajar_ra", "ر"),
        ArabicVocal("belajar_zayn", "ز"),
        ArabicVocal("belajar_sin", "س"),
        ArabicVocal("belajar_syin", "ش"),
        ArabicVocal("belajar_shad", "ص"),
        ArabicVocal("belajar_dhad", "ض"),
        ArabicVocal("belajar_tha", "ط"),
        ArabicVocal("belajar_dzo", "ظ"),
        ArabicVocal("belajar_ain", "ع"),
        ArabicVocal("belajar_ghain", "غ"),
        ArabicVocal("belajar_fa", "ف"),
        ArabicVocal("belajar_khof", "ق"),
        ArabicVocal("belajar_khaf", "ك"),
        ArabicVocal("belajar_lam_alif", "ل"),
        ArabicVocal("belajar_mim", "م"),
        ArabicVocal("belajar_nun", "ن"),
        ArabicVocal("belajar_haa", "هـ"),
        ArabicVocal("belajar_waw", "و"),
        ArabicVocal("belajar_ya", "ي")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learn_detail)

        val data = intent.getStringExtra("item")
        var itemDesc = intent.getStringExtra("itemDescription")
        if (itemDesc!!.contains('(')) {
            var index = itemDesc.indexOf('(')
            itemDesc =
                itemDesc.substring(0, index - 1) + "\n" + itemDesc.substring(index, itemDesc.length)
        }

        val txtV: TextView = findViewById(R.id.arabic)
        val txtDesc: TextView = findViewById(R.id.description)
        txtV.text = data
        txtDesc.text = itemDesc

        data?.let { playSound(it) }
    }

    private fun playSound(arabic: String) {
        val fileNameWithoutExtension: String = arabicVocalList.find { it.arabic == arabic }!!.fileName
        val resId = resources.getIdentifier(fileNameWithoutExtension, "raw", packageName)
        if (resId != 0) {
            val mediaPlayer = MediaPlayer.create(applicationContext, resId)
            mediaPlayer.start()
        }
    }

}