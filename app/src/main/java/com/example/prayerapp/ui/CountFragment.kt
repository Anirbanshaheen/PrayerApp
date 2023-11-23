package com.example.prayerapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.prayerapp.R
import com.example.prayerapp.databinding.FragmentCountBinding
import com.example.prayerapp.databinding.FragmentHomeBinding
import com.example.prayerapp.utils.changeFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CountFragment : Fragment() {

    private lateinit var binding : FragmentCountBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCountBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    private fun initialize() {

    }
}