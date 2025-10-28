package com.example.myremind.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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

private val FieldBg = Color(0xFFFFFFFF)
private val FieldPlaceholder = Color(0xFF9E9E9E)
private val ButtonBg = Color(0xFFDADADA)
private val ButtonText = Color(0xFF000000)
private val ErrorRed = Color(0xFFFF7070)

@Composable
fun SignUpScreen(
    onBackToLogin: () -> Unit,
    onSubmitSignUp: (username: String, email: String, password: String, verifyPassword: String) -> Unit,
    loading: Boolean,
    errorMessage: String?,
    onDismissError: () -> Unit
) {
    var username by remember { mutableStateOf(TextFieldValue("")) }
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var verifyPassword by remember { mutableStateOf(TextFieldValue("")) }

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

            // Title
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

            // Fields
            SignUpTextField(username, { username = it }, "Username", false)
            Spacer(Modifier.height(24.dp))
            SignUpTextField(email, { email = it }, "Email", false)
            Spacer(Modifier.height(24.dp))
            SignUpTextField(password, { password = it }, "Password", true)
            Spacer(Modifier.height(24.dp))
            SignUpTextField(verifyPassword, { verifyPassword = it }, "Verify Password", true)

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
                            onDismissError()
                        }
                )
            }

            Spacer(Modifier.height(8.dp))

            // Sign Up button kanan
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                SignUpButton(
                    enabled = !loading,
                    onClick = {
                        if (!loading) {
                            onDismissError()
                            onSubmitSignUp(
                                username.text.trim(),
                                email.text.trim(),
                                password.text.trim(),
                                verifyPassword.text.trim()
                            )
                        }
                    }
                )
            }

            Spacer(Modifier.weight(1f))

            // "Already have an account? Sign In"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp)
                    .clickable(
                        enabled = !loading,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        if (!loading) {
                            onDismissError()
                            onBackToLogin()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Already have an account? Sign In",
                    color = TextWhite,
                    fontSize = 18.sp,
                    textDecoration = TextDecoration.Underline
                )
            }
        }
    }
}

@Composable
private fun SignUpTextField(
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
private fun SignUpButton(
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
                contentDescription = "Sign up icon",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
