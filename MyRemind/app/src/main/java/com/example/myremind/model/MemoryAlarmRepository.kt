package com.example.myremind.model

import java.util.Calendar

// contoh minimal
class MemoryAlarmRepository {

    private val alarms = mutableListOf<Alarm>()
    private var nextId = 1

    fun viewAllAlarms(): List<Alarm> = alarms.toList()

    fun addAlarm(
        alarmName: String,
        timeCal: Calendar,
        repeatDays: List<Boolean>,
        ownerType: String,
        groupId: Int?,
        groupName: String?
    ): Alarm {
        val newAlarm = Alarm(
            alarm_Id = nextId++,
            alarm_Name = alarmName,
            timeCal = timeCal,
            repeatDays = repeatDays,
            ownerType = ownerType,
            groupId = groupId,
            groupName = groupName
        )
        alarms += newAlarm
        return newAlarm
    }

    fun getAlarm(id: Int): Alarm? =
        alarms.firstOrNull { it.getAlarmId() == id }

    fun updateAlarm(
        id: Int,
        newName: String,
        newRepeatDays: List<Boolean>,
        newHour: Int?,
        newMinute: Int?,
        newOwnerType: String,
        newGroupId: Int?,
        newGroupName: String?
    ): Boolean {
        val idx = alarms.indexOfFirst { it.getAlarmId() == id }
        if (idx == -1) return false

        val old = alarms[idx]

        // rebuild Calendar
        val cal = (old.getTimeCal().clone() as Calendar).apply {
            if (newHour != null) {
                set(Calendar.HOUR_OF_DAY, newHour)
            }
            if (newMinute != null) {
                set(Calendar.MINUTE, newMinute)
            }
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val updated = old.copyInternal(
            alarmName = newName,
            timeCal = cal,
            repeatDays = newRepeatDays,
            ownerType = newOwnerType,
            groupId = newGroupId,
            groupName = newGroupName
        )

        alarms[idx] = updated
        return true
    }

    fun deleteAlarmsByIds(ids: List<Int>): Boolean {
        val before = alarms.size
        alarms.removeAll { alarm -> ids.contains(alarm.getAlarmId()) }
        val after = alarms.size
        return after < before
    }
}

