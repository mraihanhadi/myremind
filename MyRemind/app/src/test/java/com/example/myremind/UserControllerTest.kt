package com.example.myremind.controller

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Test

class UserControllerTest {

    @Test
    fun signIn_emailKosong_passwordAda_errorSemuaFieldWajib() {
        val controller = UserController()

        // identifier/email kosong
        controller.signIn(identifier = "   ", password = "123456") {
            // Tidak akan terpanggil karena harusnya gagal di validasi awal
            assertFalse("onSuccess tidak boleh terpanggil", true)
        }

        assertEquals("Semua field wajib diisi", controller.lastError)
    }

    @Test
    fun signIn_emailAda_passwordKosong_errorSemuaFieldWajib() {
        val controller = UserController()

        controller.signIn(identifier = "user@gmail.com", password = "   ") {
            assertFalse("onSuccess tidak boleh terpanggil", true)
        }

        assertEquals("Semua field wajib diisi", controller.lastError)
    }

    @Test
    fun signUp_passwordTidakSama_errorKonfirmasi() {
        val controller = UserController()

        controller.signUp(
            username = "Rei",
            email = "rei@gmail.com",
            password = "123456",
            verifyPassword = "654321",
            onSuccess = { assertFalse("onSuccess tidak boleh terpanggil", true) }
        )

        assertEquals("Password dan konfirmasi tidak sama.", controller.lastError)
    }

    @Test
    fun signUp_fieldKosong_errorSemuaFieldWajib() {
        val controller = UserController()

        controller.signUp(
            username = "   ",
            email = "rei@gmail.com",
            password = "123456",
            verifyPassword = "123456",
            onSuccess = { assertFalse("onSuccess tidak boleh terpanggil", true) }
        )

        assertEquals("Semua field wajib diisi.", controller.lastError)
    }

    @Test
    fun signUp_emailKosong_errorSemuaFieldWajib() {
        val controller = UserController()

        controller.signUp(
            username = "Rei",
            email = "   ",
            password = "123456",
            verifyPassword = "123456",
            onSuccess = { assertFalse("onSuccess tidak boleh terpanggil", true) }
        )

        assertEquals("Semua field wajib diisi.", controller.lastError)
    }

    @Test
    fun signUp_passwordKosong_errorSemuaFieldWajib() {
        val controller = UserController()

        controller.signUp(
            username = "Rei",
            email = "rei@gmail.com",
            password = "",
            verifyPassword = "",
            onSuccess = { assertFalse("onSuccess tidak boleh terpanggil", true) }
        )

        assertEquals("Semua field wajib diisi.", controller.lastError)
    }

    /**
     * TC-01
     * Menguji reset password dengan input email kosong
     * Expected result: gagal dan menampilkan pesan error
     */
    @Test
    fun resetPassword_emailKosong_gagal() {
        // Arrange
        val controller = UserController()
        var result: Boolean? = null

        // Act
        controller.resetPassword("") {
            result = it
        }

        // Assert
        assertFalse(result!!)
        assertEquals("Email tidak boleh kosong", controller.lastError)
    }

    /**
     * TC-02
     * Menguji reset password dengan input spasi saja
     * Expected result: gagal dan menampilkan pesan error
     */
    @Test
    fun resetPassword_emailSpasi_gagal() {
        // Arrange
        val controller = UserController()
        var result: Boolean? = null

        // Act
        controller.resetPassword("   ") {
            result = it
        }

        // Assert
        assertFalse(result!!)
        assertEquals("Email tidak boleh kosong", controller.lastError)
    }

    /**
     * TC-03
     * Menguji bahwa error di-reset sebelum proses dijalankan
     */
    @Test
    fun resetPassword_errorSebelumnya_dibersihkan() {
        // Arrange
        val controller = UserController()
        controller.clearError()

        // Act
        controller.resetPassword("") {}

        // Assert
        assertNotNull(controller.lastError)
    }
}
