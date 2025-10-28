package com.example.myremind.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val PlaceholderGray = Color(0xFF9E9E9E)


@Composable
fun GroupCreateScreen(
    loading: Boolean,
    errorMessage: String?,
    onDismissError: () -> Unit,
    onBack: () -> Unit,
    onCreateGroup: (groupName: String, description: String) -> Unit
) {
    var groupName by remember { mutableStateOf(TextFieldValue("")) }
    var description by remember { mutableStateOf(TextFieldValue("")) }

    val canSubmit = groupName.text.isNotBlank() && !loading

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
            horizontalAlignment = Alignment.Start
        ) {

            Spacer(Modifier.height(24.dp))

            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    tint = TextWhite,
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(32.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            onBack()
                        }
                )

                Spacer(Modifier.width(16.dp))

                Text(
                    text = "Group",
                    color = TextWhite,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(64.dp))

            
            TextField(
                value = groupName,
                onValueChange = { groupName = it },
                placeholder = {
                    Text(
                        text = "Nama Group",
                        color = PlaceholderGray,
                        fontSize = 20.sp
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
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
                    .height(60.dp)
            )

            Spacer(Modifier.height(24.dp))

            
            TextField(
                value = description,
                onValueChange = { description = it },
                placeholder = {
                    Text(
                        text = "Deskripsi (opsional)",
                        color = PlaceholderGray,
                        fontSize = 20.sp
                    )
                },
                shape = RoundedCornerShape(20.dp),
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
                    .heightIn(min = 60.dp)
            )

            Spacer(Modifier.weight(1f))

            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    color = if (canSubmit) AccentYellow else AccentYellow.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable(
                            enabled = canSubmit,
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            onDismissError()
                            onCreateGroup(
                                groupName.text.trim(),
                                description.text.trim()
                            )
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Create",
                            fontSize = 24.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.width(12.dp))
                        Icon(
                            imageVector = Icons.Default.Add,
                            tint = Color.Black,
                            contentDescription = "Add",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }

        
        if (errorMessage != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 100.dp)
            ) {
                Text(
                    text = errorMessage,
                    color = Color(0xFFFF7070),
                    fontSize = 16.sp,
                    modifier = Modifier
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            onDismissError()
                        }
                )
            }
        }
    }
}
