package com.giovani.game_edukasi_anak_islami

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import androidx.lifecycle.lifecycleScope
import com.giovani.game_edukasi_anak_islami.data.Question
import com.giovani.game_edukasi_anak_islami.databinding.ActivityQuizBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request


@SuppressLint("SetTextI18n")
class QuizActivity : AppCompatActivity() {
    internal data class ArabicVocal(val arabic: String, val rawInt: Int)

    private lateinit var binding: ActivityQuizBinding
    private lateinit var mGameTypes: String
    private var mQuestions: List<Question> = emptyList()
    private var mQuestionPosition: Int = 1
    private var mCorrectCount: Int = 0
    private var mWrongCount: Int = 0
    private val client: OkHttpClient = OkHttpClient()
    private lateinit var vocalSound: MediaPlayer

    private fun fetchQuestions(categoryId: Int): Job = lifecycleScope.launch {
        withContext(Dispatchers.IO) {
            try {
                // Build request
                val request = Request.Builder()
                    .url("http://${getString(R.string.ip_address)}/admin/api/read.php?id_kategori=$categoryId")
                    .build()
                // Execute request
                @Suppress("BlockingMethodInNonBlockingContext") val res =
                    client.newCall(request).execute().body?.string()

                if (res != null) {
                    try {
                        // Parse result string JSON to data class
                        mQuestions = Json.decodeFromString(res)
                    } catch (err: Error) {
                        print("Error when parsing JSON: " + err.localizedMessage)
                    }
                } else {
                    print("Error: Get request returned no response")
                }
            } catch (err: Error) {
                print("Error when executing get request: " + err.localizedMessage)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set the game type passed from menu
        mGameTypes = intent.getStringExtra("quizType").toString()

        // Get fetchQuestions job
        val fetchJob = when (mGameTypes) {
            "Angka" -> fetchQuestions(1)
            else -> fetchQuestions(2)
        }
        // Run Coroutine with join the fetchJob and update ui
        lifecycleScope.launch(Dispatchers.Main) {
            fetchJob.join()
            // Get Initialize Question From Questions Fetching
            val question = mQuestions[mQuestionPosition - 1]
            // Play Vocal Sound
            playVocalSound(question.answer).start()

            // Initialize UI
            binding.gameTitle.text = getString(R.string.game_title, mGameTypes)
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

        // Home Button
        binding.homeButton.setOnClickListener {
            if (vocalSound.isPlaying) {
                vocalSound.stop()
                vocalSound.release()
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
            vocalSound.release()
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
            vocalSound.release()
        }

        if (optionSound.isPlaying) optionSound.reset()

        // Check if correct answer
        when (radioButton.text) {
            mQuestions[mQuestionPosition - 1].answer -> {
                optionSound.start()
                mCorrectCount++
            }
            else -> {
                optionSound.start()
                mWrongCount++
            }
        }
        // Check if position is last of questions list
        if (mQuestionPosition == mQuestions.size) {
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

    private fun playVocalSound(answer: String?): MediaPlayer {
        val listArabicVocal = listOf(
            ArabicVocal("a", R.raw.belajar_ain),
            ArabicVocal("١", R.raw.quiz_wahid),
            ArabicVocal("٢", R.raw.quiz_itsnani),
            ArabicVocal("٣", R.raw.quiz_tsalatsatun),
            ArabicVocal("٤", R.raw.quiz_arbataun),
            ArabicVocal("٥", R.raw.quiz_khamsatun),
            ArabicVocal("٦", R.raw.quiz_sittatun),
            ArabicVocal("٧", R.raw.quiz_sabatun),
            ArabicVocal("٨", R.raw.quiz_tsamaniyatun),
            ArabicVocal("٩", R.raw.quiz_tisatun),
            ArabicVocal("١٠", R.raw.quiz_asyartun),
            ArabicVocal("ض", R.raw.quiz_dod),
            ArabicVocal("ش", R.raw.quiz_syin),
            ArabicVocal("ص", R.raw.quiz_sad),
            ArabicVocal("ظ", R.raw.quiz_dzo),
            ArabicVocal("س", R.raw.quiz_sin),
            ArabicVocal("ط", R.raw.quiz_to),
            ArabicVocal("ث", R.raw.quiz_tsa),
            ArabicVocal("ي", R.raw.quiz_ya),
            ArabicVocal("ذ", R.raw.quiz_dzal),
            ArabicVocal("ق", R.raw.quiz_kof),
            ArabicVocal("ب", R.raw.quiz_ba),
            ArabicVocal("د", R.raw.quiz_dal),
            ArabicVocal("ف", R.raw.quiz_fa),
            ArabicVocal("ل", R.raw.quiz_lam),
            ArabicVocal("ز", R.raw.quiz_zain),
            ArabicVocal("غ", R.raw.quiz_gain),
            ArabicVocal("ا", R.raw.quiz_alif),
            ArabicVocal("ر", R.raw.quiz_ro),
            ArabicVocal("ع", R.raw.quiz_ain),
            ArabicVocal("ت", R.raw.quiz_ta),
            ArabicVocal("و", R.raw.quiz_wa),
            ArabicVocal("هـ", R.raw.quiz_hak),
            ArabicVocal("ن", R.raw.quiz_nun),
            ArabicVocal("خ", R.raw.quiz_kho),
            ArabicVocal("م", R.raw.quiz_mim),
            ArabicVocal("ح", R.raw.quiz_ha),
            ArabicVocal("ك", R.raw.quiz_kaf),
            ArabicVocal("ج", R.raw.quiz_jim)
        )
        vocalSound = MediaPlayer.create(
            applicationContext,
            listArabicVocal.find { it.arabic == answer }!!.rawInt
        )
        vocalSound.isLooping = false

        return this.vocalSound
    }

}
