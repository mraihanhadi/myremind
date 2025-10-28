package com.example.myremind.model

class MemoryUserRepository : UserRepository {

    private val users = mutableListOf(
        User(email = "abc@example.com", username = "abc", password = "123"),
        User(email = "cde@example.com", username = "cde", password = "12345"),
        User(email = "fgh@example.com", username = "fgh", password = "5678")
    )
    private var activeUser: User? = null

    override fun signIn(identifier: String, password: String): Result<User> {
        val user = users.find { u ->
            (u.getEmail() == identifier || u.getUsername() == identifier) &&
                    u.getPassword() == password
        }
        return if (user != null) {
            activeUser = user
            Result.success(user)
        } else {
            Result.failure(Exception("Email/username atau password salah"))
        }
    }

    override fun signUp(
        username: String,
        email: String,
        password: String,
        verifyPassword: String
    ): Result<User> {
        if (password != verifyPassword) {
            return Result.failure(Exception("Password tidak sama"))
        }
        if (users.any { it.getEmail() == email }) {
            return Result.failure(Exception("Email sudah digunakan"))
        }
        if (users.any { it.getUsername() == username }) {
            return Result.failure(Exception("Username sudah digunakan"))
        }

        val newUser = User(email, username, password)
        users.add(newUser)
        activeUser = newUser
        return Result.success(newUser)
    }

    override fun resetPassword(email: String, newPassword: String): Result<Unit> {
        val target = users.find { it.getEmail() == email }
        return if (target != null) {
            target.setPassword(newPassword)
            Result.success(Unit)
        } else {
            Result.failure(Exception("Email tidak ditemukan"))
        }
    }

    override fun signOut(): Result<Unit> {
        activeUser = null
        return Result.success(Unit)
    }

    override fun getCurrentUser(): User? = activeUser
}
