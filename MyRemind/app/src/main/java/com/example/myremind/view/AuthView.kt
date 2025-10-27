package com.example.myremind.view

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myremind.data.model.User
import com.example.myremind.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthView (
    private val repo: AuthRepository
) : ViewModel() {

    // UI state
    private val _currentUser = mutableStateOf<User?>(repo.currentUser())
    val currentUser: State<User?> = _currentUser

    private val _loading = mutableStateOf(false)
    val loading: State<Boolean> = _loading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    fun clearError() {
        _errorMessage.value = null
    }

    fun login(identifier: String, password: String, onSuccess: () -> Unit) {
        _loading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            val result = repo.login(identifier, password)
            _loading.value = false

            result
                .onSuccess { user ->
                    _currentUser.value = user
                    onSuccess()
                }
                .onFailure { e ->
                    _errorMessage.value = e.message ?: "Login gagal"
                }
        }
    }

    fun signUp(
        username: String,
        email: String,
        password: String,
        verifyPassword: String,
        onSuccess: () -> Unit
    ) {
        _loading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            val result = repo.signUp(username, email, password, verifyPassword)
            _loading.value = false

            result
                .onSuccess { user ->
                    _currentUser.value = user
                    onSuccess()
                }
                .onFailure { e ->
                    _errorMessage.value = e.message ?: "Sign up gagal"
                }
        }
    }

    fun resetPassword(email: String, newPassword: String, onDone: () -> Unit) {
        _loading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            val result = repo.resetPassword(email, newPassword)
            _loading.value = false

            result
                .onSuccess {
                    onDone()
                }
                .onFailure { e ->
                    _errorMessage.value = e.message ?: "Reset password gagal"
                }
        }
    }

    fun signOut(onDone: () -> Unit) {
        viewModelScope.launch {
            repo.signOut()
            _currentUser.value = null
            onDone()
        }
    }
}