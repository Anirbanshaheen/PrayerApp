package com.bitbytestudio.autosilentprayerapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bitbytestudio.autosilentprayerapp.R
import com.bitbytestudio.autosilentprayerapp.databinding.ActivityMainBinding
import com.bitbytestudio.autosilentprayerapp.prefs.Prefs
import com.bitbytestudio.autosilentprayerapp.utils.changeStatusBarColor
import com.bitbytestudio.autosilentprayerapp.utils.updateLocale
import com.bitbytestudio.autosilentprayerapp.viewmodel.PrayerViewModel
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

        mainActivity = this

        if (myLocationManager == null) {
            myLocationManager = MyLocationManager(this)
        }

        updateLocale(Locale(prefs.appLanguage))
        if (prefs.statusBarColor != 0) changeStatusBarColor(prefs.statusBarColor)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        val navController = findNavController(R.id.fragmentContainer)

        bottomNavigationView.setupWithNavController(navController)
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