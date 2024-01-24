package com.example.prayerapp.ui

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.palette.graphics.Palette
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.azan.Azan
import com.azan.Method
import com.azan.astrologicalCalc.Location
import com.azan.astrologicalCalc.SimpleDate
import com.example.prayerapp.R
import com.example.prayerapp.databinding.FragmentHomeBinding
import com.example.prayerapp.prefs.Prefs
import com.example.prayerapp.ui.MainActivity.Companion.mainActivity
import com.example.prayerapp.utils.changeStatusBarColor
import com.example.prayerapp.utils.twentyFourTo12HourConverter
import com.example.prayerapp.worker.PrayersWorker
//import com.google.android.gms.ads.AdListener
//import com.google.android.gms.ads.AdLoader
//import com.google.android.gms.ads.AdRequest
//import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding
    private lateinit var workManager: WorkManager
    private lateinit var periodicWorkRequest: PeriodicWorkRequest
    private val WORKER_TAG = "PRAYERS_WORKER"
//    lateinit var adRequest : AdRequest
//    lateinit var adLoader: AdLoader

    @Inject
    lateinit var prefs: Prefs

    private var isPermission = false
    private var checkNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        isPermission = isGranted
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)
        prefs = Prefs(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //MobileAds.initialize(requireContext()) {}

        initialize()
        dailyOneTimeRunWorkerTrigger()
    }

//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//
//    }

//    override fun onDetach() {
//        super.onDetach()
//    }

    private fun initialize() {
        //adShow()
        setDNDModePolicy()
        checkPermission()
        showTime()
    }

//    private fun adShow() {
//        adRequest = AdRequest.Builder().build()
//        binding.adView.loadAd(adRequest)
//        binding.adView.adListener = object : AdListener() {
//            override fun onAdLoaded() {
//                super.onAdLoaded()
//
//            }
//        }
//    }

    private fun setDNDModePolicy() {
        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (!notificationManager.isNotificationPolicyAccessGranted) {
            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            startActivity(intent)
        }
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                isPermission = true
            } else {
                isPermission = false
                checkNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            isPermission = true
        }
    }

    private fun showTime() {
        val today = SimpleDate(GregorianCalendar())
        val location = Location(23.8103, 90.4125, +6.0, 0)
        val azan = Azan(location, Method.KARACHI_HANAF)
        val prayerTimes = azan.getPrayerTimes(today)

        val fajrTimeInMilliseconds = timeToMilliSecond(prayerTimes.fajr().hour, prayerTimes.fajr().minute)
        val juhorTimeInMilliseconds = timeToMilliSecond(prayerTimes.thuhr().hour, prayerTimes.thuhr().minute)
        val asorTimeInMilliseconds = timeToMilliSecond(prayerTimes.assr().hour, prayerTimes.assr().minute)
        val magribTimeInMilliseconds = timeToMilliSecond(prayerTimes.maghrib().hour, prayerTimes.maghrib().minute)
        val ishaTimeInMilliseconds = timeToMilliSecond(prayerTimes.ishaa().hour, prayerTimes.ishaa().minute)

        if (System.currentTimeMillis() in ((fajrTimeInMilliseconds + (30 * 60000)) + 1)..(juhorTimeInMilliseconds + (3 * 3600000))) {
            binding.prayerBgIV.setImageResource(R.drawable.juhor)
            binding.prayerNameTV.text = "Juhor Namaz"
            binding.prayerTimeTV.text = prayerTimes.thuhr().twentyFourTo12HourConverter()
            binding.johorCV.strokeColor = ContextCompat.getColor(requireContext(), R.color.light_green)
            binding.johorCV.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.dim_green))
            backgroundScreenGradient(R.drawable.juhor)
        }else if (System.currentTimeMillis() in ((juhorTimeInMilliseconds + (3 * 3600000)) + 1)..(asorTimeInMilliseconds + 3600000)) {
            binding.prayerBgIV.setImageResource(R.drawable.asor)
            binding.prayerNameTV.text = "Asor Namaz"
            binding.prayerTimeTV.text = prayerTimes.assr().twentyFourTo12HourConverter()
            binding.asorCV.strokeColor = ContextCompat.getColor(requireContext(), R.color.light_green)
            binding.asorCV.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.dim_green))
            backgroundScreenGradient(R.drawable.asor)
        }else if (System.currentTimeMillis() in ((asorTimeInMilliseconds + 3600000) + 1)..(magribTimeInMilliseconds + (20 * 60000))) {
            binding.prayerBgIV.setImageResource(R.drawable.magrib)
            binding.prayerNameTV.text = "Magrib Namaz"
            binding.prayerTimeTV.text = prayerTimes.maghrib().twentyFourTo12HourConverter()
            binding.magribCV.strokeColor = ContextCompat.getColor(requireContext(), R.color.light_green)
            binding.magribCV.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.dim_green))
            backgroundScreenGradient(R.drawable.magrib)
        }else if (System.currentTimeMillis() in ((magribTimeInMilliseconds + (20 * 60000)) + 1)..(ishaTimeInMilliseconds + (5 * 3600000))) {
            binding.prayerBgIV.setImageResource(R.drawable.isha)
            binding.prayerNameTV.text = "Isha Namaz"
            binding.prayerTimeTV.text = prayerTimes.ishaa().twentyFourTo12HourConverter()
            binding.eshaCV.strokeColor = ContextCompat.getColor(requireContext(), R.color.light_green)
            binding.eshaCV.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.dim_green))
            backgroundScreenGradient(R.drawable.isha)
        }else if (System.currentTimeMillis() in ((ishaTimeInMilliseconds + (10 * 3600000)) + 1)..(fajrTimeInMilliseconds + (30 * 60000))) {
            binding.prayerBgIV.setImageResource(R.drawable.fazor)
            binding.prayerNameTV.text = "Fazor Namaz"
            binding.prayerTimeTV.text = prayerTimes.fajr().twentyFourTo12HourConverter()
            binding.fazorCV.strokeColor = ContextCompat.getColor(requireContext(), R.color.light_green)
            binding.fazorCV.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.dim_green))
            backgroundScreenGradient(R.drawable.fazor)
        }

        binding.fazorTimeTV.text = prayerTimes.fajr().twentyFourTo12HourConverter()
        binding.juhorTimeTV.text = prayerTimes.thuhr().twentyFourTo12HourConverter()
        binding.asorTimeTV.text = prayerTimes.assr().twentyFourTo12HourConverter()
        binding.magribTimeTV.text = prayerTimes.maghrib().twentyFourTo12HourConverter()
        binding.eshaTimeTV.text = prayerTimes.ishaa().twentyFourTo12HourConverter()
    }

    fun timeToMilliSecond(hours: Int, minutes: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hours)
        calendar.set(Calendar.MINUTE, minutes)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        return calendar.timeInMillis
    }

