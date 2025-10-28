package com.example.myremind.controller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.myremind.model.*
import java.util.Calendar

class AlarmController(
    private val repo: MemoryAlarmRepository
) {
    var alarmList by mutableStateOf<List<Alarm>>(emptyList())
        private set

    var lastError by mutableStateOf<String?>(null)
        private set

    fun clearError() { lastError = null }

    fun refresh(joinedGroupIds: List<Int>) {
        val raw = repo.viewAllAlarms()
        alarmList = raw.filter { alarm ->
            when (alarm.getOwnerType()) {
                "personal" -> true
                "group" -> {
                    val gid = alarm.getGroupId()
                    gid != null && joinedGroupIds.contains(gid)
                }
                else -> false
            }
        }
    }

    fun createAlarm(
        title: String,
        hour: Int?,
        minute: Int?,
        repeatDays: List<Boolean>,
        ownerType: String,
        groupId: Int?,
        groupName: String?,
        onSuccess: (Alarm) -> Unit
    ) {
        if (title.isBlank()) {
            lastError = "Title can't be empty"
            return
        }
        if (hour == null || minute == null) {
            lastError = "Time is required"
            return
        }

        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val created = repo.addAlarm(
            alarmName = title,
            timeCal = cal,
            repeatDays = repeatDays,
            ownerType = ownerType,
            groupId = groupId,
            groupName = groupName
        )

        onSuccess(created)
    }

    fun getAlarmById(id: Int): EditableAlarmData? {
        val a = repo.getAlarm(id) ?: return null
        val timeCal = a.getTimeCal()

        return EditableAlarmData(
            id = a.getAlarmId(),
            title = a.getAlarmName(),
            repeatDays = a.getRepeatDays(),
            dateMillis = timeCal.timeInMillis,
            hour = timeCal.get(Calendar.HOUR_OF_DAY),
            minute = timeCal.get(Calendar.MINUTE),
            ownerType = a.getOwnerType(),
            groupId = a.getGroupId(),
            groupName = a.getGroupName()
        )
    }

    fun updateAlarm(
        id: Int,
        title: String,
        days: List<Boolean>,
        hour: Int?,
        minute: Int?,
        ownerType: String,
        groupId: Int?,
        groupName: String?,
        onSuccess: () -> Unit
    ) {
        // validasi kecil
        if (title.isBlank()) {
            lastError = "Title can't be empty"
            return
        }
        if (hour == null || minute == null) {
            lastError = "Time is required"
            return
        }

        val ok = repo.updateAlarm(
            id = id,
            newName = title,
            newRepeatDays = days,
            newHour = hour,
            newMinute = minute,
            newOwnerType = ownerType,
            newGroupId = groupId,
            newGroupName = groupName
        )

        if (!ok) {
            lastError = "Alarm not found"
            return
        }

        onSuccess()
    }

    fun deleteAlarms(
        ids: List<Int>,
        joinedGroupIdsAfterDelete: List<Int>,
        onSuccess: () -> Unit
    ) {
        if (ids.isEmpty()) {
            lastError = "No alarms selected"
            return
        }

        val ok = repo.deleteAlarmsByIds(ids)
        if (!ok) {
            lastError = "Failed to delete alarms"
            return
        }

        // refresh list visible untuk user saat ini
        refresh(joinedGroupIdsAfterDelete)

        onSuccess()
    }

}
