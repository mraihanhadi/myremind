package com.example.myremind.model

import java.util.Calendar

data class Alarm(
    private val alarm_Id: Int,
    private val alarm_Name: String,
    private val timeCal: Calendar,
    private val repeatDays: List<Boolean>,
    private val ownerType: String,
    private val groupId: Int?,
    private val groupName: String?
) {
    fun getAlarmId(): Int = alarm_Id
    fun getAlarmName(): String = alarm_Name
    fun getTimeCal(): Calendar = timeCal
    fun getRepeatDays(): List<Boolean> = repeatDays
    fun getOwnerType(): String = ownerType
    fun getGroupId(): Int? = groupId
    fun getGroupName(): String? = groupName

    fun copyInternal(
        alarmName: String = alarm_Name,
        timeCal: Calendar = this.timeCal,
        repeatDays: List<Boolean> = this.repeatDays,
        ownerType: String = this.ownerType,
        groupId: Int? = this.groupId,
        groupName: String? = this.groupName
    ): Alarm {
        return Alarm(
            alarm_Id = alarm_Id,
            alarm_Name = alarmName,
            timeCal = timeCal,
            repeatDays = repeatDays,
            ownerType = ownerType,
            groupId = groupId,
            groupName = groupName
        )
    }
}
