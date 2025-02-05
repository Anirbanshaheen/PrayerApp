package com.bitbytestudio.autosilentprayerapp.ui

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
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
import com.bitbytestudio.autosilentprayerapp.R
import com.bitbytestudio.autosilentprayerapp.databinding.ActivityMainBinding
import com.bitbytestudio.autosilentprayerapp.prefs.DataStorePreference
import com.bitbytestudio.autosilentprayerapp.prefs.DataStorePreference.Companion.IS_DND_ENABLED
import com.bitbytestudio.autosilentprayerapp.prefs.DataStorePreference.Companion.LOCATION
import com.bitbytestudio.autosilentprayerapp.prefs.Prefs
import com.bitbytestudio.autosilentprayerapp.receiver.PrayersAlertReceiver
import com.bitbytestudio.autosilentprayerapp.ui.HomeFragment.Companion
import com.bitbytestudio.autosilentprayerapp.ui.HomeFragment.Companion.WORKER_NAME
import com.bitbytestudio.autosilentprayerapp.utils.changeStatusBarColor
import com.bitbytestudio.autosilentprayerapp.utils.twentyFourTo12HourConverter
import com.bitbytestudio.autosilentprayerapp.utils.updateLocale
import com.bitbytestudio.autosilentprayerapp.viewmodel.PrayerViewModel
import com.bitbytestudio.autosilentprayerapp.worker.PrayersWorker
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val prayersViewModel by viewModels<PrayerViewModel>()
    private lateinit var binding: ActivityMainBinding
    var myLocationManager: MyLocationManager? = null
    private val WORKER_TAG = "PRAYERS_WORKER"

    @Inject
    lateinit var prefs: Prefs
    @Inject
    lateinit var dataStorePreference: DataStorePreference

    private lateinit var workManager: WorkManager
    private lateinit var periodicWorkRequest: PeriodicWorkRequest
    private lateinit var notificationManager: NotificationManager

    private var checkExactAlarmPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        isPermission = isGranted
    }

    private var isPermission = false
    private var checkNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        isPermission = isGranted
        if (!notificationManager.isNotificationPolicyAccessGranted) {
            showMaterialAlertDialog()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.SCHEDULE_EXACT_ALARM
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                isPermission = true
            } else {
                isPermission = false
                checkExactAlarmPermission.launch(Manifest.permission.SCHEDULE_EXACT_ALARM)
            }
        }
    }

    private val requestNotificationPolicyAccess = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            myLocationManager?.initialize()
            Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show()

        } else {
            myLocationManager?.initialize()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        updateLocale(Locale(prefs.appLanguage))
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (prefs.statusBarColor != 0) changeStatusBarColor(prefs.statusBarColor)
        val navController = findNavController(R.id.fragmentContainer)
        binding.bottomNavigation.setupWithNavController(navController)

        initialize()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        myLocationManager?.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        myLocationManager?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun initialize() {
        prayersViewModel.getPrayersTime()
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        locationOn()
        checkPermission()

        lifecycleScope.launch {
            dataStorePreference.getPreference(IS_DND_ENABLED, true).collectLatest {
                if (it) {
                    enableWorker()
                } else {
                    cancelWorkerByTag()
                }
            }
        }
    }

    private fun locationOn() {
        if (myLocationManager == null) {
            myLocationManager = MyLocationManager(this)
        }
        if (notificationManager.isNotificationPolicyAccessGranted) {
            myLocationManager?.initialize()
        }

        myLocationManager?.locationCallback = object : (Location?) -> Unit {
            override fun invoke(location: Location?) {
                prefs.currentLat = location?.latitude ?: 23.8103
                prefs.currentLon = location?.longitude ?: 90.4125
                prayersViewModel.getPrayersTime()
                Log.d(
                    "locationCallback",
                    "\nlatitude: ${prefs.currentLat}\nlongitude: ${prefs.currentLon}"
                )
            }
        }
    }


    private fun showMaterialAlertDialog() {
        val builder = MaterialAlertDialogBuilder(this)

        builder.setTitle("Require Permission")
        builder.setMessage("Please grant permission for DND mode")
        builder.setCancelable(false)

        builder.setPositiveButton("OK") { dialog, which ->
            setDNDModePolicy()
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                isPermission = true
            } else {
                isPermission = false
                checkNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            if (!notificationManager.isNotificationPolicyAccessGranted) {
                showMaterialAlertDialog()
            }
            isPermission = true
        }
    }

    private fun setDNDModePolicy() {
        if (!notificationManager.isNotificationPolicyAccessGranted) {
            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            requestNotificationPolicyAccess.launch(intent)
        }
    }

    private fun enableWorker() {
        if (notificationManager.isNotificationPolicyAccessGranted) {
            Log.d("Prayer_tag", "Notification Granted")

            workManager = WorkManager.getInstance(this)

            val constraints =
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

            val data = Data.EMPTY

            periodicWorkRequest =
                PeriodicWorkRequestBuilder<PrayersWorker>(12, TimeUnit.HOURS).setInputData(data)
                    .setConstraints(constraints).addTag(HomeFragment.WORKER_TAG).setBackoffCriteria(
                        BackoffPolicy.LINEAR,
                        PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS,
                        TimeUnit.MILLISECONDS
                    ).build()

            workManager.enqueueUniquePeriodicWork(
                WORKER_NAME, ExistingPeriodicWorkPolicy.UPDATE, periodicWorkRequest
            )


            workManager.getWorkInfoByIdLiveData(periodicWorkRequest.id).observe(this) {
                when (it.state) {
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

                    else -> {
                        Log.d("PRAYERS_WORKER", "else : ${it.progress}")
                    }
                }
            }
        } else {
            Log.d("Prayer_tag", "Notification Policy Access Not Granted")
        }
    }

    private fun cancelWorkerByTag(workerTag: String = HomeFragment.WORKER_TAG) {
        val workManager = WorkManager.getInstance(this)
        workManager.cancelAllWorkByTag(workerTag)
        cancelAlarm()
    }

    private fun cancelAlarm() {
        for (i in 1..5){
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(this, PrayersAlertReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                this, i, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        }
    }

    private fun isWorkerRunning(workerTag: String = HomeFragment.WORKER_TAG): Boolean {
        val workManager = WorkManager.getInstance(this)
        val workInfos = workManager.getWorkInfosByTag(workerTag).get()
        return workInfos.any {
            it.state == WorkInfo.State.RUNNING || it.state == WorkInfo.State.ENQUEUED
        }
    }
}