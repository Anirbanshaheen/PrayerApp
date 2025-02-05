package com.bitbytestudio.autosilentprayerapp.worker

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bitbytestudio.autosilentprayerapp.prefs.Prefs
import com.bitbytestudio.autosilentprayerapp.ui.DndHandler
import com.bitbytestudio.autosilentprayerapp.worker.PrayersWorker.Companion.ALERT_DELAY_TIME
import com.bitbytestudio.autosilentprayerapp.worker.PrayersWorker.Companion.ALERT_ID
import com.bitbytestudio.autosilentprayerapp.worker.PrayersWorker.Companion.ALERT_NAME
import com.bitbytestudio.autosilentprayerapp.worker.PrayersWorker.Companion.IS_ENABLE
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import java.io.IOException
import javax.inject.Inject

@HiltWorker
class DndDisableWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters
) : CoroutineWorker(context, params) {

    @Inject
    lateinit var prefs: Prefs
    private val dndHandler by lazy { DndHandler() }

    override suspend fun doWork(): Result {
        return try {
            Log.d("DND_DISABLE_TAG", "doWork() -> DndDisableWorker call")

            // Fetch the delay time from the input data
            val delay = inputData.getLong(ALERT_DELAY_TIME, 0L)
            val name = inputData.getString(ALERT_NAME)
            val id = inputData.getInt(ALERT_ID, 0)
            val intent = Intent().apply {
                putExtra(ALERT_NAME, name)
                putExtra(ALERT_ID, id)
                putExtra(ALERT_DELAY_TIME, delay)
                putExtra(IS_ENABLE, false)
            }

            // Logging user details for debugging
            Log.d("DND_DISABLE_TAG", "user NAME : ${name}")
            Log.d("DND_DISABLE_TAG", "user ID : ${id}")
            Log.d("DND_DISABLE_TAG", "user delay time : $delay")

            // Log before the delay and pause the execution for the given delay time
            Log.d("DND_DISABLE_TAG", "before delay")
            delay(delay) // Ensure you use the correct delay function (from kotlinx.coroutines)

            // Disable DND mode and send the notification
            dndHandler.disableDndMode(applicationContext)
            dndHandler.sendNotification(context, intent)

            // Log after disabling DND
            Log.d("DND_DISABLE_TAG", "after delay")
            Log.d("DND_DISABLE_TAG", "Back to normal mode")

            // Return success after the task is completed
            Result.success()

        } catch (e: IOException) {
            Log.d("DND_DISABLE_TAG", "exception -> ${e.localizedMessage}")
            // Retry the worker in case of failure
            return Result.retry()
        }
    }
}
