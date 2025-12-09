package com.example.myremind.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.myremind.model.Alarm
import java.util.Calendar

class AlarmScheduler(private val context: Context) {

    fun scheduleAlarm(alarm: Alarm) {
        val sanitizedAlarm = sanitizeAlarm(alarm) ?: return
        val triggerAtMillis = calculateNextTrigger(sanitizedAlarm) ?: return
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
        val pendingIntent = buildPendingIntent(sanitizedAlarm)

        runCatching {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        }.onFailure { error ->
            if (error is SecurityException) {
                val alarmClockInfo = AlarmManager.AlarmClockInfo(triggerAtMillis, pendingIntent)
                alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
            } else {
                Log.w(TAG, "Failed to schedule alarm ${sanitizedAlarm.id}", error)
            }
        }
    }

    fun cancelAlarm(alarm: Alarm) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val pendingIntent = buildPendingIntent(alarm)
        alarmManager?.cancel(pendingIntent)
    }

    private fun buildPendingIntent(alarm: Alarm): PendingIntent {
        val repeatDays = normalizeRepeatDays(alarm.repeatDays)
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(EXTRA_ALARM_ID, alarm.id)
            putExtra(EXTRA_ALARM_TITLE, alarm.title)
            putExtra(EXTRA_REPEAT_DAYS, repeatDays.toBooleanArray())
            putExtra(EXTRA_HOUR, alarm.hour ?: -1)
            putExtra(EXTRA_MINUTE, alarm.minute ?: -1)
        }

        return PendingIntent.getBroadcast(
            context,
            alarm.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    companion object {
        private const val TAG = "AlarmScheduler"
        const val EXTRA_ALARM_ID = "extra_alarm_id"
        const val EXTRA_ALARM_TITLE = "extra_alarm_title"
        const val EXTRA_REPEAT_DAYS = "extra_alarm_repeat_days"
        const val EXTRA_HOUR = "extra_alarm_hour"
        const val EXTRA_MINUTE = "extra_alarm_minute"

        fun normalizeRepeatDays(days: List<Boolean>): List<Boolean> =
            List(7) { index -> days.getOrNull(index) == true }

        fun calculateNextTrigger(alarm: Alarm): Long? {
            val hour = alarm.hour ?: return null
            val minute = alarm.minute ?: return null
            val hasRepeats = alarm.repeatDays.any { it }
            val now = Calendar.getInstance()

            val baseTime = Calendar.getInstance().apply {
                timeInMillis = now.timeInMillis
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            if (!hasRepeats) {
                if (baseTime.timeInMillis <= now.timeInMillis) {
                    baseTime.add(Calendar.DAY_OF_YEAR, 1)
                }
                return baseTime.timeInMillis
            }

            for (offset in 0..13) {
                val candidate = baseTime.clone() as Calendar
                candidate.add(Calendar.DAY_OF_YEAR, offset)
                val dayIndex = (candidate.get(Calendar.DAY_OF_WEEK) + 6) % 7
                if (alarm.repeatDays.getOrNull(dayIndex) != true) continue
                if (candidate.timeInMillis > now.timeInMillis) {
                    return candidate.timeInMillis
                }
            }

            return null
        }
    }

    private fun sanitizeAlarm(alarm: Alarm): Alarm? {
        val hour = alarm.hour ?: return null
        val minute = alarm.minute ?: return null
        val repeatDays = normalizeRepeatDays(alarm.repeatDays)
        return alarm.copy(hour = hour, minute = minute, repeatDays = repeatDays)
    }
}
