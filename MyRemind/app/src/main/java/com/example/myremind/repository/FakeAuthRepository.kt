package com.example.myremind.repository

import com.example.myremind.data.model.User
import kotlinx.coroutines.delay

class FakeAuthRepository : AuthRepository {

    // penyimpanan user sementara di memory
    private val users = mutableListOf<User>()

    // user yang sekarang "login"
    private var activeUser: User? = null

    override suspend fun login(identifier: String, password: String): Result<User> {
        // delay biar kelihatan realistis nanti kalau kamu mau loading spinner
        delay(200)

        val found = users.find { u ->
            u.getEmail() == identifier && u.getPassword() == password
        }

        return if (found != null) {
            activeUser = found
            Result.success(found)
        } else {
            Result.failure(Exception("Email/username atau password salah"))
        }
    }

    override suspend fun signUp(
        username: String,
        email: String,
        password: String,
        verifyPassword: String
    ): Result<User> {
        delay(200)

        if (password != verifyPassword) {
            return Result.failure(Exception("Password tidak sama"))
        }

        if (users.any { it.getEmail() == email }) {
            return Result.failure(Exception("Email sudah digunakan"))
        }

        if (users.any { it.getUsername() == username }) {
            return Result.failure(Exception("Username sudah digunakan"))
        }

        val newUser = User(
            email = email,
            username = username,
            password = password
        )

        users.add(newUser)
        activeUser = newUser
        return Result.success(newUser)
    }

    override suspend fun resetPassword(email: String, newPassword: String): Result<Unit> {
        delay(200)

        val found = users.find { it.getEmail() == email }
        return if (found != null) {
            found.resetPassword(email, newPassword)
            Result.success(Unit)
        } else {
            Result.failure(Exception("Email tidak ditemukan"))
        }
    }

    override suspend fun signOut(): Result<Unit> {
        activeUser?.signOut()
        activeUser = null
        return Result.success(Unit)
    }

    override fun currentUser(): User? = activeUser
}
