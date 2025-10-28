// file: model/AlarmForm.kt
package com.example.myremind.model

data class SelectableGroupOption(
    val label: String,
    val ownerType: String,
    val groupId: Int?,
    val groupName: String?
)

data class AddAlarmForm(
    val title: String,
    val days: List<Boolean>,
    val hour: Int?,
    val minute: Int?,
    val selectedTarget: SelectableGroupOption
)
