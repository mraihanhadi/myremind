package com.example.myremind.model

data class User(
    val email: String,
    val username: String,
    val uid: String? = null
) {
    fun getEmail(): String = email
    fun getUsername(): String = username
}
