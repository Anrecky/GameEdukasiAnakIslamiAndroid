package com.giovani.game_edukasi_anak_islami

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.giovani.game_edukasi_anak_islami.R
import com.giovani.game_edukasi_anak_islami.databinding.FragmentHomeBinding
import kotlin.system.exitProcess

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.startButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_startMenuFragment)
        }
        binding.settingsButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
        }
        binding.aboutButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_aboutFragment)
        }
        binding.exitButton.setOnClickListener {
            requireActivity().finishAffinity()
            exitProcess(0)
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}