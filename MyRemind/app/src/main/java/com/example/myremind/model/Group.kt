package com.example.myremind.model

data class Group(
    val id: String = "",
    val groupName: String = "",
    val description: String = "",
    val members: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)