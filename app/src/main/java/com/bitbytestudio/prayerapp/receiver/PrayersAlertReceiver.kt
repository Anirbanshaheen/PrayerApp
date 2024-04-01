package com.bitbytestudio.prayerapp.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.bitbytestudio.prayerapp.prefs.Prefs
import com.bitbytestudio.prayerapp.ui.DndHandler
import com.bitbytestudio.prayerapp.worker.DndDisableWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PrayersAlertReceiver : BroadcastReceiver() {

    private val dndHandler by lazy { DndHandler() }

    @Inject
    lateinit var prefs: Prefs

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("CHECK_TIME", "Prayers call")
        Log.d("TAKE_TIME", "Broadcast Receiver call")
        dNdMode(context, intent)
    }

    private fun dNdMode(context: Context?, intent: Intent?) {
        Log.d("CHECK_TIME", "user NAME : ${intent?.getStringExtra("NAME")}")
        Log.d("CHECK_TIME", "user ID : ${intent?.getIntExtra("ID", 0)}")
        Log.d("CHECK_TIME", "user delay time : ${intent?.getIntExtra("DELAY_TIME", 0)}")
        context?.let {
            if (dndHandler.isDndEnabled(context)) {
                dndHandler.enableDndMode(context)
                dndHandler.sendNotification(context, intent)
            } else {
                Toast.makeText(context, "DND mode already active!!", Toast.LENGTH_LONG).show()
            }

            val delay = intent?.getIntExtra("DELAY_TIME",0)!!.toLong()
            val workRequest = OneTimeWorkRequest.Builder(DndDisableWorker::class.java)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(
                    Data.Builder()
                        .apply {
                            putString("NAME", intent.getStringExtra("NAME"))
                            putInt("ID", intent.getIntExtra("ID", 0))
                            putLong("DELAY_TIME", delay)
                        }
                        .build()
                )
                .build()
            WorkManager.getInstance(context).enqueue(workRequest)


//            Handler(Looper.getMainLooper()).postDelayed({
//                if (dndHandler.isDndEnabled(it)) {
//                    dndHandler.disableDndMode(context)
//                    sendNotification(context, intent)
//                    Log.d("CHECK_TIME", "Back to normal mode")
//                }else{
//                    Log.d("CHECK_TIME", "normal mode")
//                    Toast.makeText(context, "DND mode already deactivate!!", Toast.LENGTH_LONG).show()
//                }
//            }, intent?.getIntExtra("DELAY_TIME", (60000 * 15))!!.toLong())
        }
    }


    companion object {
        const val NOTIFICATION_ID = "appName_notification_id"
        const val NOTIFICATION_NAME = "appName"
        const val NOTIFICATION_CHANNEL = "appName_channel_01"
    }
}

