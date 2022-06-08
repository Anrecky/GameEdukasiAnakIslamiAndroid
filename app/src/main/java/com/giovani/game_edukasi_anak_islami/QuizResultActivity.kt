package com.giovani.game_edukasi_anak_islami

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.giovani.game_edukasi_anak_islami.databinding.ActivityQuizResultBinding

class QuizResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQuizResultBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val gameTitle: String = intent.getStringExtra("game_title").toString()
        val correctsCount: Int = intent.getIntExtra("corrects_count", 0)
        val wrongsCount: Int = intent.getIntExtra("wrongs_count", 0)
        val totalQuestion: Int = intent.getIntExtra("total_questions", 0)

        val score: Int = (correctsCount * 100) / totalQuestion
        binding.resultTitle.text = getString(R.string.quiz_result_title, gameTitle)
        binding.correctResult.text = getString(R.string.correct_count, correctsCount)
        binding.wrongResult.text = getString(R.string.wrong_count, wrongsCount)
        binding.score.text = getString(R.string.quiz_score, score)
        binding.homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }

    }
}