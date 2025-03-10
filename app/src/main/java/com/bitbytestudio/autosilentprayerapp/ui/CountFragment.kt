package com.bitbytestudio.autosilentprayerapp.ui

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bitbytestudio.autosilentprayerapp.R
import com.bitbytestudio.autosilentprayerapp.databinding.FragmentCountBinding
import com.bitbytestudio.autosilentprayerapp.prefs.Prefs
import com.bitbytestudio.autosilentprayerapp.utils.rotateClockwiseAnimated
import com.bitbytestudio.autosilentprayerapp.viewmodel.PrayerViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CountFragment : Fragment() {

    private lateinit var binding : FragmentCountBinding
    private var isCardBackgroundSelected = false

    private val prayersViewModel by viewModels<PrayerViewModel>()

    @Inject
    lateinit var prefs: Prefs


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCountBinding.inflate(inflater)
        //prefs = Prefs(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.counterTB.backgroundTintList = ColorStateList.valueOf(prefs.statusBarColor)

        initialize()
        clickListener()
    }

    private fun clickListener(){
        binding.buttonOne.setOnClickListener {
            binding.buttonOne.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.dim_green))
            binding.buttonTwo.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.buttonThree.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
            prefs.selectedValue = "Alhamdulliah"

            var tasbihModel = prefs.get<Prefs.TasbihModel>(prefs.selectedValue)
            Log.d("check_counter","$tasbihModel")

            if (tasbihModel != null){
                tasbihModel.name = "Alhamdulliah"
                tasbihModel.selectedBtnText = "Alhamdulliah"
            }else{
                tasbihModel = Prefs.TasbihModel(totalCount = 0, maxCount = 33, name = "Alhamdulliah", selectedBtnText = "Alhamdulliah")
            }

            prefs.save(tasbihModel, prefs.selectedValue)
            binding.countSelectTv.text = requireContext().getString(R.string.alhamdulliah)

            counter()
        }

        binding.buttonTwo.setOnClickListener {
            binding.buttonTwo.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.dim_green))
            binding.buttonOne.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.buttonThree.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
            prefs.selectedValue = "Allahu Akbar"

            var tasbihModel = prefs.get<Prefs.TasbihModel>(prefs.selectedValue)
            Log.d("check_counter","$tasbihModel")
            if (tasbihModel != null){
                tasbihModel?.name = "Allahu Akbar"
                tasbihModel?.selectedBtnText = "Allahu Akbar"
            }else{
                tasbihModel = Prefs.TasbihModel(totalCount = 0, maxCount = 34, name = "Allahu Akbar", selectedBtnText = "Allahu Akbar")
            }

            prefs.save(tasbihModel, prefs.selectedValue)

            binding.countSelectTv.text = requireContext().getString(R.string.allahu_akbar)

            counter()
        }

        binding.buttonThree.setOnClickListener {
            binding.buttonThree.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.dim_green))
            binding.buttonOne.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.buttonTwo.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
            prefs.selectedValue = "Subhanallah"

            var tasbihModel = prefs.get<Prefs.TasbihModel>(prefs.selectedValue)
            if (tasbihModel != null){
                tasbihModel?.name = "Subhanallah"
                tasbihModel?.selectedBtnText = "Subhanallah"
            }else{
                tasbihModel = Prefs.TasbihModel(totalCount = 0, maxCount = 33, name = "Subhanallah", selectedBtnText = "Subhanallah")
            }
            Log.d("check_counter","$tasbihModel")

            prefs.save(tasbihModel, prefs.selectedValue)

            binding.countSelectTv.text = requireContext().getString(R.string.subhanallah)

            counter()
        }

        var tasbihModel = prefs.get<Prefs.TasbihModel>(prefs.selectedValue)
        Log.d("check_counter","$tasbihModel")
        if (tasbihModel == null) binding.buttonThree.performClick()
    }

    private fun initialize() {
        selectCounterValue()
    }

    private fun selectCounterValue() {
        val tasbihModel = prefs.get<Prefs.TasbihModel>(prefs.selectedValue)
        Log.d("check_counter","$tasbihModel")
        binding.countSelectTv.text = when(tasbihModel?.name){
            "Alhamdulliah" ->{
                requireContext().getString(R.string.alhamdulliah)
            }
            "Allahu Akbar" ->{
                requireContext().getString(R.string.allahu_akbar)
            }
            "Subhanallah" ->{
                requireContext().getString(R.string.subhanallah)
            }
            else -> {
                requireContext().getString(R.string.subhanallah)
            }
        }

        when(prefs.selectedValue){
            "Alhamdulliah" ->{
                binding.buttonOne.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.dim_green))
                binding.buttonTwo.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                binding.buttonThree.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
            "Allahu Akbar" ->{
                binding.buttonTwo.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.dim_green))
                binding.buttonOne.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                binding.buttonThree.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
            "Subhanallah" ->{
                binding.buttonThree.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.dim_green))
                binding.buttonOne.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                binding.buttonTwo.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
        }
        counter()
    }

    private fun counter() {
        val tasbihModel = prefs.get<Prefs.TasbihModel>(prefs.selectedValue)
        val totalCount = tasbihModel?.totalCount?:0
        val maxCount = when(prefs.selectedValue){
            "Subhanallah" -> 33
            "Alhamdulliah" -> 33
            "Allahu Akbar" -> 34
            else -> {33}
        }
        binding.progressBar.max = maxCount
        binding.progressBar.progress = totalCount
        binding.countMainTV.text = "$totalCount"
        binding.countIV.setOnClickListener {
            incrementProgress()
        }

        binding.resetIV.setOnClickListener {
            it.rotateClockwiseAnimated()
            reset()
        }
    }

    private fun reset() {
        binding.progressBar.progress = 0
        binding.countMainTV.text = "0"
        var tasbihModel = prefs.get<Prefs.TasbihModel>(prefs.selectedValue)
        tasbihModel = Prefs.TasbihModel(totalCount = 0, maxCount = 33, name = tasbihModel?.name?:"", selectedBtnText = tasbihModel?.selectedBtnText?:":")
        prefs.save(tasbihModel, prefs.selectedValue)
    }

    private fun incrementProgress() {
        var tasbihModel = prefs.get<Prefs.TasbihModel>(prefs.selectedValue)
        var totalCount = tasbihModel?.totalCount?:0
        if (totalCount < binding.progressBar.max) {
            totalCount++
            binding.progressBar.progress = totalCount
            binding.countMainTV.text = "$totalCount"
            tasbihModel?.totalCount = totalCount
            prefs.save(tasbihModel, prefs.selectedValue)
        } else {
            reset()
        }
    }

    companion object {
        fun newInstance() = CountFragment().apply {
            arguments = Bundle().apply {

            }
        }
    }
}