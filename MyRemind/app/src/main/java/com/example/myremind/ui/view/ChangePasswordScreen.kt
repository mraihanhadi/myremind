package com.example.myremind.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val PlaceholderGray = Color(0xFF9E9E9E)
private val ErrorRed = Color(0xFFFF7070)

@Composable
fun ChangePasswordScreen(
    loading: Boolean,
    errorMessage: String?,
    onDismissError: () -> Unit,
    onSubmit: (newPassword: String) -> Unit
) {
    var password by remember { mutableStateOf("") }

    val canSubmit = password.isNotBlank() && !loading

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
            Spacer(Modifier.height(80.dp))

            Text(
                text = "New Password",
                color = TextWhite,
                fontSize = 32.sp
            )

            Spacer(Modifier.height(32.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 360.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = {
                        Text(
                            text = "New Password",
                            color = PlaceholderGray,
                            fontSize = 20.sp
                        )
                    },
                    visualTransformation = PasswordVisualTransformation(),
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                )

                Spacer(Modifier.height(12.dp))

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

                Spacer(Modifier.height(24.dp))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Surface(
                        color = if (canSubmit) Color.White else Color(0xFFEDEDED),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable(
                                enabled = canSubmit,
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                onSubmit(password)
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .height(36.dp)
                                .padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Ubah Password",
                                color = Color(0xFF111111)
                            )
                            Spacer(Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Submit",
                                tint = Color(0xFF111111)
                            )
                        }
                    }
                }
            }
        }
    }
}
