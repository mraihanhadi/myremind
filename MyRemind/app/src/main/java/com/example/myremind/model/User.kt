package com.example.myremind.model

class User(
    private var email: String,
    private var username: String,
    private var password: String
) {
    fun getEmail(): String = email
    fun getUsername(): String = username
}
