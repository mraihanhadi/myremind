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

    private companion object {
        const val ACTION_ALARM = "com.example.myremind.ALARM"
    }

    private fun pendingIntentFor(alarmId: String, title: String? = null): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_ALARM
            putExtra("ALARM_ID", alarmId)
            if (title != null) putExtra("ALARM_TITLE", title)
        }
        return PendingIntent.getBroadcast(
            context,
            alarmId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun schedule(alarm: Alarm) {
        // Simpan state terbaru dulu
        localStore.upsert(alarm)

        // Kalau disable: cancel + hapus local
        if (!alarm.enabled) {
            cancelById(alarm.id)
            return
        }

        val triggerAt = computeNextTriggerTime(alarm) ?: return

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAt,
            pendingIntentFor(alarm.id, alarm.title)
        )
    }

    fun cancelById(alarmId: String) {
        val pi = pendingIntentFor(alarmId)
        alarmManager.cancel(pi)
        pi.cancel()
        localStore.remove(alarmId)
    }

    // kalau kamu masih butuh cancel(alarm) biar kompatibel
    fun cancel(alarm: Alarm) = cancelById(alarm.id)

    fun rescheduleById(alarmId: String) {
        // Kalau alarm sudah dihapus/disable, localStore.get() akan null → tidak reschedule
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
            // one-shot: kalau sudah lewat, schedule besok (kalau ini memang yang kamu mau)
            if (base.timeInMillis <= now.timeInMillis) base.add(Calendar.DAY_OF_YEAR, 1)
            return base.timeInMillis
        }

        for (i in 0..7) {
            val candidate = base.clone() as Calendar
            candidate.add(Calendar.DAY_OF_YEAR, i)

            val dayIndex = (candidate.get(Calendar.DAY_OF_WEEK) - 1) // 0..6
            val isActive = repeat.getOrNull(dayIndex) == true
            val isFuture = candidate.timeInMillis > now.timeInMillis

            if (isActive && isFuture) return candidate.timeInMillis
        }

        return null
    }
}
