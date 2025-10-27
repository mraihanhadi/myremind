package com.example.myremind.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// kamu udah pake warna hitam/putih consistent, jadi kita define lagi lokal


private val FieldBg = Color(0xFFFFFFFF)
private val FieldPlaceholder = Color(0xFF9E9E9E)
private val ButtonBg = Color(0xFF3A3A3A)
private val ButtonText = Color(0xFFFFFFFF)
private val ErrorRed = Color(0xFFFF7070)

@Composable
fun LoginScreen(
    onSignIn: (identifier: String, password: String) -> Unit,
    onSignUp: () -> Unit,
    onResetPassword: () -> Unit,
    loading: Boolean,
    errorMessage: String?,
    onDismissError: () -> Unit,
) {
    var identifier by remember { mutableStateOf(TextFieldValue("")) } // username atau email
    var password by remember { mutableStateOf(TextFieldValue("")) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(Modifier.height(48.dp))

            // App Title
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "MyRemind",
                    color = TextWhite,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(64.dp))

            // Username / Email input
            AuthTextField(
                value = identifier,
                onValueChange = { identifier = it },
                placeholder = "Email",
                isPassword = false
            )

            Spacer(Modifier.height(24.dp))

            // Password input
            AuthTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = "Password",
                isPassword = true
            )

            Spacer(Modifier.height(24.dp))

            // Reset Password link
            Text(
                text = "Reset Password",
                color = TextWhite,
                fontSize = 20.sp,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    onResetPassword()
                }
            )

            Spacer(Modifier.height(24.dp))

            // Error message (kalau ada)
            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = ErrorRed,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            // biar bisa tap untuk clear error
                            onDismissError()
                        }
                )
            }

            // Sign In button
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                SignInButton(
                    enabled = !loading,
                    onClick = {
                        if (!loading) {
                            onDismissError()
                            onSignIn(identifier.text.trim(), password.text.trim())
                        }
                    }
                )
            }

            Spacer(Modifier.weight(1f))

            // Sign Up section bawah
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Don't have account?",
                    color = TextWhite,
                    fontSize = 18.sp
                )
                Spacer(Modifier.height(12.dp))
                SignUpActionButton(
                    enabled = !loading,
                    onClick = {
                        if (!loading) {
                            onDismissError()
                            onSignUp()
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun AuthTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    placeholder: String,
    isPassword: Boolean
) {
    Surface(
        color = FieldBg,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = FieldPlaceholder,
                    fontSize = 22.sp
                )
            },
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = 22.sp
            ),
            singleLine = true,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                cursorColor = Color.Black
            )
        )
    }
}

@Composable
private fun SignInButton(
    enabled: Boolean,
    onClick: () -> Unit
) {
    Surface(
        color = if (enabled) ButtonBg else ButtonBg.copy(alpha = 0.5f),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                enabled = enabled,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Sign In",
                color = ButtonText,
                fontSize = 24.sp
            )
            Spacer(Modifier.width(12.dp))
            Icon(
                imageVector = Icons.Default.Login,
                tint = ButtonText,
                contentDescription = "Sign in",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun SignUpActionButton(
    enabled: Boolean,
    onClick: () -> Unit
) {
    Surface(
        color = if (enabled) ButtonBg else ButtonBg.copy(alpha = 0.5f),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                enabled = enabled,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Sign Up",
                color = ButtonText,
                fontSize = 24.sp
            )
            Spacer(Modifier.width(12.dp))
            Icon(
                imageVector = Icons.Default.Person,
                tint = ButtonText,
                contentDescription = "Sign up",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
