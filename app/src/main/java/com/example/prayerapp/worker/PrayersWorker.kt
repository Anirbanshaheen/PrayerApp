package com.example.prayerapp.worker

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.azan.Azan
import com.azan.Method
import com.azan.astrologicalCalc.Location
import com.azan.astrologicalCalc.SimpleDate
import com.example.prayerapp.model.PrayersTime
import com.example.prayerapp.prefs.Prefs
import com.example.prayerapp.receiver.PrayersAlertReceiver
import com.example.prayerapp.ui.DndHandler
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.runBlocking
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import javax.inject.Inject

@HiltWorker
class PrayersWorker @AssistedInject constructor (@Assisted private val context: Context, @Assisted private val params: WorkerParameters) :
    CoroutineWorker(context, params) {

    private lateinit var alarmManager: AlarmManager
    @Inject
    lateinit var prefs: Prefs

    override suspend fun doWork(): Result {
        //prefs = Prefs(context)
        return try {
            Log.d("Prayer_tag", "doWork() -> Prayers Worker call")
            alarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            for (i in getPrayersTime()) {
                Log.d("Prayer_tag", "getPrayersTime() -> time : $i")
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, i.hours)
                    set(Calendar.MINUTE, i.minutes)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                if (calendar.timeInMillis >= System.currentTimeMillis()) {
                    Log.d("Prayer_tag", "if -> time : ${i.hours}")
                    setRepeatingAlarmExactTime(i.id, i.name, i.hours, i.minutes)
                } else {
                    val hours = i.hours + 24
                    Log.d("Prayer_tag", "else -> time : ${i.hours}")
                    setRepeatingAlarmExactTime(i.id, i.name, hours, i.minutes)
                }
            }

            Result.success()
        } catch (e: IOException) {
            Log.d("Prayer_tag", "exception -> ${e.localizedMessage}")
            Result.retry()
        }
    }

    private fun setRepeatingAlarmExactTime(id: Int, name: String, hours: Int, minutes: Int) {
        val intent = Intent(applicationContext, PrayersAlertReceiver::class.java)
        intent.putExtra("NAME", name)
        intent.putExtra("ID", id)
        intent.putExtra("DELAY_TIME", (1 * 60 * 1000))

        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(
                applicationContext,
                id,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getBroadcast(
                applicationContext,
                id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hours)
            set(Calendar.MINUTE, minutes)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        //Log.d("wwe", "Calendar =>  ${calendar.timeInMillis}")
//        alarmManager.cancel(pendingIntent) // first cancel alarm then set the new alarm
//        alarmManager.setExact(
//            AlarmManager.ELAPSED_REALTIME_WAKEUP,
//            calendar.timeInMillis,
//            pendingIntent
//        )

        alarmManager.cancel(pendingIntent)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
        Log.d("Prayer_tag", "Calendar =>  ${calendar.timeInMillis}")
        Log.d("Prayer_tag", "final =>  ${formatMilliseconds(calendar.timeInMillis)}")
    }

    private fun getPrayersTime() = runBlocking {
        val today = SimpleDate(GregorianCalendar())
        val location = Location(23.7561, 90.3872, +6.0, 0)
        val azan = Azan(location, Method.KARACHI_HANAF)
        val prayerTimes = azan.getPrayerTimes(today)
        //val imsaak = azan.getImsaak(today)

        val prayerTimeList = arrayListOf(
            PrayersTime(
                1,
                "Fajr Time",
//                10,
//                10
                prayerTimes.fajr().hour,
                prayerTimes.fajr().minute
            ),
            PrayersTime(
                2,
                "Dhuhr Time",
//                15,
//                26
                prayerTimes.thuhr().hour,
                prayerTimes.thuhr().minute
            ),
            PrayersTime(
                3,
                "Asr Time",
                prayerTimes.assr().hour,
                prayerTimes.assr().minute
            ),
            PrayersTime(
                4,
                "Maghrib Time",
                prayerTimes.maghrib().hour,
                prayerTimes.maghrib().minute
            ),
            PrayersTime(
                5,
                "Isha Time",
                prayerTimes.ishaa().hour,
                prayerTimes.ishaa().minute
            )
        )
        return@runBlocking prayerTimeList
    }

    private fun formatMilliseconds(milliseconds: Long): String {
        val format = SimpleDateFormat("hh:mm:ss aa")
        return format.format(Date(milliseconds))
    }
}