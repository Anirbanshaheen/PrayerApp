package com.example.prayerapp

import android.Manifest.permission.ACCESS_NOTIFICATION_POLICY
import android.Manifest.permission.MODIFY_AUDIO_SETTINGS
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.audiofx.BassBoost
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val bt: Button = findViewById(R.id.button)
        val btP: Button = findViewById(R.id.button2)
        checking()
//        workingMachine()
        /*val request = PeriodicWorkRequestBuilder<LabourWorker>(15, TimeUnit.MINUTES).build()

        *//*val request = OneTimeWorkRequestBuilder<LabourWorker>()
            .build()*//*
        val bt: Button = findViewById(R.id.button)
        val requestUUID = request.id
        val workManager = WorkManager.getInstance(this)
        workManager.enqueueUniquePeriodicWork("Getdata", ExistingPeriodicWorkPolicy.KEEP, request)*/

//        workManager.beginWith("do",ExistingPeriodicWorkPolicy.REPLACE, request)
//        workManager.beginUniqueWork("do", ExistingWorkPolicy.REPLACE, request).enqueue()
        /*workManager.getWorkInfoByIdLiveData(requestUUID).observe(this, Observer {workInfo->
            if (workInfo != null){
                val result = workInfo.outputData.getString("work_result")
                if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                    Toast.makeText(this, "Successfully success $result", Toast.LENGTH_SHORT).show()
                } else if (workInfo.state == WorkInfo.State.FAILED) {
                    Toast.makeText(this, "Failed $result", Toast.LENGTH_SHORT).show()
                }

            }
        })*/

        /*val coordinates = Coordinates(23.8103, 90.4125)
        val dateComponents = DateComponents.from(Date())
        val parameters = CalculationMethod.KARACHI.parameters
        parameters.madhab = Madhab.HANAFI

        val formatter = SimpleDateFormat("hh:mm a")

        val prayerTimes = PrayerTimes(coordinates, dateComponents, parameters)

        val fazor: TextView = findViewById(R.id.fazor)
        val juhor: TextView = findViewById(R.id.juhor)
        val asor: TextView = findViewById(R.id.asor)
        val magrib: TextView = findViewById(R.id.magrib)
        val esha: TextView = findViewById(R.id.esha)
        val sunrise: TextView = findViewById(R.id.sunrise)*/

        btP.setOnClickListener {
            checkPermission(MODIFY_AUDIO_SETTINGS, 1)
            checkPermission(ACCESS_NOTIFICATION_POLICY, 2)
        }

        bt.setOnClickListener {
            workingMachine()
        }
    }

    private fun checking() {
        var notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationManager.isNotificationPolicyAccessGranted) {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            return
        }
    }

    private fun workingMachine() {
        val request = PeriodicWorkRequestBuilder<LabourWorker>(15, TimeUnit.MINUTES).build()
//        val request = OneTimeWorkRequestBuilder<LabourWorker>().build()
        val requestUUID = request.id
        val workManager = WorkManager.getInstance(this)
        // In this case we use enqueueUniquePeriodicWork for destroy previous background task
//        workManager.enqueueUniquePeriodicWork("Getdata", ExistingPeriodicWorkPolicy.REPLACE, request)
        workManager.enqueue(request)
        workManager.getWorkInfoByIdLiveData(requestUUID).observe(this, Observer { workInfo ->
            if (workInfo != null) {
                val result = workInfo.outputData.getString("work_result")
                if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                    Log.d("WorkManager onSuccess", "$result")
                } else if (workInfo.state == WorkInfo.State.FAILED) {
                    Log.d("WorkManager onFailed", "$result")
                }
            }
        })
    }

    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                permission
            ) == PackageManager.PERMISSION_DENIED
        ) {

            // Requesting the permission
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission), requestCode)
        } else {
            Toast.makeText(this@MainActivity, "Permission already granted", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@MainActivity, "Camera Permission Granted", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(this@MainActivity, "Camera Permission Denied", Toast.LENGTH_SHORT)
                    .show()
            }
        } else if (requestCode == 2) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@MainActivity, "Storage Permission Granted", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(this@MainActivity, "Storage Permission Denied", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}