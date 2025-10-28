package com.example.myremind.model

interface AuthRepository {
    fun signUp(username: String, email: String, password: String, verifyPassword: String): Result<User>
    fun signIn(identifier: String, password: String): Result<User>
    fun resetPassword(email: String, newPassword: String): Result<Unit>
    fun signOut(): Result<Unit>
    fun getCurrentUser(): User?
}
