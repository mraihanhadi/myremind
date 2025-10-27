package com.example.myremind.ui.screens

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

@Composable
fun SignUpScreen(
    onBackToLogin: () -> Unit,
    onSubmitSignUp: (username: String, email: String, password: String, verifyPassword: String) -> Unit
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
            SignUpTextField(username, { username = it }, "Username")
            Spacer(Modifier.height(24.dp))
            SignUpTextField(email, { email = it }, "Email")
            Spacer(Modifier.height(24.dp))
            SignUpTextField(password, { password = it }, "Password", isPassword = true)
            Spacer(Modifier.height(24.dp))
            SignUpTextField(verifyPassword, { verifyPassword = it }, "Verify Password", isPassword = true)

            Spacer(Modifier.height(32.dp))

            // Sign Up button (kanan)
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                SignUpButton(
                    onClick = {
                        onSubmitSignUp(
                            username.text.trim(),
                            email.text.trim(),
                            password.text.trim(),
                            verifyPassword.text.trim()
                        )
                    }
                )
            }

            Spacer(Modifier.weight(1f))

            // Link "Already have an account? Sign In"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        onBackToLogin()
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
    isPassword: Boolean = false
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
    onClick: () -> Unit
) {
    Surface(
        color = ButtonBg,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(
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
                contentDescription = "Sign up icon",
                tint = ButtonText,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
