package com.example.myremind.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.myremind.model.Alarm
import com.example.myremind.receiver.AlarmReceiver
import java.util.Calendar

class AlarmScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val localStore = LocalAlarmStore(context)

    fun schedule(alarm: Alarm) {
        if (!alarm.enabled) {
            cancel(alarm)
            localStore.upsert(alarm)
            return
        }
        localStore.upsert(alarm)
        val triggerAt = computeNextTriggerTime(alarm) ?: return
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("ALARM_ID", alarm.id)
            putExtra("ALARM_TITLE", alarm.title)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAt,
            pendingIntent
        )
    }
    fun cancel(alarm: Alarm) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        localStore.remove(alarm.id)
    }
    fun rescheduleById(alarmId: String) {
        val alarm = localStore.get(alarmId) ?: return
        schedule(alarm)
    }
    private fun computeNextTriggerTime(alarm: Alarm): Long? {
        val hour = alarm.hour ?: return null
        val minute = alarm.minute ?: return null

        val now = Calendar.getInstance()

        val base = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val repeat = alarm.repeatDays
        val hasRepeat = repeat.any { it }

        if (!hasRepeat) {
            // one-shot: kalau sudah lewat, schedule besok
            if (base.timeInMillis <= now.timeInMillis) base.add(Calendar.DAY_OF_YEAR, 1)
            return base.timeInMillis
        }

        // repeat: cari max 7 hari ke depan yang aktif
        for (i in 0..7) {
            val candidate = base.clone() as Calendar
            candidate.add(Calendar.DAY_OF_YEAR, i)

            // Calendar: SUNDAY=1 ... SATURDAY=7
            val dayIndex = (candidate.get(Calendar.DAY_OF_WEEK) - 1) // 0..6
            val isActive = repeat.getOrNull(dayIndex) == true
            val isFuture = candidate.timeInMillis > now.timeInMillis

            if (isActive && isFuture) return candidate.timeInMillis
        }

        return null
    }
}
