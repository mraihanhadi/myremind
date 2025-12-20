package com.example.myremind.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color

data class AlarmSmall(
    val id: String,
    val label: String,
    val time: String,
    val ampm: String,
    val days: List<DayInfo>,
    val enabled: Boolean
)

@Composable
fun AlarmScreen(
    alarms: List<AlarmSmall>,
    onClickHome: () -> Unit,
    onClickAlarm: () -> Unit,
    onClickAdd: () -> Unit,
    onClickGroup: () -> Unit,
    onClickProfile: () -> Unit,
    onClickDelete: () -> Unit,
    onToggleAlarm: (String, Boolean) -> Unit,
    onAlarmClick: (String) -> Unit
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
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFFFFFFFF),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(alarms, key = { it.id }) { alarm ->
                    AlarmGridCard(
                        alarm = alarm,
                        onToggle = { enabled -> onToggleAlarm(alarm.id, enabled) },
                        onClick = { onAlarmClick(alarm.id) }
                    )
                }
            }
        }

        BottomBarWithFabSelectable(
            modifier = Modifier.align(Alignment.BottomCenter),
            selectedTab = BottomTab.ALARM,
            onClickHome = onClickHome,
            onClickAlarm = onClickAlarm,
            onClickAdd = onClickAdd,
            onClickGroup = onClickGroup,
            onClickProfile = onClickProfile
        )
    }
}

@Composable
fun AlarmGridCard(
    alarm: AlarmSmall,
    onToggle: (Boolean) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = CardDark,
        shape = RoundedCornerShape(28.dp),
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Text(
                text = alarm.label,
                color = TextSoft,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.Bottom) {
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

            DaysRowSmall(days = alarm.days)

            Spacer(Modifier.height(16.dp))

            AlarmToggle(
                checked = alarm.enabled,
                onCheckedChange = onToggle
            )
        }
    }
}

@Composable
fun DaysRowSmall(days: List<DayInfo>) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                            .background(if (day.active) AccentYellow else Color.Transparent)
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            days.forEach { day ->
                Text(
                    text = day.letter,
                    color = AccentYellow,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.width(9.dp),
                    lineHeight = 20.sp
                )
            }
        }
    }
}

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
