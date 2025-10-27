package com.example.myremind.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
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

// fallback warna kalau belum keimport dari global
// hapus ini kalau kamu sudah import warna global yang sama dari file lain
private val ScreenBlackFallback = Color(0xFF000000)
private val AccentYellowFallback = Color(0xFFE8F245)
private val TextWhiteFallback = Color(0xFFFFFFFF)

// Gunakan warna global project kamu kalau sudah ada
private val ScreenBgColor @Composable get() = try { ScreenBlack } catch (_: Throwable) { ScreenBlackFallback }
private val AccentColor   @Composable get() = try { AccentYellow } catch (_: Throwable) { AccentYellowFallback }
private val TextWhiteColor@Composable get() = try { TextWhite } catch (_: Throwable) { TextWhiteFallback }

@Composable
fun GroupCreateScreen(
    onBack: () -> Unit,
    onCreateGroup: (String) -> Unit
) {
    // <-- STATE LOKAL DI SCREEN
    var groupName by remember { mutableStateOf(TextFieldValue("")) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBgColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {

            // Header: "< Group"
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
                        tint = TextWhiteColor,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(Modifier.width(8.dp))

                Text(
                    text = "Group",
                    color = TextWhiteColor,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 44.sp
                )
            }

            Spacer(Modifier.height(24.dp))

            // Input pill besar: "Nama Group"
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(28.dp)),
                color = Color.White,
                shape = RoundedCornerShape(28.dp)
            ) {
                TextField(
                    value = groupName,
                    onValueChange = { groupName = it },
                    placeholder = {
                        Text(
                            text = "Nama Group",
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
                        .padding(horizontal = 24.dp, vertical = 20.dp),
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

            // Tombol "Create +"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 48.dp),
                contentAlignment = Alignment.Center
            ) {
                CreateGroupButton(
                    enabled = groupName.text.isNotBlank(),
                    onClick = {
                        onCreateGroup(groupName.text.trim())
                        // opsional: reset setelah create
                        groupName = TextFieldValue("")
                    }
                )
            }
        }
    }
}

// Tombol kuning kapsul "Create  +"
@Composable
fun CreateGroupButton(
    enabled: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (enabled) AccentColor else AccentColor.copy(alpha = 0.4f)
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
                text = "Create",
                color = textColor,
                fontSize = 28.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 32.sp
            )

            Spacer(Modifier.width(12.dp))

            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Create group",
                tint = textColor,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

// helper: no ripple
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
