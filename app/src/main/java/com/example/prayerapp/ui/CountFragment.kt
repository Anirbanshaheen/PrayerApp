package com.example.prayerapp.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.prayerapp.R
import com.example.prayerapp.databinding.FragmentCountBinding
import com.example.prayerapp.databinding.FragmentHomeBinding
import com.example.prayerapp.prefs.Prefs
import com.example.prayerapp.utils.changeFragment
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CountFragment : Fragment() {

    private lateinit var binding : FragmentCountBinding
    lateinit var adLoader: AdLoader

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

        MobileAds.initialize(requireContext()) {}

        initialize()
    }

    private fun initialize() {
        adShow()
        counter()
    }

    private fun adShow() {
        adLoader = AdLoader.Builder(requireContext(), "ca-app-pub-3940256099942544/2247696110")
            .forNativeAd {
                binding.nativeAd.setNativeAd(it)
                Log.e("","")
            }.build()
        adLoader.loadAd(AdRequest.Builder().build())
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