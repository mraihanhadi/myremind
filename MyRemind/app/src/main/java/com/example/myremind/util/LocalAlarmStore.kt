package com.example.myremind.util

import android.content.Context
import com.example.myremind.model.Alarm

class LocalAlarmStore(context: Context) {
    private val prefs = context.getSharedPreferences("alarms_local", Context.MODE_PRIVATE)

    private fun key(id: String, field: String) = "alarm_${id}_$field"

    fun upsert(alarm: Alarm) {
        if (alarm.id.isBlank()) return

        val ids = prefs.getStringSet("alarm_ids", emptySet())?.toMutableSet() ?: mutableSetOf()
        ids.add(alarm.id)

        prefs.edit()
            .putStringSet("alarm_ids", ids)
            .putString(key(alarm.id, "title"), alarm.title)
            .putInt(key(alarm.id, "hour"), alarm.hour ?: 0)
            .putInt(key(alarm.id, "minute"), alarm.minute ?: 0)
            .putBoolean(key(alarm.id, "enabled"), alarm.enabled)
            .putString(key(alarm.id, "repeat"), alarm.repeatDays.joinToString("") { if (it) "1" else "0" })
            .apply()
    }

    fun remove(id: String) {
        val ids = prefs.getStringSet("alarm_ids", emptySet())?.toMutableSet() ?: mutableSetOf()
        ids.remove(id)

        prefs.edit()
            .putStringSet("alarm_ids", ids)
            .remove(key(id, "title"))
            .remove(key(id, "hour"))
            .remove(key(id, "minute"))
            .remove(key(id, "enabled"))
            .remove(key(id, "repeat"))
            .apply()
    }

    fun get(id: String): Alarm? {
        if (id.isBlank()) return null
        val title = prefs.getString(key(id, "title"), null) ?: return null
        val hour = prefs.getInt(key(id, "hour"), 0)
        val minute = prefs.getInt(key(id, "minute"), 0)
        val enabled = prefs.getBoolean(key(id, "enabled"), true)
        val repeatStr = prefs.getString(key(id, "repeat"), "0000000") ?: "0000000"
        val repeatDays = repeatStr.map { it == '1' }

        return Alarm(
            id = id,
            title = title,
            hour = hour,
            minute = minute,
            enabled = enabled,
            repeatDays = repeatDays
        )
    }

    fun getAll(): List<Alarm> {
        val ids = prefs.getStringSet("alarm_ids", emptySet()) ?: emptySet()
        return ids.mapNotNull { get(it) }
    }
}
