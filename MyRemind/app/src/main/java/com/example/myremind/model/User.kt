package com.example.myremind.model

class User(
    private var email: String,
    private var username: String,
    private var password: String
) {
    fun signOut() {}
    fun getEmail(): String = email
    fun getUsername(): String = username
    fun getPassword(): String = password

    fun setEmail(email: String) {
        this.email = email
    }
    fun setUsername(username: String) {
        this.username = username
    }
    fun setPassword(password: String) {
        this.password = password
    }
}
