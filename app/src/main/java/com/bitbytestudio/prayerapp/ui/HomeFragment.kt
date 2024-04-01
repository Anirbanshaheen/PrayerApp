package com.bitbytestudio.prayerapp.ui

//import com.google.android.gms.ads.AdListener
//import com.google.android.gms.ads.AdLoader
//import com.google.android.gms.ads.AdRequest
//import com.google.android.gms.ads.MobileAds
import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.GradientDrawable
import android.hardware.Sensor
import android.hardware.SensorManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
import com.azan.astrologicalCalc.SimpleDate
import com.bitbytestudio.prayerapp.R
import com.bitbytestudio.prayerapp.databinding.FragmentHomeBinding
import com.bitbytestudio.prayerapp.prefs.Prefs
import com.bitbytestudio.prayerapp.ui.MainActivity.Companion.mainActivity
import com.bitbytestudio.prayerapp.ui.compass.CompassViewModel
import com.bitbytestudio.prayerapp.utils.changeStatusBarColor
import com.bitbytestudio.prayerapp.utils.twentyFourTo12HourConverter
import com.bitbytestudio.prayerapp.viewmodel.PrayerViewModel
import com.bitbytestudio.prayerapp.worker.PrayersWorker
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding
    private val compassViewModel by activityViewModels<CompassViewModel>()
    private val prayersViewModel by activityViewModels<PrayerViewModel>()
    private lateinit var a: MainActivity

    private lateinit var workManager: WorkManager
    private lateinit var periodicWorkRequest: PeriodicWorkRequest
    private lateinit var notificationManager: NotificationManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    val PERMISSION_ID = 42
    private var sensorManager: SensorManager? = null
    private var sensor: Sensor? = null
    private val WORKER_TAG = "PRAYERS_WORKER"
    private val WORKER_NAME = "WORKER_NAME"
