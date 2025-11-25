package com.example.myremind.model

import java.util.Calendar

data class Alarm(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val repeatDays: List<Boolean> = List(7) { false },
    val dateMillis: Long? = null,
    val hour: Int? = null,
    val minute: Int? = null,
    val ownerType: String = "personal",
    val groupId: String? = null,
    val groupName: String? = null,
    val enabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
