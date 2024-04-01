package com.bitbytestudio.prayerapp.worker

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bitbytestudio.prayerapp.prefs.Prefs
import com.bitbytestudio.prayerapp.ui.DndHandler
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import java.io.IOException
import javax.inject.Inject

@HiltWorker
class DndDisableWorker @AssistedInject constructor (@Assisted private val context: Context, @Assisted private val params: WorkerParameters) :
    CoroutineWorker(context, params) {

    @Inject
    lateinit var prefs: Prefs
    private val dndHandler by lazy { DndHandler() }
    override suspend fun doWork(): Result {
        return try {
            Log.d("DND_DISABLE_TAG", "doWork() -> DndDisableWorker call")
            val delay = inputData.getLong("DELAY_TIME", 0)
            val intent = Intent()
            intent.putExtra("NAME", inputData.getString("NAME"))
            intent.putExtra("ID", inputData.getInt("ID", 0))
            intent.putExtra("DELAY_TIME", delay)

            Log.d("DND_DISABLE_TAG", "user NAME : ${inputData.getString("NAME")}")
            Log.d("DND_DISABLE_TAG", "user ID : ${inputData.getInt("ID", 0)}")
            Log.d("DND_DISABLE_TAG", "user delay time : $delay")

            Log.d("DND_DISABLE_TAG", "before delay : ${dndHandler.isDndEnabled(applicationContext)}")
            delay(delay)
            Log.d("DND_DISABLE_TAG", "after delay : ${dndHandler.isDndEnabled(applicationContext)}")
            if (dndHandler.isDndEnabled(context)) {
                dndHandler.disableDndMode(context)
                dndHandler.sendNotification(context, intent)
                Log.d("DND_DISABLE_TAG", "Back to normal mode")
            } else {
                Log.d("DND_DISABLE_TAG", "normal mode")
                Toast.makeText(context, "DND mode already deactivate!!", Toast.LENGTH_LONG).show()
            }

            Result.success()
        } catch (e: IOException) {
            Log.d("DND_DISABLE_TAG", "exception -> ${e.localizedMessage}")
            Result.retry()
        }
    }
}