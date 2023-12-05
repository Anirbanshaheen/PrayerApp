package com.example.prayerapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.prayerapp.R
import com.example.prayerapp.databinding.FragmentCountBinding
import com.example.prayerapp.databinding.FragmentHomeBinding
import com.example.prayerapp.prefs.Prefs
import com.example.prayerapp.utils.changeFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CountFragment : Fragment() {

    private lateinit var binding : FragmentCountBinding

    @Inject
    lateinit var prefs: Prefs

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCountBinding.inflate(inflater)
        prefs = Prefs(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    private fun initialize() {
        counter()
    }

    private fun counter() {
        var count = prefs.counterValue
        binding.countTV.text = count.toString()
        binding.countBt.setOnClickListener {
            count++
            binding.countTV.text = count.toString()
            prefs.counterValue = count
        }

        binding.resetBt.setOnClickListener {
            count = 0
            binding.countTV.text = count.toString()
            prefs.counterValue = count
        }
    }
}