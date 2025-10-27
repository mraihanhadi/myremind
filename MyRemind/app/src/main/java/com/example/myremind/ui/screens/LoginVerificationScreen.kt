package com.example.myremind.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginVerificationScreen(
    onResend: (emailOrCode: String) -> Unit,
    onVerify: (emailOrCode: String, username: String) -> Unit
) {
    var emailCode by remember { mutableStateOf(TextFieldValue("")) }
    var username by remember { mutableStateOf(TextFieldValue("")) }
    val canVerify = emailCode.text.isNotBlank() && username.text.isNotBlank()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(96.dp))

            // BATAS LEBAR PERSIS KAYAK FRAME FIGMA
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 360.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Field: Email Verification
                WhitePillTextField(
                    value = emailCode,
                    onValueChange = { emailCode = it },
                    placeholder = "Email Verification",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                )

                Spacer(Modifier.height(8.dp))

// Resend: di bawah field 1, rata kanan
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    MiniWhitePill(
                        text = "Resend verification code",
                        onClick = { onResend(emailCode.text) },
                        modifier = Modifier.height(32.dp)
                    )
                }

                Spacer(Modifier.height(18.dp))

// Field: Username
                WhitePillTextField(
                    value = username,
                    onValueChange = { username = it },
                    placeholder = "Username",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                )

                Spacer(Modifier.height(24.dp))

// Verifikasi: rata kanan
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    WhiteActionPill(
                        text = "Verifikasi",
                        enabled = emailCode.text.isNotBlank() && username.text.isNotBlank(),
                        onClick = { if (emailCode.text.isNotBlank() && username.text.isNotBlank()) onVerify(emailCode.text, username.text) },
                        modifier = Modifier.height(40.dp)
                    )
                }
            }
        }
    }
}

/* ====== UI Helpers sesuai figma ====== */

@Composable
private fun WhitePillTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Color(0xFF9E9E9E), fontSize = 16.sp) },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = Color.Black,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black
        ),
        modifier = modifier
    )
}

@Composable
private fun MiniWhitePill(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier,
        onClick = onClick
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = text, color = Color(0xFF111111), fontSize = 14.sp)
        }
    }
}

@Composable
private fun WhiteActionPill(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (enabled) Color.White else Color(0xFFEDEDED),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier,
        onClick = { if (enabled) onClick() },
        enabled = enabled
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = text, color = Color(0xFF111111), fontSize = 16.sp)
            Spacer(Modifier.width(8.dp))
            Icon(Icons.Default.Check, null, tint = Color(0xFF111111))
        }
    }
}
