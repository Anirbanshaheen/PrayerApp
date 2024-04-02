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
        Log.d("CHECK_TIME", "user delay time : ${intent?.getLongExtra("DELAY_TIME", 0)}")
        context?.let {
            dndHandler.enableDndMode(context)
            intent?.let {
                it.putExtra("IS_ENABLE", true)
                dndHandler.sendNotification(context, it)
            }

            val delay = intent?.getLongExtra("DELAY_TIME",0L) ?: 0L
            val workRequest = OneTimeWorkRequest.Builder(DndDisableWorker::class.java)
                .setInputData(
                    Data.Builder()
                        .apply {
                            putString("NAME", intent?.getStringExtra("NAME"))
                            putInt("ID", intent?.getIntExtra("ID", 0) ?: 0)
                            putLong("DELAY_TIME", delay)
                        }
                        .build()
                )
                .build()
            WorkManager.getInstance(context).enqueue(workRequest)
        }
    }


    companion object {
        const val NOTIFICATION_ID = "appName_notification_id"
        const val NOTIFICATION_NAME = "appName"
        const val NOTIFICATION_CHANNEL = "appName_channel_01"
    }
}

