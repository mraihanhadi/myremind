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
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.remember

// fallback warna kalau variable global belum keimport
private val ScreenBlackFallback = Color(0xFF000000)
private val TextWhiteFallback = Color(0xFFFFFFFF)
private val FieldBgFallback = Color(0xFFFFFFFF)
private val FieldPlaceholder = Color(0xFF9E9E9E)
private val ButtonBg = Color(0xFF2E2E2E)

private val ScreenBgColor @Composable get() = try { ScreenBlack } catch (_: Throwable) { ScreenBlackFallback }
private val TextWhiteColor @Composable get() = try { TextWhite } catch (_: Throwable) { TextWhiteFallback }

@Composable
fun LoginScreen(
    onSignIn: (username: String, password: String) -> Unit,
    onSignUp: () -> Unit,
    onResetPassword: () -> Unit,
) {
    var username by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBgColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ===== APP TITLE =====
            Spacer(Modifier.height(48.dp))
            Text(
                text = "MyRemind",
                color = TextWhiteColor,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 52.sp
            )

            Spacer(Modifier.height(64.dp))

            // ===== USERNAME FIELD =====
            AuthTextField(
                value = username,
                onValueChange = { username = it },
                placeholder = "Username",
                isPassword = false
            )

            Spacer(Modifier.height(24.dp))

            // ===== PASSWORD FIELD =====
            AuthTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = "Password",
                isPassword = true
            )

            Spacer(Modifier.height(24.dp))

            // ===== RESET PASSWORD (LEFT ALIGNED, UNDERLINED) =====
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickableNoRipple { onResetPassword() }
            ) {
                Text(
                    text = "Reset Password",
                    color = TextWhiteColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            }

            Spacer(Modifier.height(48.dp))

            // ===== SIGN IN BUTTON (RIGHT SIDE) =====
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                DarkActionButton(
                    label = "Sign In",
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Login,
                            contentDescription = "Sign In",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    onClick = {
                        onSignIn(username.text.trim(), password.text.trim())
                    }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // ===== SIGN UP SECTION (BOTTOM) =====
            Text(
                text = "Don’t have account?",
                color = TextWhiteColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 24.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            DarkActionButton(
                label = "Sign Up",
                icon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Sign Up",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                },
                onClick = { onSignUp() }
            )

            Spacer(Modifier.height(48.dp))
        }
    }
}


// ============ COMPONENT: TextField Putih Rounded ============
@Composable
private fun AuthTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    placeholder: String,
    isPassword: Boolean
) {
    Surface(
        color = FieldBgFallback,
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
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Normal
                )
            },
            singleLine = true,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = 22.sp,
                fontWeight = FontWeight.Normal
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
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


// ============ COMPONENT: Tombol Dark Rounded dengan Icon kanan ============
@Composable
private fun DarkActionButton(
    label: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Surface(
        color = ButtonBg,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickableNoRipple { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 28.sp
            )

            Spacer(Modifier.width(12.dp))

            icon()
        }
    }
}


// ============ Helper: clickable tanpa ripple ============
@Composable
private fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier = composed {
    val interaction = remember { MutableInteractionSource() }
    this.then(
        Modifier.clickable(
            indication = null,
            interactionSource = interaction
        ) { onClick() }
    )
}
