package com.example.prayerapp.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.prayerapp.R
import com.example.prayerapp.databinding.FragmentCountBinding
import com.example.prayerapp.prefs.Prefs
import com.google.android.material.card.MaterialCardView
//import com.google.android.gms.ads.AdLoader
//import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CountFragment : Fragment() {

    private lateinit var binding : FragmentCountBinding
    //lateinit var adLoader: AdLoader
    private var countValue = 0
    private var countMaxValue = 0
    private var selectedValue = 0
    private var selectedName = ""

    @Inject
    lateinit var prefs: Prefs


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCountBinding.inflate(inflater)
        prefs = Prefs(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //MobileAds.initialize(requireContext()) {}

        initialize()
    }

    private fun initialize() {
        //adShow()

        selectCounterValue()
        counter()
    }

    private fun selectCounterValue() {
        selectedValue = prefs.selectedValue
        selectedName = prefs.selectedName.toString()
        binding.countSelectTv.text = "Selected $selectedName : $selectedValue"

        binding.buttonOne.setOnClickListener {
            countMaxValue = 33
            selectedName = "Alhamdulliah"
            selectedValue = countMaxValue
            binding.countSelectTv.text = "Selected $selectedName : $selectedValue"
            binding.progressBar.max = countMaxValue
            prefs.selectedValue = selectedValue
            prefs.selectedName = selectedName
        }

        binding.buttonTwo.setOnClickListener {
            countMaxValue = 33
            selectedName = "Allahu Akbar"
            selectedValue = countMaxValue
            binding.countSelectTv.text = "Selected $selectedName : $selectedValue"
            binding.progressBar.max = countMaxValue
            prefs.selectedValue = selectedValue
            prefs.selectedName = selectedName
        }

        binding.buttonThree.setOnClickListener {
            countMaxValue = 33
            selectedName = "Subhanallah"
            selectedValue = countMaxValue
            binding.countSelectTv.text = "Selected $selectedName : $selectedValue"
            binding.progressBar.max = countMaxValue
            prefs.selectedValue = selectedValue
            prefs.selectedName = selectedName
        }

    }

//    private fun adShow() {
//        adLoader = AdLoader.Builder(requireContext(), "ca-app-pub-3940256099942544/2247696110")
//            .forNativeAd {
//                binding.nativeAd.setNativeAd(it)
//                Log.e("","")
//            }.build()
//        adLoader.loadAd(AdRequest.Builder().build())
//    }

    private fun counter() {
        var count = prefs.counterValue
        binding.progressBar.max = selectedValue
        incrementProgress(count)
        binding.countMainTV.text = "Count $count"

        binding.countIV.setOnClickListener {
            incrementProgress(1)
        }

        binding.resetIV.setOnClickListener {
            countValue = 0
            binding.progressBar.progress = countValue
            binding.countMainTV.text = "Count $countValue"
            prefs.counterValue = countValue
            prefs.selectedName = "Null"
        }
    }

    private fun incrementProgress(incrementValue: Int) {
        val currentProgress = binding.progressBar.progress
        val newProgress = currentProgress + incrementValue

        if (newProgress <= binding.progressBar.max) {
            binding.progressBar.progress = newProgress
            binding.countMainTV.text = "Count $newProgress"
            prefs.counterValue = newProgress
        } else {
            binding.progressBar.progress = binding.progressBar.max
            binding.countMainTV.text = "Count ${binding.progressBar.max}"
        }
    }

    companion object {
        fun newInstance() = CountFragment().apply {
            arguments = Bundle().apply {

            }
        }
    }
}