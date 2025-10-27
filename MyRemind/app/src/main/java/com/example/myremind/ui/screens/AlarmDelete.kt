package com.example.myremind.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.ui.draw.scale


// Reuse AlarmSmall, DayInfo dari AlarmScreen sebelumnya.
// Kalau file ini terpisah, pastikan kamu import / pindahkan data class yang sama.

@Composable
fun AlarmDeleteScreen(
    alarms: List<AlarmSmall>,
    onBack: () -> Unit,
    onClickHome: () -> Unit,
    onClickAlarm: () -> Unit,
    onClickAdd: () -> Unit,
    onClickGroup: () -> Unit,
    onClickProfile: () -> Unit,
    onDeleteSelected: (List<String>) -> Unit // kirim id alarm yang dipilih
) {
    // kita perlu id unik per alarm supaya bisa kita tandai
    // untuk contoh dummy, kita anggap index sebagai id string
    var selectedIds by remember { mutableStateOf(setOf<String>()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBlack)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 110.dp) // ruang untuk bottom bar
        ) {

            // HEADER: back arrow + "Alarm"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 32.dp, bottom = 24.dp),
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

            // GRID LIST 2 kolom
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(alarms.indices.toList()) { idx ->
                    val alarm = alarms[idx]
                    val id = idx.toString()
                    val isSelected = selectedIds.contains(id)

                    AlarmGridCardDeletable(
                        alarm = alarm,
                        selected = isSelected,
                        onToggleSelected = {
                            selectedIds =
                                if (isSelected) selectedIds - id
                                else selectedIds + id
                        }
                    )
                }
            }
        }

        // FAB + bottom bar tetap sama gaya
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

        // Tombol trash mengambang kanan bawah di atas bar
        if (selectedIds.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = 120.dp) // naik sedikit di atas bottom bar
            ) {
                DeleteButtonWithBadge(
                    count = selectedIds.size,
                    onClick = {
                        onDeleteSelected(selectedIds.toList())
                        // misal setelah delete kita kosongkan pilihan
                        selectedIds = emptySet()
                    }
                )
            }
        }
    }
}


// Card alarm untuk mode delete:
// - pojok kanan atas ada kotak cek (kuning kalau pilih, putih kalau tidak)
@Composable
fun AlarmGridCardDeletable(
    alarm: AlarmSmall,
    selected: Boolean,
    onToggleSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = CardDark,
        shape = RoundedCornerShape(28.dp),
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {

            // ---------- Badge select di pojok kanan atas ----------
            SelectBadge(
                selected = selected,
                onToggleSelected = onToggleSelected,
                modifier = Modifier.align(Alignment.TopEnd)
            )

            // ---------- Isi kartu ----------
            Column {
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

                // switch nyala / mati
                AlarmToggle(
                    checked = alarm.enabled,
                    onCheckedChange = { /* nanti update state alarm.enabled */ }
                )
            }
        }
    }
}


// Tombol delete merah dengan badge jumlah
@Composable
fun DeleteButtonWithBadge(
    count: Int,
    onClick: () -> Unit
) {
    // di figma tombolnya bulat putih dengan ikon trash merah + badge merah kecil
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(Color.White)
            .border(
                width = 2.dp,
                color = Color.White,
                shape = CircleShape
            )
            .clickable { onClick() }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        BadgedBox(
            badge = {
                Surface(
                    color = Color.Red,
                    shape = CircleShape
                ) {
                    Text(
                        text = count.toString(),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Selected",
                tint = Color.Red,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun SelectBadge(
    selected: Boolean,
    onToggleSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(32.dp)                // ukuran KONSISTEN untuk layout
            .clip(RoundedCornerShape(6.dp))
            .background(
                if (selected) AccentYellow else Color.White
            )
            .border(
                width = 2.dp,
                color = if (selected) AccentYellow else Color(0xFFBEBEBE),
                shape = RoundedCornerShape(6.dp)
            )
            .clickable { onToggleSelected() },
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Text(
                text = "✓",
                color = Color.Black,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

