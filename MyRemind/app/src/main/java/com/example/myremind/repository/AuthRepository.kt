package com.example.myremind.repository

import com.example.myremind.data.model.User

/**
 * Kontrak modul autentikasi.
 * ViewModel / UI hanya boleh bergantung ke interface ini.
 * Implementasi konkretnya (Fake / Remote / Database) tinggal ditukar.
 */
interface AuthRepository {

    /**
     * Login dengan username OR email + password
     * return Result.success(User) jika sukses
     * return Result.failure(Exception) jika gagal
     */
    suspend fun login(identifier: String, password: String): Result<User>

    /**
     * Registrasi user baru
     */
    suspend fun signUp(
        username: String,
        email: String,
        password: String,
        verifyPassword: String
    ): Result<User>

    /**
     * Reset password user (skenario: kirim password baru / link reset)
     * Untuk sekarang, kita langsung ganti password-nya in-memory.
     */
    suspend fun resetPassword(email: String, newPassword: String): Result<Unit>

    /**
     * Logout user saat ini
     */
    suspend fun signOut(): Result<Unit>

    /**
     * User yang saat ini sedang login (null kalau belum login)
     */
    fun currentUser(): User?
}
