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
            ArabicVocal("??", R.raw.belajar_satu),
            ArabicVocal("??", R.raw.belajar_dua),
            ArabicVocal("??", R.raw.belajar_tiga),
            ArabicVocal("??", R.raw.belajar_empat),
            ArabicVocal("??", R.raw.belajar_lima),
            ArabicVocal("??", R.raw.belajar_enam),
            ArabicVocal("??", R.raw.belajar_tujuh),
            ArabicVocal("??", R.raw.belajar_delapan),
            ArabicVocal("??", R.raw.belajar_sembilan),
            ArabicVocal("????", R.raw.belajar_sepuluh),
            ArabicVocal("??", R.raw.belajar_dhad),
            ArabicVocal("??", R.raw.belajar_syin),
            ArabicVocal("??", R.raw.belajar_shad),
            ArabicVocal("??", R.raw.belajar_dzo),
            ArabicVocal("??", R.raw.belajar_sin),
            ArabicVocal("??", R.raw.belajar_tha),
            ArabicVocal("??", R.raw.belajar_tsa),
            ArabicVocal("??", R.raw.belajar_ya),
            ArabicVocal("??", R.raw.belajar_dzal),
            ArabicVocal("??", R.raw.belajar_khof),
            ArabicVocal("??", R.raw.belajar_ba),
            ArabicVocal("??", R.raw.belajar_dal),
            ArabicVocal("??", R.raw.belajar_fa),
            ArabicVocal("??", R.raw.belajar_lam),
            ArabicVocal("??", R.raw.belajar_zayn),
            ArabicVocal("??", R.raw.belajar_ghain),
            ArabicVocal("??", R.raw.belajar_alif),
            ArabicVocal("??", R.raw.belajar_ra),
            ArabicVocal("??", R.raw.belajar_ain),
            ArabicVocal("??", R.raw.belajar_ta),
            ArabicVocal("??", R.raw.belajar_waw),
            ArabicVocal("????", R.raw.belajar_haa),
            ArabicVocal("??", R.raw.belajar_nun),
            ArabicVocal("??", R.raw.belajar_kho),
            ArabicVocal("??", R.raw.belajar_mim),
            ArabicVocal("??", R.raw.belajar_hak),
            ArabicVocal("??", R.raw.belajar_khaf),
            ArabicVocal("??", R.raw.belajar_jim)
        )
        vocalSound = MediaPlayer.create(
            applicationContext,
            listArabicVocal.find { it.arabic == arabicText }!!.rawInt
        )
        vocalSound.isLooping = false

        return this.vocalSound
    }

}