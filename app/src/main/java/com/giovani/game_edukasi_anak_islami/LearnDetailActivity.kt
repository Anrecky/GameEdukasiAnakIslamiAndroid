package com.giovani.game_edukasi_anak_islami

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.giovani.game_edukasi_anak_islami.data.ArabicVocal
import java.util.*

class LearnDetailActivity : AppCompatActivity() {

    private lateinit var vocalSound: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learn_detail)

        val data = intent.getStringExtra("item")
        var itemDesc = intent.getStringExtra("itemDescription")

        fun String.replace(mapping: Map<String, String>): String {
            var str = this
            mapping.forEach { str = str.replace(it.key, it.value) }
            return str
        }

        if (itemDesc!!.contains('(')) {
            val index = itemDesc.indexOf('(')
            val mapping = mapOf("(" to "", ")" to "")
            itemDesc = "${((itemDesc.substring(index,itemDesc.length)).replace(mapping)).replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            }} Adalah Angka ${itemDesc.substring(0, index - 1)}"
        }else{
            itemDesc = "Huruf ${itemDesc.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            }}"
        }


        val txtV: TextView = findViewById(R.id.arabic)
        val txtDesc: TextView = findViewById(R.id.description)
        txtV.text = data
        txtDesc.text = itemDesc


        data?.let {
            playVocalSound(it).start()
        }
    }
    override fun onBackPressed() {
        if (vocalSound.isPlaying) {
            vocalSound.stop()
            vocalSound.reset()
        }
        super.onBackPressed()
    }


    private fun playVocalSound(arabicText: String): MediaPlayer {
        val listArabicVocal = listOf(
            ArabicVocal("١", R.raw.belajar_satu),
            ArabicVocal("٢", R.raw.belajar_dua),
            ArabicVocal("٣", R.raw.belajar_tiga),
            ArabicVocal("٤", R.raw.belajar_empat),
            ArabicVocal("٥", R.raw.belajar_lima),
            ArabicVocal("٦", R.raw.belajar_enam),
            ArabicVocal("٧", R.raw.belajar_tujuh),
            ArabicVocal("٨", R.raw.belajar_delapan),
            ArabicVocal("٩", R.raw.belajar_sembilan),
            ArabicVocal("١٠", R.raw.belajar_sepuluh),
            ArabicVocal("ض", R.raw.belajar_dhad),
            ArabicVocal("ش", R.raw.belajar_syin),
            ArabicVocal("ص", R.raw.belajar_shad),
            ArabicVocal("ظ", R.raw.belajar_dzo),
            ArabicVocal("س", R.raw.belajar_sin),
            ArabicVocal("ط", R.raw.belajar_tha),
            ArabicVocal("ث", R.raw.belajar_tsa),
            ArabicVocal("ي", R.raw.belajar_ya),
            ArabicVocal("ذ", R.raw.belajar_dzal),
            ArabicVocal("ق", R.raw.belajar_khof),
            ArabicVocal("ب", R.raw.belajar_ba),
            ArabicVocal("د", R.raw.belajar_dal),
            ArabicVocal("ف", R.raw.belajar_fa),
            ArabicVocal("ل", R.raw.belajar_lam),
            ArabicVocal("ز", R.raw.belajar_zayn),
            ArabicVocal("غ", R.raw.belajar_ghain),
            ArabicVocal("ا", R.raw.belajar_alif),
            ArabicVocal("ر", R.raw.belajar_ra),
            ArabicVocal("ع", R.raw.belajar_ain),
            ArabicVocal("ت", R.raw.belajar_ta),
            ArabicVocal("و", R.raw.belajar_waw),
            ArabicVocal("هـ", R.raw.belajar_haa),
            ArabicVocal("ن", R.raw.belajar_nun),
            ArabicVocal("خ", R.raw.belajar_kho),
            ArabicVocal("م", R.raw.belajar_mim),
            ArabicVocal("ح", R.raw.belajar_hak),
            ArabicVocal("ك", R.raw.belajar_khaf),
            ArabicVocal("ج", R.raw.belajar_jim)
        )
        vocalSound = MediaPlayer.create(
            applicationContext,
            listArabicVocal.find { it.arabic == arabicText }!!.rawInt
        )
        vocalSound.isLooping = false

        return this.vocalSound
    }

}