package com.example.prayerapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.prayerapp.R
import com.example.prayerapp.databinding.ActivityMainBinding
import com.example.prayerapp.prefs.Prefs
import com.example.prayerapp.utils.changeStatusBarColor
import com.example.prayerapp.utils.updateLocale
import com.example.prayerapp.viewmodel.PrayerViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val prayersViewModel by viewModels<PrayerViewModel>()
    var myLocationManager: MyLocationManager? = null
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

        updateLocale(Locale(prefs.appLanguage))
        if (prefs.statusBarColor != 0) changeStatusBarColor(prefs.statusBarColor)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainActivity = this

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        val navController = findNavController(R.id.fragmentContainer)

        bottomNavigationView.setupWithNavController(navController)

        if (myLocationManager == null) {
            myLocationManager = MyLocationManager(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        myLocationManager?.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        myLocationManager?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}