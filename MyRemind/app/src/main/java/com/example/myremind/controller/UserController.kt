package com.example.myremind.controller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myremind.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserController : ViewModel() {

    var currentUser by mutableStateOf<User?>(null)

    var lastError by mutableStateOf<String?>(null)
        private set

    var loading by mutableStateOf(false)
        private set

    var infoMessage by mutableStateOf<String?>(null)
        private set

    fun clearError() { lastError = null }
    fun clearInfo() { infoMessage = null }

    fun signIn(identifier: String, password: String, onSuccess: () -> Unit) {
        val email = identifier.trim()

        if (email.isBlank() || password.isBlank()) {
            lastError = "Semua field wajib diisi"
            return
        }

        loading = true
        lastError = null

        viewModelScope.launch {
            try {
                val auth = FirebaseAuth.getInstance()
                val authResult = auth.signInWithEmailAndPassword(email, password).await()
                val firebaseUser = authResult.user ?: throw Exception("User Firebase null")

                firebaseUser.reload().await()

                if (!firebaseUser.isEmailVerified) {
                    auth.signOut()
                    throw Exception("Email belum diverifikasi. Silakan cek inbox atau spam untuk link verifikasi.")
                }

                val uid = firebaseUser.uid
                val firestore = FirebaseFirestore.getInstance()
                val snapshot = firestore.collection("users").document(uid).get().await()

                if (!snapshot.exists()) {
                    throw Exception("Data user tidak ditemukan.")
                }

                val user = User(
                    snapshot.getString("email") ?: "",
                    snapshot.getString("username") ?: ""
                )

                currentUser = user
                loading = false
                onSuccess()
            } catch (e: Exception) {
                loading = false
                lastError = when (e) {
                    is FirebaseAuthInvalidUserException,
                    is FirebaseAuthInvalidCredentialsException -> "Email atau password salah."
                    else -> e.message ?: "Terjadi kesalahan."
                }
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
        val emailTrim = email.trim()
        val emailLower = emailTrim.lowercase()
        val usernameTrim = username.trim()

        if (password != verifyPassword) {
            lastError = "Password dan konfirmasi tidak sama."
            return
        }

        if (usernameTrim.isBlank() || emailTrim.isBlank() || password.isBlank()) {
            lastError = "Semua field wajib diisi."
            return
        }

        loading = true
        lastError = null

        viewModelScope.launch {
            try {
                val auth = FirebaseAuth.getInstance()
                val authResult = auth.createUserWithEmailAndPassword(emailTrim, password).await()
                val firebaseUser = authResult.user ?: throw Exception("Gagal Membuat Akun")

                val firestore = FirebaseFirestore.getInstance()

                // User profile doc (private: only owner can read/write)
                val userData = mapOf(
                    "email" to emailTrim,
                    "emailLower" to emailLower,
                    "username" to usernameTrim
                )
                firestore.collection("users").document(firebaseUser.uid).set(userData).await()

                // Public-ish lookup doc to verify existence by email
                // Requires rules for /user_lookup/{email}
                firestore.collection("user_lookup").document(emailLower)
                    .set(mapOf("uid" to firebaseUser.uid))
                    .await()

                firebaseUser.sendEmailVerification().await()

                loading = false
                onSuccess()
            } catch (e: Exception) {
                loading = false
                lastError = when (e) {
                    is FirebaseAuthWeakPasswordException -> "Minimal panjang password adalah 6 karakter."
                    is FirebaseAuthUserCollisionException -> "Email sudah digunakan."
                    else -> e.message ?: "Terjadi kesalahan."
                }
            }
        }
    }

    fun resetPassword(email: String, onResult: (Boolean) -> Unit) {
        val emailTrim = email.trim()

        if (emailTrim.isBlank()) {
            lastError = "Email tidak boleh kosong"
            onResult(false)
            return
        }

        loading = true
        lastError = null

        viewModelScope.launch {
            try {
                val auth = FirebaseAuth.getInstance()
                auth.sendPasswordResetEmail(emailTrim).await()
                loading = false
                infoMessage = "Link reset password telah dikirim. Silahkan cek email."
                onResult(true)
            } catch (e: Exception) {
                loading = false
                lastError = when (e) {
                    is FirebaseAuthInvalidUserException -> "Email tidak terdaftar."
                    is FirebaseAuthInvalidCredentialsException -> "Format email tidak valid."
                    else -> e.message ?: "Gagal mengirim email reset password."
                }
                onResult(false)
            }
        }
    }

    fun signOut(onDone: () -> Unit) {
        FirebaseAuth.getInstance().signOut()
        currentUser = null
        onDone()
    }
}