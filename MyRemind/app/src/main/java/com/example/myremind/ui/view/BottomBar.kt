package com.example.myremind.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- warna global (pakai sama di semua screen)
val ScreenBlack = Color(0xFF000000)
val CardDark = Color(0xFF333445)
val TextWhite = Color(0xFFFFFFFF)
val TextSoft = Color(0xFFDBDBDB)
val AccentYellow = Color(0xFFE8F245)

// warna khusus bottom bar
private val BottomBarBg = CardDark

// Tab yang aktif
enum class BottomTab { HOME, ALARM, GROUP, PROFILE }

@Composable
fun BottomBarWithFabSelectable(
    modifier: Modifier = Modifier,
    selectedTab: BottomTab,
    onClickHome: () -> Unit,
    onClickAlarm: () -> Unit,
    onClickAdd: () -> Unit,
    onClickGroup: () -> Unit,
    onClickProfile: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(110.dp)
    ) {
        // Area bar bawah
        Surface(
            color = BottomBarBg,
            shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .align(Alignment.BottomCenter)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                BottomNavItemSelectable(
                    icon = Icons.Default.Home,
                    label = "Home",
                    active = (selectedTab == BottomTab.HOME),
                    onClick = onClickHome
                )

                BottomNavItemSelectable(
                    icon = Icons.Default.AccessTime,
                    label = "Alarm",
                    active = (selectedTab == BottomTab.ALARM),
                    onClick = onClickAlarm
                )

                Spacer(modifier = Modifier.width(64.dp)) // ruang FAB tengah

                BottomNavItemSelectable(
                    icon = Icons.Default.Group,
                    label = "Group",
                    active = (selectedTab == BottomTab.GROUP),
                    onClick = onClickGroup
                )

                BottomNavItemSelectable(
                    icon = Icons.Default.Person,
                    label = "Profile",
                    active = (selectedTab == BottomTab.PROFILE),
                    onClick = onClickProfile
                )
            }
        }

        // FAB bulat kuning mengambang di tengah
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(AccentYellow),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = onClickAdd) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        tint = Color.Black,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavItemSelectable(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    active: Boolean,
    onClick: () -> Unit
) {
    val tintColor = if (active) AccentYellow else TextWhite

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = icon,
                tint = tintColor,
                contentDescription = label,
                modifier = Modifier.size(32.dp)
            )
        }
        Text(
            text = label,
            color = tintColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal
        )
    }
}
