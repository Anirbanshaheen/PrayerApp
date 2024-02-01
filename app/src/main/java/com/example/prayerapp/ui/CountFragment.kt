package com.example.prayerapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.prayerapp.databinding.FragmentCountBinding
import com.example.prayerapp.prefs.Prefs
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
        binding.countSelectTv.text = "Selected : $selectedValue"

        binding.buttonOne.setOnClickListener {
            countMaxValue = 33
            selectedValue = countMaxValue
            binding.countSelectTv.text = "Selected : $selectedValue"
            binding.progressBar.max = countMaxValue
            prefs.selectedValue = selectedValue
        }

        binding.buttonTwo.setOnClickListener {
            countMaxValue = 34
            selectedValue = countMaxValue
            binding.countSelectTv.text = "Selected : $selectedValue"
            binding.progressBar.max = countMaxValue
            prefs.selectedValue = selectedValue
        }

        binding.buttonThree.setOnClickListener {
            countMaxValue = 35
            selectedValue = countMaxValue
            binding.countSelectTv.text = "Selected : $selectedValue"
            binding.progressBar.max = countMaxValue
            prefs.selectedValue = selectedValue
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