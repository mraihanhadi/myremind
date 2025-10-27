package com.example.myremind.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color

// ---------- DATA UNTUK ALARM SCREEN GRID ----------
data class AlarmSmall(
    val label: String,        // "Work"
    val time: String,         // "8:30"
    val ampm: String,         // "AM"
    val days: List<DayInfo>,  // pakai DayInfo yg sama dgn Home
    val enabled: Boolean
)

// ---------- ALARM SCREEN ----------
@Composable
fun AlarmScreen(
    alarms: List<AlarmSmall>,
    onClickHome: () -> Unit,
    onClickAlarm: () -> Unit,
    onClickAdd: () -> Unit,
    onClickGroup: () -> Unit,
    onClickProfile: () -> Unit,
    onClickDelete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBlack)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 110.dp)
        ) {
            // Header "Alarm" + icon edit
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Alarm",
                    color = TextWhite,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 44.sp
                )

                IconButton(
                    onClick = onClickDelete,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit alarms",
                        tint = TextWhite,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            // Grid 2 kolom
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(alarms) { alarm ->
                    AlarmGridCard(alarm)
                }
            }
        }

        // Bottom bar dengan tab Alarm aktif
        BottomBarWithFabSelectable(
            modifier = Modifier
                .align(Alignment.BottomCenter),
            selectedTab = BottomTab.ALARM,
            onClickHome = onClickHome,
            onClickAlarm = onClickAlarm,
            onClickAdd = onClickAdd,
            onClickGroup = onClickGroup,
            onClickProfile = onClickProfile
        )
    }
}

// ---------- KARTU ALARM KECIL (kotak grid) ----------
@Composable
fun AlarmGridCard(
    alarm: AlarmSmall,
    modifier: Modifier = Modifier
) {
    Surface(
        color = CardDark,
        shape = RoundedCornerShape(28.dp),
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            // "Work"
            Text(
                text = alarm.label,
                color = TextSoft,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(16.dp))

            // jam besar + AM
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = alarm.time,
                    color = TextWhite,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 40.sp
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = alarm.ampm,
                    color = TextWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 20.sp
                )
            }

            Spacer(Modifier.height(16.dp))

            // hari
            DaysRowSmall(days = alarm.days)

            Spacer(Modifier.height(16.dp))

            // switch on/off (kuning kalau aktif)
            AlarmToggle(
                checked = alarm.enabled,
                onCheckedChange = { /* nanti hubungkan state */ }
            )
        }
    }
}

// ---------- ROW HARI VERSI KARTU KECIL ----------
@Composable
fun DaysRowSmall(days: List<DayInfo>) {
    Column {
        // titik di atas
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            days.forEach { day ->
                Box(
                    modifier = Modifier.width(18.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(
                                if (day.active) AccentYellow else Color.Transparent
                            )
                    )
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
                    modifier = Modifier.width(18.dp),
                    lineHeight = 20.sp
                )
            }
        }
    }
}

// ---------- SWITCH STYLING ----------
@Composable
fun AlarmToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = AccentYellow,
            uncheckedThumbColor = Color.White,
            uncheckedTrackColor = Color.White
        )
    )
}
