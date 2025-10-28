package com.example.myremind.model

data class EditableAlarmData(
    val id: Int,
    val title: String,
    val repeatDays: List<Boolean>,
    val dateMillis: Long?,
    val hour: Int?,
    val minute: Int?,
    val ownerType: String,
    val groupId: Int?,
    val groupName: String?
)