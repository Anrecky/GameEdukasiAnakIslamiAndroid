package com.giovani.game_edukasi_anak_islami

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.giovani.game_edukasi_anak_islami.data.GameTypes
import com.giovani.game_edukasi_anak_islami.databinding.FragmentStartMenuBinding

class StartMenuFragment : Fragment() {

    private var _binding: FragmentStartMenuBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStartMenuBinding.inflate(layoutInflater)
        binding.gameLetterButton.setOnClickListener {
            val intent = Intent(requireActivity(), QuizActivity::class.java).putExtra(
                "quizType",
                GameTypes.Huruf.toString()
            )
            requireActivity().startActivity(intent)
        }
        binding.gameNumberButton.setOnClickListener {
            val intent = Intent(requireActivity(), QuizActivity::class.java).putExtra(
                "quizType",
                GameTypes.Angka.toString()
            )
            requireActivity().startActivity(intent)
        }
        binding.learnLetterButton.setOnClickListener {
            val intent = Intent(requireActivity(), LearnActivity::class.java).putExtra(
                "learnType",
                GameTypes.Huruf.toString()
            )
            requireActivity().startActivity(intent)
        }
        binding.learnNumberButton.setOnClickListener {
            val intent = Intent(requireActivity(), LearnActivity::class.java).putExtra(
                "learnType",
                GameTypes.Angka.toString()
            )
            requireActivity().startActivity(intent)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}