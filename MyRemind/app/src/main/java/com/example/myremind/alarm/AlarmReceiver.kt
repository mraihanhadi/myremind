package com.example.myremind.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import com.example.myremind.model.Alarm

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getStringExtra(AlarmScheduler.EXTRA_ALARM_ID).orEmpty()
        val title = intent.getStringExtra(AlarmScheduler.EXTRA_ALARM_TITLE).orEmpty()
        val repeatDays = intent.getBooleanArrayExtra(AlarmScheduler.EXTRA_REPEAT_DAYS)?.toList() ?: emptyList()
        val hour = intent.getIntExtra(AlarmScheduler.EXTRA_HOUR, -1).takeIf { it >= 0 }
        val minute = intent.getIntExtra(AlarmScheduler.EXTRA_MINUTE, -1).takeIf { it >= 0 }

        NotificationHelper.showAlarmNotification(context, alarmId, title.ifBlank { "Alarm" })

        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val ringtone = RingtoneManager.getRingtone(context, alarmSound)
        ringtone?.play()

        if (repeatDays.any { it }) {
            val alarm = Alarm(
                id = alarmId,
                title = title,
                repeatDays = repeatDays,
                hour = hour,
                minute = minute
            )
            AlarmScheduler(context).scheduleAlarm(alarm)
        }
    }
}