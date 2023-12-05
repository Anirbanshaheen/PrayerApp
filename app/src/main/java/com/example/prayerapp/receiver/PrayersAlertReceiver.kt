package com.example.prayerapp.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.prayerapp.R
import com.example.prayerapp.prefs.Prefs
import com.example.prayerapp.ui.DndHandler
import com.example.prayerapp.ui.MainActivity
import javax.inject.Inject

class PrayersAlertReceiver : BroadcastReceiver() {
    private val dndHandler by lazy { DndHandler() }

    @Inject
    lateinit var prefs: Prefs

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("CHECK_TIME", "Prayers call")
        Log.d("TAKE_TIME", "Broadcast Receiver call")
        sendNotification(context, intent)
        changeRingingMode(context, intent)
    }

//    private fun changeRingingMode(context: Context, intent: Intent) {
//        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
//        audioManager.ringerMode = AudioManager.RINGER_MODE_VIBRATE
//        Log.d("CHECK_TIME", "user delay time : ${intent.getIntExtra("DELAY_TIME", (60000 * 15)).toLong()}")
//        Handler(Looper.getMainLooper()).postDelayed({
//            audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
//            Log.d("CHECK_TIME", "Back to normal mode")
//        }, intent.getIntExtra("DELAY_TIME", (60000 * 15)).toLong())
//        // audioManager.ringerMode = AudioManager.RINGER_MODE_VIBRATE
//        // audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
//    }

    private fun changeRingingMode(context: Context, intent: Intent) {
        dndHandler.enableDndMode(context)

        Log.d(
            "CHECK_TIME",
            "user delay time : ${intent.getIntExtra("DELAY_TIME", (60000 * 15)).toLong()}"
        )
        Handler(Looper.getMainLooper()).postDelayed({
            dndHandler.disableDndMode(context)
            Log.d("CHECK_TIME", "Back to normal mode")
        }, intent.getIntExtra("DELAY_TIME", (60000 * 15)).toLong())
    }

    private fun sendNotification(context: Context, i: Intent) {

        val id = i.getIntExtra("ID", 1)
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(NOTIFICATION_ID, id)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val titleNotification = i.getStringExtra("NAME")
        val subtitleNotification = "Your Phone Is Going To Vibrate Mode Now."
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)
            //.setLargeIcon(bitmap)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(titleNotification).setContentText(subtitleNotification)
            .setDefaults(NotificationCompat.DEFAULT_ALL).setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notification.priority = NotificationCompat.PRIORITY_MAX

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification.setChannelId(NOTIFICATION_CHANNEL)

            val ringtoneManager = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes =
                AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()

            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL,
                NOTIFICATION_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )

            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            channel.setSound(ringtoneManager, audioAttributes)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(id, notification.build())
    }

    companion object {
        const val NOTIFICATION_ID = "appName_notification_id"
        const val NOTIFICATION_NAME = "appName"
        const val NOTIFICATION_CHANNEL = "appName_channel_01"
    }
}

