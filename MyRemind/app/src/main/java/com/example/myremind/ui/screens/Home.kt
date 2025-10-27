package com.example.myremind.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color

// ---------- DATA UNTUK HOME ----------
data class DayInfo(
    val letter: String,   // "S", "M", ...
    val active: Boolean   // untuk titik kuning
)

data class AlarmEntry(
    val label: String,        // "Work"
    val time: String,         // "8:30"
    val ampm: String,         // "AM"
    val group: String,        // "GRUP 1"
    val days: List<DayInfo>   // list hari
)

// ---------- HOME SCREEN ----------
@Composable
fun HomeScreen(
    username: String,
    alarms: List<AlarmEntry>,
    onClickHome: () -> Unit,
    onClickAlarm: () -> Unit,
    onClickAdd: () -> Unit,
    onClickGroup: () -> Unit,
    onClickProfile: () -> Unit,
    onClickBell: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBlack)
    ) {
        // Konten scroll
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 110.dp) // ruang bottom bar
        ) {

            // Header "Hello, User" + bell
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 24.dp,
                            end = 24.dp,
                            top = 32.dp,
                            bottom = 24.dp
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Hello,",
                            color = TextWhite,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Light,
                            lineHeight = 36.sp
                        )
                        Text(
                            text = username,
                            color = TextWhite,
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 44.sp
                        )
                    }

                    IconButton(
                        onClick = onClickBell,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = TextWhite,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }

            // Daftar kartu besar alarm (full width)
            items(alarms) { alarm ->
                AlarmBigCard(
                    alarm = alarm,
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 24.dp)
                )
            }
        }

        // Bottom bar
        BottomBarWithFabSelectable(
            modifier = Modifier
                .align(Alignment.BottomCenter),
            selectedTab = BottomTab.HOME,
            onClickHome = onClickHome,
            onClickAlarm = onClickAlarm,
            onClickAdd = onClickAdd,
            onClickGroup = onClickGroup,
            onClickProfile = onClickProfile
        )
    }
}

// ---------- KARTU ALARM BESAR (sesuai HOME figma) ----------
@Composable
fun AlarmBigCard(
    alarm: AlarmEntry,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = CardDark,
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            // Baris "Work"
            Text(
                text = alarm.label,
                color = TextSoft,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(16.dp))

            // Baris waktu besar + grup di kanan
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = alarm.time,
                        color = TextWhite,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 48.sp
                    )

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = alarm.ampm,
                        color = TextWhite,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 24.sp
                    )
                }

                Text(
                    text = alarm.group,
                    color = TextWhite,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Light,
                    lineHeight = 36.sp
                )
            }

            Spacer(Modifier.height(24.dp))

            // Baris hari dengan titik kuning
            DaysRowBig(alarm.days)
        }
    }
}

// ---------- ROW HARI (versi besar, seperti di HOME) ----------
@Composable
fun DaysRowBig(days: List<DayInfo>) {
    Column {
        // titik kuning
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            days.forEach { day ->
                Box(
                    modifier = Modifier.width(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (day.active) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(RoundedCornerShape(50))
                                .background(AccentYellow)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // huruf hari
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            days.forEach { day ->
                Text(
                    text = day.letter,
                    color = AccentYellow,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.width(20.dp),
                    lineHeight = 20.sp
                )
            }
        }
    }
}
