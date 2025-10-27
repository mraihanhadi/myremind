package com.example.myremind.data.model

class User (
    private var email: String,
    private var username: String,
    private var password: String
){
    fun signIn(email: String, password: String) {
        if (this.email == email && this.password == password) {
            println("Sign In successful for user: $username")
        } else {
            println("Invalid email or password.")
        }
    }
    fun signUp(email: String, password: String) {
        this.email = email
        this.password = password
        println("Sign Up successful! Welcome $username")
    }
    fun resetPassword(email: String, password: String) {
        if (this.email == email) {
            this.password = password
            println("Password reset successful for $email")
        } else {
            println("Email not found. Cannot reset password.")
        }
    }
    fun signOut() {
        println("User $username has signed out.")
    }
    fun getEmail(): String = email
    fun getUsername(): String = username
    fun getPassword(): String = password
    fun setEmail(email: String) {
        this.email = email
    }
    fun setUsername(username: String) {
        this.username = username
    }
}