//    lateinit var adRequest : AdRequest
//    lateinit var adLoader: AdLoader

    @Inject
    lateinit var prefs: Prefs

    private var isPermission = false
    private var checkNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        isPermission = isGranted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.SCHEDULE_EXACT_ALARM) == PackageManager.PERMISSION_GRANTED) {
                isPermission = true
            } else {
                isPermission = false
                checkExactAlarmPermission.launch(Manifest.permission.SCHEDULE_EXACT_ALARM)
            }
        }
    }

    private var checkExactAlarmPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        isPermission = isGranted
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        a = (requireActivity() as MainActivity)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)
        //prefs = Prefs(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //MobileAds.initialize(requireContext()) {}

        initialize()
        locationOn()
        dailyOneTimeRunWorkerTrigger()
    }

    private fun locationOn() {
        if (notificationManager.isNotificationPolicyAccessGranted) {
            a.myLocationManager?.initialize()
        }

        a.myLocationManager?.locationCallback = object : (Location?) -> Unit {
            override fun invoke(location: Location?) {
                prefs.currentLat = location?.latitude ?: 23.8103  // default dhaka location
                prefs.currentLon = location?.longitude ?: 90.4125
                getPrayersTime()
                Log.d("locationCallback", "\nlatitude: ${prefs.currentLat}\nlongitude: ${prefs.currentLon}")
            }
        }

        a.myLocationManager?.retryPermissionCallback = object : (Boolean) -> Unit {
            override fun invoke(isGranted: Boolean) {
                binding.reTryBtn.isVisible = isGranted
            }
        }

        binding.reTryBtn.setOnClickListener {
            a.myLocationManager?.reTryPermission()
        }
    }

    private fun initialize() {
        //adShow()
        notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        checkPermission()
        setDNDModePolicy()
        getPrayersTime()
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

    private val requestNotificationPolicyAccess = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            a.myLocationManager?.initialize()
            dailyOneTimeRunWorkerTrigger()
            Toast.makeText(requireContext(), "Granted", Toast.LENGTH_SHORT).show()

        } else {
            a.myLocationManager?.initialize()
            dailyOneTimeRunWorkerTrigger()

        }
    }

    private fun requestNotificationPolicyAccess() {
        val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
        requestNotificationPolicyAccess.launch(intent)
    }

    private fun setDNDModePolicy() {
        if (!notificationManager.isNotificationPolicyAccessGranted) {
            requestNotificationPolicyAccess()
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

    private fun getPrayersTime() {
        val today = SimpleDate(GregorianCalendar())
        val location =
            com.azan.astrologicalCalc.Location(prefs.currentLat, prefs.currentLon, +6.0, 0)
        val azan = Azan(location, Method.KARACHI_HANAF)
        val prayerTimes = azan.getPrayerTimes(today)
        Log.d("showTime", "prayerTimes : $prayerTimes")
        val fajrTimeInMilliseconds =
            timeToMilliSecond(prayerTimes.fajr().hour, prayerTimes.fajr().minute)
        val juhorTimeInMilliseconds =
            timeToMilliSecond(prayerTimes.thuhr().hour, prayerTimes.thuhr().minute)
        val asorTimeInMilliseconds =
            timeToMilliSecond(prayerTimes.assr().hour, prayerTimes.assr().minute)
        val magribTimeInMilliseconds =
            timeToMilliSecond(prayerTimes.maghrib().hour, prayerTimes.maghrib().minute)
        val ishaTimeInMilliseconds =
            timeToMilliSecond(prayerTimes.ishaa().hour, prayerTimes.ishaa().minute)

        val fajrTimeString = prayerTimes.fajr().twentyFourTo12HourConverter()
        val juhorTimeString = prayerTimes.thuhr().twentyFourTo12HourConverter()
        val asorTimeString = prayerTimes.assr().twentyFourTo12HourConverter()
        val magribTimeString = prayerTimes.maghrib().twentyFourTo12HourConverter()
        val ishaTimeString = prayerTimes.ishaa().twentyFourTo12HourConverter()

        if (System.currentTimeMillis() in ((ishaTimeInMilliseconds + (10 * 3600000)) + 1)..(fajrTimeInMilliseconds + (30 * 60000))) {
            binding.prayerNameTV.text = requireActivity().getString(R.string.fajr_time)
            binding.prayerTimeTV.text = fajrTimeString
            binding.prayerBgIV.setImageResource(R.drawable.fazor)
            binding.fazorCV.strokeColor =
                ContextCompat.getColor(requireContext(), R.color.light_green)
            binding.fazorCV.setCardBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.dim_green
                )
            )
            backgroundScreenGradient(R.drawable.fazor)
        } else if (System.currentTimeMillis() in ((fajrTimeInMilliseconds + (30 * 60000)) + 1)..(juhorTimeInMilliseconds + (3 * 3600000))) {
            binding.prayerNameTV.text = requireActivity().getString(R.string.dhuhr_time)
            binding.prayerTimeTV.text = juhorTimeString
            binding.prayerBgIV.setImageResource(R.drawable.juhor)
            binding.johorCV.strokeColor =
                ContextCompat.getColor(requireContext(), R.color.light_green)
            binding.johorCV.setCardBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.dim_green
                )
            )
            backgroundScreenGradient(R.drawable.juhor)
        } else if (System.currentTimeMillis() in ((juhorTimeInMilliseconds + (3 * 3600000)) + 1)..(asorTimeInMilliseconds + 3600000)) {
            binding.prayerNameTV.text = requireActivity().getString(R.string.asr_time)
            binding.prayerTimeTV.text = asorTimeString
            binding.prayerBgIV.setImageResource(R.drawable.asor)
            binding.asorCV.strokeColor =
                ContextCompat.getColor(requireContext(), R.color.light_green)
            binding.asorCV.setCardBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.dim_green
                )
            )
            backgroundScreenGradient(R.drawable.asor)
        } else if (System.currentTimeMillis() in ((asorTimeInMilliseconds + 3600000) + 1)..(magribTimeInMilliseconds + (20 * 60000))) {
            binding.prayerNameTV.text = requireActivity().getString(R.string.maghrib_time)
            binding.prayerTimeTV.text = magribTimeString
            binding.prayerBgIV.setImageResource(R.drawable.magrib)
            binding.magribCV.strokeColor =
                ContextCompat.getColor(requireContext(), R.color.light_green)
            binding.magribCV.setCardBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.dim_green
                )
            )
            backgroundScreenGradient(R.drawable.magrib)
        } else if (System.currentTimeMillis() in ((magribTimeInMilliseconds + (20 * 60000)) + 1)..(ishaTimeInMilliseconds + (5 * 3600000))) {
            binding.prayerNameTV.text = requireActivity().getString(R.string.isha_time)
            binding.prayerTimeTV.text = ishaTimeString
            binding.prayerBgIV.setImageResource(R.drawable.isha)
            binding.eshaCV.strokeColor =
                ContextCompat.getColor(requireContext(), R.color.light_green)
            binding.eshaCV.setCardBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.dim_green
                )
            )
            backgroundScreenGradient(R.drawable.isha)
        }

        binding.fazorTimeTV.text = fajrTimeString
        binding.juhorTimeTV.text = juhorTimeString
        binding.asorTimeTV.text = asorTimeString
        binding.magribTimeTV.text = magribTimeString
        binding.eshaTimeTV.text = ishaTimeString
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

    private fun dailyOneTimeRunWorkerTrigger() {
        if (notificationManager.isNotificationPolicyAccessGranted) {
            Log.d("Prayer_tag", "if call")
            val today = SimpleDate(GregorianCalendar())
            val location =
                com.azan.astrologicalCalc.Location(prefs.currentLat, prefs.currentLon, +6.0, 0)
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

//            if (!isWorkerAlreadyRunning()) {
//            }
            workManager.enqueueUniquePeriodicWork(WORKER_NAME, ExistingPeriodicWorkPolicy.UPDATE, periodicWorkRequest)

            //prefs.workerId = periodicWorkRequest.id



            workManager.getWorkInfoByIdLiveData(periodicWorkRequest.id).observe(viewLifecycleOwner) {
                when(it.state){
                    WorkInfo.State.ENQUEUED -> {
                        Log.d("PRAYERS_WORKER", "ENQUEUED : ${it.progress}")
                    }
                    WorkInfo.State.RUNNING -> {
                        Log.d("PRAYERS_WORKER", "RUNNING : ${it.progress.toByteArray()}")
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
        }else{
            Log.d("Prayer_tag", "else call")
        }
    }

    private fun isWorkerAlreadyRunning(): Boolean {
        val workInfo = workManager.getWorkInfosByTag(WORKER_TAG).get()
        workInfo.forEach { work ->
            if(work.state == WorkInfo.State.ENQUEUED || work.state == WorkInfo.State.RUNNING)
                return true
        }
        return false
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

    companion object {
        fun newInstance() = HomeFragment().apply {
            arguments = Bundle().apply {

            }
        }
    }
}