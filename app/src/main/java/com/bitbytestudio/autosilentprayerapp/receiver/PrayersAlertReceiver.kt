package com.bitbytestudio.autosilentprayerapp.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.bitbytestudio.autosilentprayerapp.prefs.Prefs
import com.bitbytestudio.autosilentprayerapp.ui.DndHandler
import com.bitbytestudio.autosilentprayerapp.worker.DndDisableWorker
import com.bitbytestudio.autosilentprayerapp.worker.PrayersWorker.Companion.ALERT_DELAY_TIME
import com.bitbytestudio.autosilentprayerapp.worker.PrayersWorker.Companion.ALERT_ID
import com.bitbytestudio.autosilentprayerapp.worker.PrayersWorker.Companion.ALERT_NAME
import com.bitbytestudio.autosilentprayerapp.worker.PrayersWorker.Companion.IS_ENABLE
import javax.inject.Inject

class PrayersAlertReceiver : BroadcastReceiver() {

    private val dndHandler by lazy { DndHandler() }

    @Inject
    lateinit var prefs: Prefs

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        Log.d("CHECK_TIME", "Prayers call")
        Log.d("TAKE_TIME", "Broadcast Receiver call")
        dNdMode(context, intent)
    }

    private fun dNdMode(context: Context?, intent: Intent?) {
        Log.d("CHECK_TIME", "user NAME : ${intent?.getStringExtra(ALERT_NAME)}")
        Log.d("CHECK_TIME", "user ID : ${intent?.getIntExtra(ALERT_ID, 0)}")
        Log.d("CHECK_TIME", "user delay time : ${intent?.getLongExtra(ALERT_DELAY_TIME, 0L)}")
        context?.let {
            dndHandler.enableDndMode(context)
            intent?.let {
                it.putExtra(IS_ENABLE, true)
                dndHandler.sendNotification(context, it)
            }

            val delay = intent?.getLongExtra(ALERT_DELAY_TIME, 0L) ?: 0L
            val workRequest = OneTimeWorkRequest.Builder(DndDisableWorker::class.java).setInputData(
                workDataOf(
                    ALERT_NAME to intent?.getStringExtra(ALERT_NAME),
                    ALERT_ID to intent?.getIntExtra(ALERT_ID, 0),
                    ALERT_DELAY_TIME to delay
                )
            ).build()
            WorkManager.getInstance(context).enqueue(workRequest)
        }
    }


    companion object {
        const val NOTIFICATION_ID = "appName_notification_id"
        const val NOTIFICATION_NAME = "appName"
        const val NOTIFICATION_CHANNEL = "appName_channel_01"
    }
}