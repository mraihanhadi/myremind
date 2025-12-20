package com.example.myremind.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.myremind.util.AlarmScheduler
import com.example.myremind.util.NotificationHelper

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getStringExtra("ALARM_ID") ?: return
        val alarmTitle = intent.getStringExtra("ALARM_TITLE") ?: "Alarm"
        val scheduler = AlarmScheduler(context)
        NotificationHelper(context).showNotification(
            alarmId = alarmId,
            title = alarmTitle,
            message = "Your alarm is ringing"
        )
        scheduler.rescheduleById(alarmId)
    }
}
