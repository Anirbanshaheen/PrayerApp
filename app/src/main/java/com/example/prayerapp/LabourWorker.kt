package com.example.prayerapp

import android.content.Context
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.batoulapps.adhan.CalculationMethod
import com.batoulapps.adhan.Coordinates
import com.batoulapps.adhan.Madhab
import com.batoulapps.adhan.PrayerTimes
import com.batoulapps.adhan.data.DateComponents
import java.text.SimpleDateFormat
import java.util.*

class LabourWorker(private val context: Context, private val workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    var tempTime2 = "03:16 PM"
    var tempTime1 = "03:14 PM"

    override fun doWork(): Result {
        mainOperation(context)
        return Result.success()
    }

    private fun mainOperation(context: Context) {
        val coordinates = Coordinates(23.8103, 90.4125)
        val dateComponents = DateComponents.from(Date())
        val parameters = CalculationMethod.KARACHI.parameters
        parameters.madhab = Madhab.HANAFI

        val formatter = SimpleDateFormat("hh:mm a", Locale.US)

        val localCurrentDateTime = formatter.format(Calendar.getInstance().time)
        var prayerTimes = PrayerTimes(coordinates, dateComponents, parameters)
//        var temp = prayerTimes.dhuhr

        if (tempTime1 == localCurrentDateTime) {
            internalTimeCheck()
//            extendTime()
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
            /*var notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationManager.isNotificationPolicyAccessGranted) {
                context.applicationContext.startActivity(
                    Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS).setFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK
                    )
                )
            }*/
            Log.d("WorkManager", "YES $localCurrentDateTime")
        } else {
            Log.d("WorkManager", "NO")
        }
    }

    private fun internalTimeCheck() {
        Handler(Looper.getMainLooper()).postDelayed({
//            var startTime = System.currentTimeMillis().toString()

            val formatter = SimpleDateFormat("hh:mm a", Locale.US)
            val localCurrentDateTime = formatter.format(Calendar.getInstance().time)

            if(localCurrentDateTime == tempTime2){
                val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
                Log.d("WorkManager", "YESSSSS")
            }
        }, 900000)
    }

    private fun extendTime() {
        var currentTime = Calendar.getInstance()
        currentTime.add(Calendar.MINUTE, 18)
        currentTime.time

        Log.d("WorkManager", (currentTime.time).toString())
    }

}