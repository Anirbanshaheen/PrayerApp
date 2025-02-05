package com.bitbytestudio.autosilentprayerapp.worker

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.azan.Azan
import com.azan.Method
import com.azan.astrologicalCalc.Location
import com.azan.astrologicalCalc.SimpleDate
import com.bitbytestudio.autosilentprayerapp.model.PrayersTime
import com.bitbytestudio.autosilentprayerapp.prefs.Prefs
import com.bitbytestudio.autosilentprayerapp.receiver.PrayersAlertReceiver
import com.bitbytestudio.autosilentprayerapp.ui.DndHandler
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.runBlocking
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale
import javax.inject.Inject

@HiltWorker
class PrayersWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters,
) : CoroutineWorker(context, params) {

    companion object{
        const val ALERT_DELAY_TIME = "ALERT_DELAY_TIME"
        const val ALERT_NAME = "ALERT_NAME"
        const val ALERT_ID = "ALERT_ID"
        const val IS_ENABLE = "IS_ENABLE"
    }

    @Inject lateinit var prefs: Prefs

    override suspend fun doWork(): Result {
        return try {
            Log.d("Prayer_tag", "doWork() -> Prayers Worker called")

            val prayers = getPrayersTime()
            for (prayer in prayers) {
                schedulePrayerAlarm(prayer)
            }

            Result.success()
        } catch (e: IOException) {
            Log.e("Prayer_tag", "Exception: ${e.localizedMessage}")
            Result.retry()
        }
    }

    private fun schedulePrayerAlarm(prayer: PrayersTime) {
        val alarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(applicationContext, PrayersAlertReceiver::class.java).apply {
            putExtra(ALERT_NAME, prayer.name)
            putExtra(ALERT_ID, prayer.id)
            putExtra(ALERT_DELAY_TIME, (15 * 60 * 1000L)) //15 minute delay // todo(add user preference delay time)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            prayer.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, prayer.hours)
            set(Calendar.MINUTE, prayer.minutes)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis < System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1) // Move to the next day if time has passed
            }
        }

        // Set exact alarm to trigger at prayer time
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )

        Log.d("Prayer_tag", "Alarm set for ${prayer.name} at ${formatMilliseconds(calendar.timeInMillis)}")
    }

    private suspend fun getPrayersTime(): List<PrayersTime> {
        val today = SimpleDate(GregorianCalendar())
        val location = Location(prefs.currentLat, prefs.currentLon, +6.0, 0)
        val azan = Azan(location, Method.KARACHI_HANAF)
        val prayerTimes = azan.getPrayerTimes(today)

        return listOf(
            PrayersTime(1, "Fajr", prayerTimes.fajr().hour, prayerTimes.fajr().minute),
            PrayersTime(2, "Dhuhr", prayerTimes.thuhr().hour, prayerTimes.thuhr().minute),
            PrayersTime(3, "Asr", prayerTimes.assr().hour, prayerTimes.assr().minute),
            PrayersTime(4, "Maghrib",17, 55 /*prayerTimes.maghrib().hour, prayerTimes.maghrib().minute*/),
            PrayersTime(5, "Isha", prayerTimes.ishaa().hour, prayerTimes.ishaa().minute)
        )
    }

    private fun formatMilliseconds(milliseconds: Long): String {
        val format = SimpleDateFormat("hh:mm:ss aa", Locale.getDefault())
        return format.format(Date(milliseconds))
    }
}
