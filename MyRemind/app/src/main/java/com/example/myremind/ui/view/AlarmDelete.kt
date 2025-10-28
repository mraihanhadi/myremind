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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Screen delete mode.
 *
 * - alarms: list alarm yg keliatan user (AlarmSmall harus punya `id: Int`)
 * - onDeleteSelected: dipanggil dengan daftar ID yg dipilih
 */
@Composable
fun AlarmDeleteScreen(
    alarms: List<AlarmSmall>,
    onBack: () -> Unit,
    onClickHome: () -> Unit,
    onClickAlarm: () -> Unit,
    onClickAdd: () -> Unit,
    onClickGroup: () -> Unit,
    onClickProfile: () -> Unit,
    onDeleteSelected: (List<Int>) -> Unit
) {
    // set ID alarm terpilih
    var selectedIds by remember { mutableStateOf(setOf<Int>()) }

    fun toggleSelect(id: Int) {
        selectedIds = if (selectedIds.contains(id)) {
            selectedIds - id
        } else {
            selectedIds + id
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBlack)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 110.dp) // ruang bottom bar
        ) {
            // HEADER: "<  Alarm"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 24.dp,
                        end = 24.dp,
                        top = 32.dp,
                        bottom = 24.dp
                    ),
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
                    text = "Alarm",
                    color = TextWhite,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 44.sp
                )
            }

            // GRID 2 kolom, selectable
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(
                    alarms,
                    key = { it.id }
                ) { alarm ->
                    SelectableAlarmGridCard(
                        alarm = alarm,
                        isSelected = selectedIds.contains(alarm.id),
                        onToggleSelect = { toggleSelect(alarm.id) }
                    )
                }
            }
        }

        // ======== FAB DELETE KECIL DI KANAN BAWAH ========
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 98.dp) // posisinya sama kaya desain kamu
        ) {
            DeleteFabWithBadge(
                count = selectedIds.size,
                onClick = {
                    if (selectedIds.isNotEmpty()) {
                        onDeleteSelected(selectedIds.toList())
                    }
                }
            )
        }

        // ======== BOTTOM BAR (tab Alarm aktif) ========
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

/**
 * Tombol delete bulat putih + badge merah kecil angka jumlah selected.
 * Mirip screenshot kamu:
 *  - lingkaran putih
 *  - icon trash merah
 *  - badge merah kecil di pojok kanan atas
 */
@Composable
private fun DeleteFabWithBadge(
    count: Int,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(Color.White)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // icon trash
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete selected",
                tint = Color(0xFFFF2B2B),
                modifier = Modifier.size(28.dp)
            )
        }

        // badge merah di pojok kanan atas
        if (count > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 4.dp, y = (-4).dp) // sedikit keluar lingkaran biar sama kaya mock
                    .clip(CircleShape)
                    .background(Color(0xFFFF2B2B))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = count.toString(),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Card alarm versi mode delete:
 * - sama bentuknya dgn AlarmGridCard
 * - ada badge kanan atas:
 *    - kuning + ✓ kalau selected
 *    - putih kosong kalau tidak
 * - toggleSelect dipanggil saat card di-tap
 */
@Composable
private fun SelectableAlarmGridCard(
    alarm: AlarmSmall,
    isSelected: Boolean,
    onToggleSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = CardDark,
        shape = RoundedCornerShape(28.dp),
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { onToggleSelect() }
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            // badge pojok kanan atas
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(28.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        if (isSelected) AccentYellow else Color.White
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Text(
                        text = "✓",
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column {
                // label
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

                // switch visual aja (non-interaktif di delete mode)
                DisabledAlarmToggleDisplay(checked = alarm.enabled)
            }
        }
    }
}

@Composable
private fun DisabledAlarmToggleDisplay(
    checked: Boolean
) {
    val trackColor = if (checked) AccentYellow else Color.White
    val thumbColor = Color.White

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(28.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(trackColor),
            contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(thumbColor)
            )
        }
    }
}