//    fun isTimeInBetween(timeToCheck: Long, startTime: Long, endTime: Long): Boolean {
//        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
//
//        try {
//            val timeToCheckDate = dateFormat.parse(timeToCheck.toString())
//            val startTimeDate = dateFormat.parse(startTime.toString())
//            val endTimeDate = dateFormat.parse(endTime.toString())
//            Log.d("time_check","timeToCheckDate $timeToCheckDate startTimeDate $startTimeDate endTimeDate $endTimeDate")
//
//             Check if the time is between the start and end times
//            return timeToCheckDate in startTimeDate..endTimeDate
//            return timeToCheck in startTime..endTime
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//        // Return false in case of any exception or invalid input
//        return false
//    }

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
                        mainActivity.changeStatusBarColor(vibrantSwatch.rgb)
                    } else if (lightVibrantSwatch != null) {
                        changeBackground(lightVibrantSwatch)
                        mainActivity.changeStatusBarColor(lightVibrantSwatch.rgb)
                    } else if (dominantSwatch != null) {
                        changeBackground(dominantSwatch)
                        mainActivity.changeStatusBarColor(dominantSwatch.rgb)
                    } else if (darkVibrantSwatch != null) {
                        changeBackground(darkVibrantSwatch)
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

    private fun dailyOneTimeRunWorkerTrigger() {
        val today = SimpleDate(GregorianCalendar())
        val location = Location(23.8103, 90.4125, +6.0, 0)
        val azan = Azan(location, Method.KARACHI_HANAF)
        val prayerTimes = azan.getPrayerTimes(today)

        workManager = WorkManager.getInstance(requireContext())

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val data = Data.EMPTY

        periodicWorkRequest = PeriodicWorkRequestBuilder<PrayersWorker>(12, TimeUnit.HOURS)
            .setInputData(data)
            .setConstraints(constraints)
            .addTag(WORKER_TAG)
            .setBackoffCriteria(BackoffPolicy.LINEAR, PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS, TimeUnit.MILLISECONDS)
            .build()


        workManager.enqueueUniquePeriodicWork("Prayers Worker", ExistingPeriodicWorkPolicy.UPDATE, periodicWorkRequest)

        workManager.getWorkInfoByIdLiveData(periodicWorkRequest.id).observe(viewLifecycleOwner){
            when(it.state){
                WorkInfo.State.ENQUEUED -> {
                    Log.d("PRAYERS_WORKER", "ENQUEUED : ${it.progress}")
                }
                WorkInfo.State.RUNNING -> {
                    Log.d("PRAYERS_WORKER", "RUNNING : ${it.progress}")
                }
                WorkInfo.State.SUCCEEDED -> {
                    Log.d("PRAYERS_WORKER", "SUCCEEDED : ${it.progress}")
                }
                WorkInfo.State.CANCELLED -> {
                    Log.d("PRAYERS_WORKER", "CANCELLED : ${it.progress}")
                }
                WorkInfo.State.BLOCKED -> {
                    Log.d("PRAYERS_WORKER", "BLOCKED : ${it.progress}")
                }
                else ->{
                    Log.d("PRAYERS_WORKER", "else : ${it.progress}")
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        //binding.adView.pause()
    }

    override fun onResume() {
        super.onResume()
        //binding.adView.resume()
    }

    override fun onDestroy() {
        super.onDestroy()
        //binding.adView.destroy()
    }
}