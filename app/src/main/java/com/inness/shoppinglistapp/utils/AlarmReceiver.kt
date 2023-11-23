package com.inness.shoppinglistapp.utils
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager

import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

import com.inness.shoppinglistapp.R
import com.inness.shoppinglistapp.activities.AlarmRingsActivity


class AlarmReceiver : BroadcastReceiver() {

    private lateinit var ringtone: Ringtone


    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {

        val myNotification = intent.getStringExtra("myNotification")
        addNotification(myNotification,context)
        ringtonePlay(context)
    }

    private fun addNotification(text: String?, context: Context) {
        val soundUri = RingtoneManager.getActualDefaultRingtoneUri(context,RingtoneManager.TYPE_NOTIFICATION)

        val intent1 = Intent(context, AlarmRingsActivity::class.java)
        intent1.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 1, intent1, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_my_notification)
            .setContentTitle("Мое напоминание")
            //.setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle()
            .bigText(text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setSound(soundUri)

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(123, builder.build())
    }

    private fun ringtonePlay(context: Context){
        val notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ringtone = RingtoneManager.getRingtone(context,notificationUri)
        ringtone.play()
    }

    companion object{
        const val CHANNEL_ID = "foxandroid"
    }
}