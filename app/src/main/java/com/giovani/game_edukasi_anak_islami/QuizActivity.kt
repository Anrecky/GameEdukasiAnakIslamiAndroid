package com.giovani.game_edukasi_anak_islami

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.giovani.game_edukasi_anak_islami.data.ArabicVocal
import com.giovani.game_edukasi_anak_islami.data.Question
import com.giovani.game_edukasi_anak_islami.data.QuizResult
import com.giovani.game_edukasi_anak_islami.databinding.ActivityQuizBinding
import kotlinx.coroutines.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import org.json.JSONObject


@Suppress("BlockingMethodInNonBlockingContext")
@SuppressLint("SetTextI18n")
class QuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuizBinding
    private lateinit var mGameTypes: String
    private var mQuestions: List<Question> = emptyList()
    private var mQuestionPosition: Int = 1
    private var mCorrectCount: Int = 0
    private var mWrongCount: Int = 0
    private var qResult: QuizResult? = null
    private var categoryId: Int? = 1
    private val client: OkHttpClient = OkHttpClient()
    private lateinit var vocalSound: MediaPlayer
    private var vocalVolume: Float = 1.0F

    init {
        lifecycleScope.launchWhenCreated {
            withContext(Dispatchers.IO) {
                try {
                    val json = JSONObject()
                    json.put("id_kategori", "$categoryId")

                    val body = json.toString().toRequestBody(MEDIA_TYPE_JSON)

                    val request = Request.Builder()
                        .url("http://${getString(R.string.ip_address)}/admin/api/create-result-instance.php")
                        .post(body)
                        .build()
                    val res = client.newCall(request).execute().body?.string()
                    qResult = Json.decodeFromString<QuizResult>(res!!)
                } catch (err: Error) {
                    print("Error when executing create instance of result: " + err.localizedMessage)
                }
                try {
                    // Build request
                    val request = Request.Builder()
                        .url("http://${getString(R.string.ip_address)}/admin/api/read.php?id_kategori=$categoryId")
                        .build()
                    // Execute request
                    val res =
                        client.newCall(request).execute().body?.string()

                    if (res != null) {
                        try {
                            // Parse result string JSON to data class
                            mQuestions = Json.decodeFromString(res)
                            withContext(Dispatchers.Main) {
                                // Get Initialize Question From Questions Fetching
                                val question = mQuestions[mQuestionPosition - 1]
                                // Play Vocal Sound
                                playVocalSound(question.answer).start()

                                // Initialize UI
                                binding.gameTitle.text = getString(R.string.game_title, mGameTypes)
                                binding.correctCount.text =
                                    getString(R.string.correct_count, mCorrectCount)
                                binding.wrongCount.text =
                                    getString(R.string.wrong_count, mWrongCount)
                                binding.questionText.text = question.question
                                binding.questionPosition.text =
                                    getString(
                                        R.string.question_position,
                                        mQuestionPosition,
                                        mQuestions.size
                                    )
                                binding.option1.text = question.optionOne
                                binding.option2.text = question.optionTwo
                                binding.option3.text = question.optionThree
                                binding.option4.text = question.optionFour
                            }

                        } catch (err: Error) {
                            print("Error when parsing JSON: " + err.localizedMessage)
                        }
                    } else {
                        print("Error: Get request returned no response")
                    }
                } catch (err: Error) {
                    print("Error when executing get request questions : " + err.localizedMessage)
                }
            }

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Preferences
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        vocalVolume = (prefs.getInt("quizVolume", 100).toFloat()) / 100

        // Set the game type passed from menu
        mGameTypes = intent.getStringExtra("quizType").toString()

        // Set Category Id
        categoryId = when (mGameTypes) {
            "Angka" -> 1
            else -> 2
        }
        // Home Button
        binding.homeButton.setOnClickListener {
            if (vocalSound.isPlaying) {
                vocalSound.stop()
                vocalSound.reset()
            }
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        // Bind Option Buttons
        binding.option1.setOnClickListener { v -> onOptionClicked(v as RadioButton) }
        binding.option2.setOnClickListener { v -> onOptionClicked(v as RadioButton) }
        binding.option3.setOnClickListener { v -> onOptionClicked(v as RadioButton) }
        binding.option4.setOnClickListener { v -> onOptionClicked(v as RadioButton) }
    }

    override fun onBackPressed() {
        if (vocalSound.isPlaying) {
            vocalSound.stop()
            vocalSound.reset()
        }
        if (mQuestionPosition != mQuestions.size) {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val request = Request.Builder()
                        .url("http://${getString(R.string.ip_address)}/admin/api/delete-result.php?id=${qResult?.id}")
                        .build()
                    client.newCall(request).execute()

                } catch (err: Error) {
                    print("Error delete the result : " + err.localizedMessage)
                }
            }
        }

        super.onBackPressed()
    }

    private fun onOptionClicked(radioButton: RadioButton) {

        val optionSound = when (radioButton.text) {
            mQuestions[mQuestionPosition - 1].answer -> MediaPlayer.create(this, R.raw.correct)
            else -> MediaPlayer.create(this, R.raw.wrong)

        }
        optionSound.isLooping = false
        if (vocalSound.isPlaying) {
            vocalSound.stop()
            vocalSound.reset()
        }

        if (optionSound.isPlaying) optionSound.reset()

        // Check if correct answer
        when (radioButton.text) {
            mQuestions[mQuestionPosition - 1].answer -> {
                optionSound.start()

                // Save per-question data to result detail table
                postResultDetail(
                    mQuestions[mQuestionPosition - 1],
                    radioButton.text.toString(),
                    1
                )

                mCorrectCount++

                // Check if position is last of questions list
                if (mQuestionPosition == mQuestions.size) {

                    val score: Int = (mCorrectCount * 100) / mQuestions.size

                    lifecycleScope.launch(Dispatchers.IO) {

                        val json = JSONObject()
                        json.put("id", qResult!!.id)
                        json.put("id_kategori", "$categoryId")
                        json.put("skor", score)
                        json.put("tgl_waktu", qResult!!.dateTime)

                        val body = json.toString().toRequestBody(MEDIA_TYPE_JSON)
                        val request = Request.Builder()
                            .url("http://${getString(R.string.ip_address)}/admin/api/update-result.php")
                            .post(body)
                            .build()
                        client.newCall(request).execute()
                    }

                    val intent = Intent(this, QuizResultActivity::class.java)
                    intent.putExtra("corrects_count", mCorrectCount)
                    intent.putExtra("wrongs_count", mWrongCount)
                    intent.putExtra("game_title", mGameTypes)
                    intent.putExtra("total_questions", mQuestions.size)
                    startActivity(intent)
                    finish()
                    return
                }

                // Increment Current Position Of Question From Question List
                mQuestionPosition++
                // Set new question data from questions according to position
                val question = mQuestions[mQuestionPosition - 1]

                playVocalSound(question.answer).start()
                // Update UI
                binding.correctCount.text = getString(R.string.correct_count, mCorrectCount)
                binding.wrongCount.text = getString(R.string.wrong_count, mWrongCount)
                binding.questionText.text = question.question
                binding.questionPosition.text =
                    getString(R.string.question_position, mQuestionPosition, mQuestions.size)
                binding.option1.text = question.optionOne
                binding.option2.text = question.optionTwo
                binding.option3.text = question.optionThree
                binding.option4.text = question.optionFour

            }
            else -> {
                optionSound.start()
                // mWrongCount++
            }
        }

//        val isCorrect: Int = when (radioButton.text) {
//            mQuestions[mQuestionPosition - 1].answer -> 1
//            else -> 0
//        }
//
//        // Save per-question data to result detail table
//        postResultDetail(
//            mQuestions[mQuestionPosition - 1],
//            radioButton.text.toString(),
//            isCorrect
//        )

//        // Check if position is last of questions list
//        if (mQuestionPosition == mQuestions.size) {
//
//            val score: Int = (mCorrectCount * 100) / mQuestions.size
//
//            lifecycleScope.launch(Dispatchers.IO) {
//
//                val json = JSONObject()
//                json.put("id", qResult!!.id)
//                json.put("id_kategori", "$categoryId")
//                json.put("skor", score)
//                json.put("tgl_waktu", qResult!!.dateTime)
//
//                val body = json.toString().toRequestBody(MEDIA_TYPE_JSON)
//                val request = Request.Builder()
//                    .url("http://${getString(R.string.ip_address)}/admin/api/update-result.php")
//                    .post(body)
//                    .build()
//                client.newCall(request).execute()
//            }
//
//            val intent = Intent(this, QuizResultActivity::class.java)
//            intent.putExtra("corrects_count", mCorrectCount)
//            intent.putExtra("wrongs_count", mWrongCount)
//            intent.putExtra("game_title", mGameTypes)
//            intent.putExtra("total_questions", mQuestions.size)
//            startActivity(intent)
//            finish()
//            return
//        }

//        // Increment Current Position Of Question From Question List
//        mQuestionPosition++
//        // Set new question data from questions according to position
//        val question = mQuestions[mQuestionPosition - 1]
//
//        playVocalSound(question.answer).start()
//        // Update UI
//        binding.correctCount.text = getString(R.string.correct_count, mCorrectCount)
//        binding.wrongCount.text = getString(R.string.wrong_count, mWrongCount)
//        binding.questionText.text = question.question
//        binding.questionPosition.text =
//            getString(R.string.question_position, mQuestionPosition, mQuestions.size)
//        binding.option1.text = question.optionOne
//        binding.option2.text = question.optionTwo
//        binding.option3.text = question.optionThree
//        binding.option4.text = question.optionFour
    }

    private fun postResultDetail(
        question: Question,
        chosenAnswer: String,
        isCorrect: Int
    ) {
        val json = JSONObject()
        json.put("id_kategori", "$categoryId")
        json.put("id_pertanyaan", "${question.id}")
        json.put("opsi_satu", question.optionOne)
        json.put("opsi_dua", question.optionTwo)
        json.put("opsi_tiga", question.optionThree)
        json.put("opsi_empat", question.optionFour)
        json.put("benar", isCorrect)
        json.put("opsi_dipilih", chosenAnswer)
        json.put("id_hasil", "${qResult!!.id}")

        val body = json.toString().toRequestBody(MEDIA_TYPE_JSON)

        lifecycleScope.launch(Dispatchers.IO) {
            val request = Request.Builder()
                .url("http://${getString(R.string.ip_address)}/admin/api/save-result.php")
                .post(body)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                println(response.body!!.string())
            }
        }
    }

    companion object {
        val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaType()
    }

    private fun playVocalSound(answer: String?): MediaPlayer {
        val listArabicVocal = listOf(
            ArabicVocal("??", R.raw.quiz_wahid),
            ArabicVocal("??", R.raw.quiz_itsnani),
            ArabicVocal("??", R.raw.quiz_tsalatsatun),
            ArabicVocal("??", R.raw.quiz_arbataun),
            ArabicVocal("??", R.raw.quiz_khamsatun),
            ArabicVocal("??", R.raw.quiz_sittatun),
            ArabicVocal("??", R.raw.quiz_sabatun),
            ArabicVocal("??", R.raw.quiz_tsamaniyatun),
            ArabicVocal("??", R.raw.quiz_tisatun),
            ArabicVocal("????", R.raw.quiz_asyartun),
            ArabicVocal("??", R.raw.quiz_dod),
            ArabicVocal("??", R.raw.quiz_syin),
            ArabicVocal("??", R.raw.quiz_sad),
            ArabicVocal("??", R.raw.quiz_dzo),
            ArabicVocal("??", R.raw.quiz_sin),
            ArabicVocal("??", R.raw.quiz_to),
            ArabicVocal("??", R.raw.quiz_tsa),
            ArabicVocal("??", R.raw.quiz_ya),
            ArabicVocal("??", R.raw.quiz_dzal),
            ArabicVocal("??", R.raw.quiz_kof),
            ArabicVocal("??", R.raw.quiz_ba),
            ArabicVocal("??", R.raw.quiz_dal),
            ArabicVocal("??", R.raw.quiz_fa),
            ArabicVocal("??", R.raw.quiz_lam),
            ArabicVocal("??", R.raw.quiz_zain),
            ArabicVocal("??", R.raw.quiz_gain),
            ArabicVocal("??", R.raw.quiz_alif),
            ArabicVocal("??", R.raw.quiz_ro),
            ArabicVocal("??", R.raw.quiz_ain),
            ArabicVocal("??", R.raw.quiz_ta),
            ArabicVocal("??", R.raw.quiz_wa),
            ArabicVocal("????", R.raw.quiz_hak),
            ArabicVocal("??", R.raw.quiz_nun),
            ArabicVocal("??", R.raw.quiz_kho),
            ArabicVocal("??", R.raw.quiz_mim),
            ArabicVocal("??", R.raw.quiz_ha),
            ArabicVocal("??", R.raw.quiz_kaf),
            ArabicVocal("??", R.raw.quiz_jim)
        )
        vocalSound = MediaPlayer.create(
            applicationContext,
            listArabicVocal.find { it.arabic == answer }!!.rawInt
        )
        vocalSound.setVolume(vocalVolume, vocalVolume)
        vocalSound.isLooping = false

        return this.vocalSound
    }

}
