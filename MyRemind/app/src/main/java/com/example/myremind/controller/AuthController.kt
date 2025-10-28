package com.example.myremind.controller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.myremind.model.AuthRepository
import com.example.myremind.model.User

class AuthController(
    private val repo: AuthRepository
) {
    var currentUser by mutableStateOf<User?>(repo.getCurrentUser())
        private set

    var lastError by mutableStateOf<String?>(null)
        private set

    var loading by mutableStateOf(false)
        private set

    fun clearError() {
        lastError = null
    }

    fun signIn(identifier: String, password: String, onSuccess: () -> Unit) {
        loading = true
        lastError = null

        val result = repo.signIn(identifier, password)

        loading = false

        result
            .onSuccess { user ->
                currentUser = user
                onSuccess()
            }
            .onFailure { e ->
                lastError = e.message ?: "Login gagal"
            }
    }

    fun signUp(username: String, email: String, password: String, verifyPassword: String, onSuccess: () -> Unit) {
        loading = true
        lastError = null

        val result = repo.signUp(username, email, password, verifyPassword)

        loading = false

        result
            .onSuccess { user ->
                currentUser = user
                onSuccess()
            }
            .onFailure { e ->
                lastError = e.message ?: "Sign up gagal"
            }
    }

    fun resetPassword(email: String, newPassword: String, onDone: () -> Unit) {
        loading = true
        lastError = null

        val result = repo.resetPassword(email, newPassword)

        loading = false

        result
            .onSuccess {
                onDone()
            }
            .onFailure { e ->
                lastError = e.message ?: "Reset password gagal"
            }
    }

    fun signOut(onDone: () -> Unit) {
        repo.signOut()
        currentUser = null
        onDone()
    }
}
