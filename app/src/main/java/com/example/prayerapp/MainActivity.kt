package com.example.prayerapp

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
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
import com.example.prayerapp.databinding.ActivityMainBinding
import com.example.prayerapp.prefs.Prefs
import com.example.prayerapp.utils.twentyFourTo12HourConverter
import com.example.prayerapp.viewmodel.PrayerViewModel
import com.example.prayerapp.worker.PrayersWorker
import dagger.hilt.android.AndroidEntryPoint
import java.util.GregorianCalendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val prayersViewModel by viewModels<PrayerViewModel>()
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initialize()
        dailyOneTimeRunWorkerTrigger()
        clickListeners()

//        val alarmManager0 = applicationContext.getSystemService(ALARM_SERVICE) as AlarmManager
//        Log.d("CHECK_TIME", "Prayers Worker call")
//        val intent = Intent(applicationContext, PrayersAlertReceiver::class.java)
//        intent.putExtra("title", "Hello");
//        intent.putExtra("description", "world")
//
//        val pendingIntent = PendingIntent.getBroadcast(applicationContext, 1111, intent, PendingIntent.FLAG_MUTABLE)
//        alarmManager0.setExact(AlarmManager.RTC_WAKEUP,  System.currentTimeMillis() + (15*1000), pendingIntent);
    }

    private fun clickListeners() {

    }

    private fun initialize() {
        setSilentModePolicy()
        checkPermission()
        showTime()
    }

    private fun dailyOneTimeRunWorkerTrigger() {
        workManager = WorkManager.getInstance(applicationContext)

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

        workManager.getWorkInfoByIdLiveData(periodicWorkRequest.id).observe(this){
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

    private fun setSilentModePolicy() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (!notificationManager.isNotificationPolicyAccessGranted) {
            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            startActivity(intent)
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

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                isPermission = true
            } else {
                isPermission = false
                checkNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            isPermission = true
        }
    }
}