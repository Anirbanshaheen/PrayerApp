package com.bitbytestudio.autosilentprayerapp.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.bitbytestudio.autosilentprayerapp.R
import com.bitbytestudio.autosilentprayerapp.receiver.PrayersAlertReceiver

class DndHandler {

    // Function to enable DND mode
    fun enableDndMode(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (notificationManager.isNotificationPolicyAccessGranted) {
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE)
            Log.d("DND_DISABLE_TAG","enableDndMode")
        }
    }

    // Function to disable DND mode
    fun disableDndMode(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (notificationManager.isNotificationPolicyAccessGranted) {
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
            Log.d("DND_DISABLE_TAG","disableDndMode")
        }
    }

    fun sendNotification(context: Context, i: Intent) {
        val subTitle = if (i.getBooleanExtra("IS_ENABLE", false)) "Your phone is going to DND mode now." else "Back to general mode Now."

        Log.d("DND_DISABLE_TAG", "subTitle -> $subTitle")

        val id = i.getIntExtra("ID", 1) ?:1
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(PrayersAlertReceiver.NOTIFICATION_ID, id)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val titleNotification = i.getStringExtra("NAME")
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
        val notification = NotificationCompat.Builder(context,
            PrayersAlertReceiver.NOTIFICATION_CHANNEL
        )
            //.setLargeIcon(bitmap)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(titleNotification)
            .setContentText(subTitle)
            .setDefaults(NotificationCompat.DEFAULT_ALL).setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification.setChannelId(PrayersAlertReceiver.NOTIFICATION_CHANNEL)

            val ringtoneManager = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()

            val channel = NotificationChannel(
                PrayersAlertReceiver.NOTIFICATION_CHANNEL,
                PrayersAlertReceiver.NOTIFICATION_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )

            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            channel.setSound(ringtoneManager, audioAttributes)
            notificationManager.createNotificationChannel(channel)
            Log.d("DND_DISABLE_TAG", "if block")
        }
        notificationManager.notify(id, notification.build())
    }
}