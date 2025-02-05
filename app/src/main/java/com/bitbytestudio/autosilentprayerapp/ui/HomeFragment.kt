package com.bitbytestudio.autosilentprayerapp.ui

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.hardware.Sensor
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.palette.graphics.Palette
import com.azan.Azan
import com.azan.Method
import com.azan.astrologicalCalc.SimpleDate
import com.bitbytestudio.autosilentprayerapp.R
import com.bitbytestudio.autosilentprayerapp.databinding.FragmentHomeBinding
import com.bitbytestudio.autosilentprayerapp.prefs.Prefs
import com.bitbytestudio.autosilentprayerapp.ui.compass.CompassViewModel
import com.bitbytestudio.autosilentprayerapp.utils.changeStatusBarColor
import com.bitbytestudio.autosilentprayerapp.utils.exH
import com.bitbytestudio.autosilentprayerapp.utils.twentyFourTo12HourConverter
import com.bitbytestudio.autosilentprayerapp.viewmodel.PrayerViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.GregorianCalendar
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding
    private val compassViewModel by activityViewModels<CompassViewModel>()
    private val prayersViewModel by activityViewModels<PrayerViewModel>()
    private lateinit var mainActivity: MainActivity

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    val PERMISSION_ID = 42
    private var sensorManager: SensorManager? = null
    private var sensor: Sensor? = null

    @Inject
    lateinit var prefs: Prefs


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = (requireActivity() as MainActivity)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("shaheen", "Home: ${prayersViewModel.prayersTime.value}")
        initialize()
    }


    private fun initialize() {

        lifecycleScope.launch {
            prayersViewModel.currentPrayerSession.collectLatest {
                it?.let {
                    updateUI(it.first, it.second)
                }
            }
        }

        mainActivity.myLocationManager?.retryPermissionCallback = object : (Boolean) -> Unit {
            override fun invoke(isGranted: Boolean) {
                binding.reTryBtn.isVisible = isGranted
            }
        }

        binding.reTryBtn.setOnClickListener {
            mainActivity.myLocationManager?.reTryPermission()
        }
    }

    private fun updateUI(prayer: String, prayerTime: String) {
        val prayerResource = when (prayer) {
            "Fajr" -> R.drawable.fazor
            "Dhuhr" -> R.drawable.juhor
            "Asr" -> R.drawable.asor
            "Maghrib" -> R.drawable.magrib
            "Isha" -> R.drawable.isha
            else -> R.drawable.fazor // Default case
        }

        val prayerColor = ContextCompat.getColor(requireContext(), R.color.light_green)
        val prayerBgColor = ContextCompat.getColor(requireContext(), R.color.dim_green)
        val prayerCard = when (prayer) {
            "Fajr" -> binding.fazorCV
            "Dhuhr" -> binding.johorCV
            "Asr" -> binding.asorCV
            "Maghrib" -> binding.magribCV
            "Isha" -> binding.eshaCV
            else -> binding.fazorCV // Default case
        }

        binding.prayerNameTV.text = prayer
        binding.prayerTimeTV.text = prayerTime
        binding.prayerBgIV.setImageResource(prayerResource)
        prayerCard.strokeColor = prayerColor
        prayerCard.setCardBackgroundColor(prayerBgColor)

        backgroundScreenGradient(prayerResource)

        // Update all prayer times text views
            lifecycleScope.launch {
                prayersViewModel.prayersTime.collectLatest {
                    exH {
                    binding.fazorTimeTV.text = it[0].second
                    binding.juhorTimeTV.text = it[1].second
                    binding.asorTimeTV.text = it[2].second
                    binding.magribTimeTV.text = it[3].second
                    binding.eshaTimeTV.text = it[4].second
                }
            }
        }
    }


    private fun timeToMilliSecond(hours: Int, minutes: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hours)
        calendar.set(Calendar.MINUTE, minutes)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        return calendar.timeInMillis
    }

    private fun backgroundScreenGradient(drawable: Int) {
        ContextCompat.getDrawable(requireContext(), drawable)?.toBitmap()?.let {
            Palette.from(it)
                .maximumColorCount(10)
                .generate { palette ->
                    val vibrantSwatch = palette?.vibrantSwatch
                    val lightVibrantSwatch = palette?.lightVibrantSwatch
                    val dominantSwatch = palette?.dominantSwatch
                    val darkVibrantSwatch = palette?.darkVibrantSwatch
                    if (vibrantSwatch != null) {
                        changeBackground(vibrantSwatch)
                        prefs.statusBarColor = vibrantSwatch.rgb
                        mainActivity.changeStatusBarColor(vibrantSwatch.rgb)
                    } else if (lightVibrantSwatch != null) {
                        changeBackground(lightVibrantSwatch)
                        prefs.statusBarColor = lightVibrantSwatch.rgb
                        mainActivity.changeStatusBarColor(lightVibrantSwatch.rgb)
                    } else if (dominantSwatch != null) {
                        changeBackground(dominantSwatch)
                        prefs.statusBarColor = dominantSwatch.rgb
                        mainActivity.changeStatusBarColor(dominantSwatch.rgb)
                    } else if (darkVibrantSwatch != null) {
                        changeBackground(darkVibrantSwatch)
                        prefs.statusBarColor = darkVibrantSwatch.rgb
                        mainActivity.changeStatusBarColor(darkVibrantSwatch.rgb)
                    }
                }
        }
    }

    private fun changeBackground(color: Palette.Swatch) {
        binding.root.setBackgroundColor(color.rgb)
        binding.prayersTimeContainerLayout.background = createGradientDrawable(color)
    }

    private fun createGradientDrawable(palette: Palette.Swatch): GradientDrawable? {
        return try {
            GradientDrawable(
                GradientDrawable.Orientation.BOTTOM_TOP,
                intArrayOf(
                    ContextCompat.getColor(requireActivity(), R.color.white), //requireContext().getColor(R.color.bg)
                    palette.rgb
                )
            ).apply { cornerRadius = 0f }
        } catch (e: Exception) {
            null
        }
    }

    companion object {
        const val WORKER_TAG = "PRAYERS_WORKER"
        const val WORKER_NAME = "WORKER_NAME"
        fun newInstance() = HomeFragment().apply {
            arguments = Bundle().apply {

            }
        }
    }
}