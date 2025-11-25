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
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.remember

@Composable
fun GroupAddMemberScreen(
    groupName: String,
    loading: Boolean,
    errorMessage: String?,
    onDismissError: () -> Unit,
    onBack: () -> Unit,
    onAddMember: (email: String) -> Unit
) {
    var email by remember { mutableStateOf(TextFieldValue("")) }
    val canAdd = email.text.isNotBlank() && !loading

    var query by remember { mutableStateOf(TextFieldValue("")) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 32.dp, bottom = 32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = TextWhite,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(Modifier.width(8.dp))

                Text(
                    text = "Group",
                    color = TextWhite,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 44.sp
                )
            }
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = Color.White,
                shape = RoundedCornerShape(4.dp)
            ) {
                TextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = {
                        Text(
                            text = "Search user",
                            color = Color(0xFF8C8C8C),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Normal
                        )
                    },
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = Color.Black,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
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

            
            Spacer(modifier = Modifier.weight(1f))

            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 48.dp),
                contentAlignment = Alignment.Center
            ) {
                AddMemberButton(
                    enabled = query.text.isNotBlank(),
                    onClick = {
                        onAddMember(query.text.trim())
                        
                        query = TextFieldValue("")
                    }
                )
            }
        }
    }
}

@Composable
fun AddMemberButton(
    enabled: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (enabled) AccentYellow else AccentYellow.copy(alpha = 0.4f)
    val textColor = Color.Black

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(28.dp))
            .clickableNoRipple(
                enabled = enabled,
                onClick = onClick
            )
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 32.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Add",
                color = textColor,
                fontSize = 28.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 32.sp
            )

            Spacer(Modifier.width(12.dp))

            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add member",
                tint = textColor,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}


@Composable
private fun Modifier.clickableNoRipple(
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier = composed {
    val interaction = remember { MutableInteractionSource() }
    this.then(
        Modifier.clickable(
            enabled = enabled,
            indication = null,
            interactionSource = interaction
        ) { onClick() }
    )
}