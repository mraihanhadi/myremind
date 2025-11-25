package com.example.myremind.controller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import com.example.myremind.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserController() : ViewModel() {
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
        if(identifier.isBlank() || password.isBlank()){
            lastError = "Semua field wajib diisi"
            return
        }
        loading = true
        lastError = null
        viewModelScope.launch{
            try{
                val auth = FirebaseAuth.getInstance()
                val authResult = auth.signInWithEmailAndPassword(identifier,password).await()
                val firebaseUser = authResult.user?: throw Exception("User Firebase null")
                firebaseUser.reload().await()
                if(!firebaseUser.isEmailVerified){
                    auth.signOut()
                    throw Exception("Email belum diverifikasi. Silakan cek inbox atau spam untuk link verifikasi.")
                }
                val uid = firebaseUser.uid
                val firestore = FirebaseFirestore.getInstance()
                val snapshot = firestore.collection("users").document(uid).get().await()
                if(!snapshot.exists()){
                    throw Exception("Data user tidak ditemukan.")
                }
                val user = User(
                    email = snapshot.getString("email") ?: "",
                    username = snapshot.getString("username") ?: "",
                    uid = uid
                )
                currentUser = user
                loading = false
                onSuccess()
            }catch (e:Exception){
                loading = false
                lastError = when (e) {
                    is FirebaseAuthInvalidUserException, is FirebaseAuthInvalidCredentialsException -> {
                        "Email atau password salah."
                    }
                    else -> e.message ?: "Terjadi kesalahan."
                }
            }
        }
    }

    fun signUp(username: String, email: String, password: String, verifyPassword: String, onSuccess: () -> Unit) {
        if (password != verifyPassword) {
            lastError = "Password dan konfirmasi tidak sama."
            return
        }
        if (username.isBlank() || email.isBlank() || password.isBlank()) {
            lastError = "Semua field wajib diisi."
            return
        }

        loading = true
        lastError = null

        viewModelScope.launch{
            try{
                val auth = FirebaseAuth.getInstance()
                val authResult = auth.createUserWithEmailAndPassword(email,password).await()
                val firebaseUser = authResult.user?: throw Exception("Gagal Membuat Akun")
                val firestore = FirebaseFirestore.getInstance()
                val userData = mapOf("email" to email, "username" to username)
                firestore.collection("users").document(firebaseUser.uid).set(userData).await()
                firebaseUser.sendEmailVerification().await()
                currentUser = User(email = email, username = username, uid = firebaseUser.uid)
                loading = false
                onSuccess()
            }catch (e:Exception){
                loading = false
                lastError = when (e) {
                    is FirebaseAuthWeakPasswordException -> {
                        "Minimal panjang password adalah 6 karakter."
                    }
                    is FirebaseAuthUserCollisionException -> {
                        "Email sudah digunakan."
                    }
                    else -> e.message ?: "Terjadi kesalahan."
                }
            }
        }
    }

    fun resetPassword(email: String, onResult: (Boolean) -> Unit) {
        if (email.isBlank()){
            lastError = "Email tidak boleh kosong"
            onResult(false)
            return
        }
        loading = true
        lastError = null

        viewModelScope.launch{
            try{
                val auth = FirebaseAuth.getInstance()
                auth.sendPasswordResetEmail(email).await()
                loading = false
                infoMessage = "Jika akun terdaftar, link reset password telah dikirim."
                onResult(true)
            }catch (e:Exception){
                loading = false
                lastError = when (e) {
                    is FirebaseAuthInvalidCredentialsException ->
                        "Format email tidak valid."

                    else ->
                        "Jika akun terdaftar, link reset password telah dikirim."
                }
                onResult(false)
            }
        }
    }

    fun signOut(onDone: () -> Unit) {
        val auth = FirebaseAuth.getInstance()
        auth.signOut()
        currentUser = null
        onDone()
    }
}
