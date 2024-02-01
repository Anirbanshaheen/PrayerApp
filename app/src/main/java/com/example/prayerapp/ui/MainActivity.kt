package com.example.prayerapp.ui

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NavUtils
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.prayerapp.R
import com.example.prayerapp.databinding.ActivityMainBinding
import com.example.prayerapp.model.FragmentState
import com.example.prayerapp.prefs.Prefs
import com.example.prayerapp.utils.changeFragment
import com.example.prayerapp.viewmodel.PrayerViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
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

    companion object {
        lateinit var mainActivity: MainActivity
    }


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

        mainActivity = this

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        val navController = findNavController(R.id.fragmentContainer)

        bottomNavigationView.setupWithNavController(navController)

        //app bar config
//        val appBarConfiguration = AppBarConfiguration(setOf(R.id.homeFragment, R.id.countFragment, R.id.compassFragment))
//        setupActionBarWithNavController(navController, appBarConfiguration)

        //initialize()
        //observer()
        //clickListeners()

//        dailyOneTimeRunWorkerTrigger()
//        clickListeners()

//        val alarmManager0 = applicationContext.getSystemService(ALARM_SERVICE) as AlarmManager
//        Log.d("CHECK_TIME", "Prayers Worker call")
//        val intent = Intent(applicationContext, PrayersAlertReceiver::class.java)
//        intent.putExtra("title", "Hello");
//        intent.putExtra("description", "world")
//
//        val pendingIntent = PendingIntent.getBroadcast(applicationContext, 1111, intent, PendingIntent.FLAG_MUTABLE)
//        alarmManager0.setExact(AlarmManager.RTC_WAKEUP,  System.currentTimeMillis() + (15*1000), pendingIntent);
    }

    private fun initialize() {
        //changeFragment(R.id.fragmentContainer, HomeFragment(), false)

//        if (!checkPermissions()) {
//            requestPermissions()
//            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//            startActivity(intent)
//        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            42
        )
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager =
            this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun observer() {
//        lifecycleScope.launch {
//            prayersViewModel.fragmentSwitchState.collectLatest {
//                when(it) {
//                    FragmentState.homeFragment.value -> {
//                        binding.bottomNavigation.menu.findItem(R.id.home_menu).isChecked = true
//                        changeFragment(R.id.fragmentContainer, HomeFragment(), false)
//                    }
//
//                    FragmentState.countFragment.value -> {
//                        binding.bottomNavigation.menu.findItem(R.id.count_menu).isChecked = true
//                        changeFragment(R.id.fragmentContainer, CountFragment(), false)
//                    }
//
//                    FragmentState.compassFragment.value -> {
//                        binding.bottomNavigation.menu.findItem(R.id.compass_menu).isChecked = true
//                        changeFragment(R.id.fragmentContainer, CompassFragment(), false)
//                    }
//                }
//            }
//        }
    }

    private fun clickListeners() {
//        binding.bottomNavigation.setOnItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.home_menu -> {
//                    prayersViewModel.setFragmentSwitchState(1)
//                    true
//                }
//
//                R.id.count_menu -> {
//                    prayersViewModel.setFragmentSwitchState(2)
//                    true
//                }
//
//                R.id.compass_menu -> {
//                    prayersViewModel.setFragmentSwitchState(3)
//                    true
//                }
//
//                else -> false
//            }
//        }
    }


//    private fun clickListeners() {
//
//    }
//
//    private fun initialize() {
//        setSilentModePolicy()
//        checkPermission()
//        showTime()
//    }

//    private fun dailyOneTimeRunWorkerTrigger() {
//        workManager = WorkManager.getInstance(applicationContext)
//
//        val constraints = Constraints.Builder()
//            .setRequiredNetworkType(NetworkType.CONNECTED)
//            .build()
//
//        val data = Data.EMPTY
//
//        periodicWorkRequest = PeriodicWorkRequestBuilder<PrayersWorker>(1, TimeUnit.DAYS) //, 15, TimeUnit.MINUTES
//            .setInputData(data)
//            .setConstraints(constraints)
//            .addTag(WORKER_TAG)
//            .setBackoffCriteria(BackoffPolicy.LINEAR, PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS, TimeUnit.MILLISECONDS)
//            .build()
//
//
//        workManager.enqueueUniquePeriodicWork("Prayers Worker", ExistingPeriodicWorkPolicy.UPDATE, periodicWorkRequest)
//
//        workManager.getWorkInfoByIdLiveData(periodicWorkRequest.id).observe(this){
//            when(it.state){
//                WorkInfo.State.ENQUEUED -> {
//                    Log.d("PRAYERS_WORKER", "ENQUEUED : ${it.progress}")
//                }
//                WorkInfo.State.RUNNING -> {
//                    Log.d("PRAYERS_WORKER", "RUNNING : ${it.progress}")
//                }
//                WorkInfo.State.SUCCEEDED -> {
//                    Log.d("PRAYERS_WORKER", "SUCCEEDED : ${it.progress}")
//                }
//                WorkInfo.State.CANCELLED -> {
//                    Log.d("PRAYERS_WORKER", "CANCELLED : ${it.progress}")
//                }
//                WorkInfo.State.BLOCKED -> {
//                    Log.d("PRAYERS_WORKER", "BLOCKED : ${it.progress}")
//                }
//                else ->{
//                    Log.d("PRAYERS_WORKER", "else : ${it.progress}")
//                }
//            }
//        }
//    }
//
//    private fun setSilentModePolicy() {
//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        if (!notificationManager.isNotificationPolicyAccessGranted) {
//            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
//            startActivity(intent)
//        }
//    }
//
//    private fun showTime() {
//        val today = SimpleDate(GregorianCalendar())
//        val location = Location(23.8103, 90.4125, +6.0, 0)
//        val azan = Azan(location, Method.KARACHI_HANAF)
//        val prayerTimes = azan.getPrayerTimes(today)
//
//        binding.fazorTimeTV.text = prayerTimes.fajr().twentyFourTo12HourConverter()
//        binding.juhorTimeTV.text = prayerTimes.thuhr().twentyFourTo12HourConverter()
//        binding.asorTimeTV.text = prayerTimes.assr().twentyFourTo12HourConverter()
//        binding.magribTimeTV.text = prayerTimes.maghrib().twentyFourTo12HourConverter()
//        binding.eshaTimeTV.text = prayerTimes.ishaa().twentyFourTo12HourConverter()
//    }
//
//    private fun checkPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (ContextCompat.checkSelfPermission(
//                    this,
//                    Manifest.permission.POST_NOTIFICATIONS
//                ) == PackageManager.PERMISSION_GRANTED
//            ) {
//                isPermission = true
//            } else {
//                isPermission = false
//                checkNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
//            }
//        } else {
//            isPermission = true
//        }
//    }
}