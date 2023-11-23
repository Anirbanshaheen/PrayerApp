package com.example.prayerapp.ui

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
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
import com.example.prayerapp.utils.twentyFourTo12HourConverter
import com.example.prayerapp.viewmodel.PrayerViewModel
import com.example.prayerapp.worker.PrayersWorker
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import dagger.hilt.android.AndroidEntryPoint
import java.util.GregorianCalendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding
    private val prayersViewModel by activityViewModels<PrayerViewModel>()
    private lateinit var alarmManager: AlarmManager
    private lateinit var workManager: WorkManager
    private lateinit var periodicWorkRequest: PeriodicWorkRequest
    private val WORKER_TAG = "PRAYERS_WORKER"

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
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialize()
        dailyOneTimeRunWorkerTrigger()
        clickListeners()
    }

    private fun clickListeners() {

    }

    private fun initialize() {
        setSilentModePolicy()
        checkPermission()
        enableLocation()
        showTime()
    }

    private fun enableLocation() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 0).apply {
            setMinUpdateDistanceMeters(1f)
            setWaitForAccurateLocation(true)
        }.build()

//        val locationRequest = LocationRequest.create()
//            .setInterval(0)
//            .setFastestInterval(0)
//            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        LocationServices.getSettingsClient(requireActivity())
            .checkLocationSettings(builder.build())
            .addOnSuccessListener { response ->

                // Location settings are satisfied, start updating location
                // startUpdatingLocation(...)
            }
            .addOnFailureListener { ex ->
                if (ex is ResolvableApiException) {
                    // Location settings are NOT satisfied, but this can be fixed by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(), and check the result in onActivityResult().
                        val resolvable = ex as ResolvableApiException
                        resolvable.startResolutionForResult(requireActivity(), 11)
                    } catch (sendEx: IntentSender.SendIntentException) {
                        // Ignore the error.
                    }
                }
            }
    }

//    override fun onActivityResult(
//        requestCode: Int,
//        resultCode: Int,
//        data: Intent?
//    ) {
//        if (11 === requestCode) {
//            if (Activity.RESULT_OK == resultCode) {
//                //user clicked OK, you can startUpdatingLocation(...);
//            } else {
//                //user clicked cancel: informUserImportanceOfLocationAndPresentRequestAgain();
//            }
//        }
//    }

    private fun setSilentModePolicy() {
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

        binding.fazorTimeTV.text = prayerTimes.fajr().twentyFourTo12HourConverter()
        binding.juhorTimeTV.text = prayerTimes.thuhr().twentyFourTo12HourConverter()
        binding.asorTimeTV.text = prayerTimes.assr().twentyFourTo12HourConverter()
        binding.magribTimeTV.text = prayerTimes.maghrib().twentyFourTo12HourConverter()
        binding.eshaTimeTV.text = prayerTimes.ishaa().twentyFourTo12HourConverter()
    }

    private fun dailyOneTimeRunWorkerTrigger() {
        workManager = WorkManager.getInstance(requireContext())

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val data = Data.EMPTY

        periodicWorkRequest = PeriodicWorkRequestBuilder<PrayersWorker>(1, TimeUnit.DAYS) //, 15, TimeUnit.MINUTES
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
}