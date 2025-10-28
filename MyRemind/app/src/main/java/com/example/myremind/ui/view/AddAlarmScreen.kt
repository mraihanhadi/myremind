package com.example.myremind.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// ---------- DATA YANG DISAVE ----------
data class AddAlarmForm(
    val title: String,
    val description: String,
    val days: List<Boolean>,     // urutan: S M T W T F S
    val dateMillis: Long?,       // nullable kalau user tidak pilih
    val hour: Int?,              // nullable kalau user tidak pilih
    val minute: Int?
)

// ---------- SCREEN ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAlarmScreen(
    onBack: () -> Unit,
    onSave: (AddAlarmForm) -> Unit
) {
    // --- STATE FORM ---
    var title by remember { mutableStateOf(TextFieldValue("")) }
    var desc by remember { mutableStateOf(TextFieldValue("")) }
    var days by remember { mutableStateOf(List(7) { false }) } // S M T W T F S
    val cal = remember { Calendar.getInstance() }

    var dateMillis by remember { mutableStateOf<Long?>(null) }
    var hour by remember { mutableStateOf<Int?>(null) }
    var minute by remember { mutableStateOf<Int?>(null) }

    // picker dialog flags
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val dateFmt = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val dateText = remember(dateMillis) {
        dateMillis?.let { dateFmt.format(it) } ?: "dd/mm/yyyy"
    }
    val timeText = remember(hour, minute) {
        if (hour != null && minute != null) {
            String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
        } else "00:00"
    }

    // Warna global: re-use dari BottomBar.kt (pastikan file itu ada)
    // ScreenBlack, CardDark, TextWhite, TextSoft, AccentYellow

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // HEADER: Back ←  Alarm
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 12.dp)
                    .fillMaxWidth()
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = TextWhite
                    )
                }
                Text(
                    text = "Alarm",
                    color = TextWhite,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            // CARD FORM
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(CardDark)
                    .padding(20.dp)
            ) {

                // FIELD: Judul (pill putih)
                AppInputPill(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = "JUDUL..."
                )

                Spacer(Modifier.height(16.dp))

                // FIELD: Deskripsi (area multiline pill)
                AppInputPill(
                    value = desc,
                    onValueChange = { desc = it },
                    placeholder = "Deskripsi....",
                    minLines = 3,
                    maxLines = 6
                )

                Spacer(Modifier.height(16.dp))

                // Label hari kecil S M T W T F S
                DaysSelector(
                    days = days,
                    onToggle = { idx ->
                        days = days.toMutableList().also { it[idx] = !it[idx] }
                    }
                )

                Spacer(Modifier.height(10.dp))

                // FIELD: Tanggal (dd/mm/yyyy + icon kalender)
                PillButtonField(
                    text = dateText,
                    leading = null,
                    trailing = {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Pick date",
                            tint = Color.Black
                        )
                    },
                    onClick = { showDatePicker = true }
                )

                Spacer(Modifier.height(10.dp))

                // FIELD: Waktu (00:00 + icon jam)
                PillButtonField(
                    text = timeText,
                    leading = null,
                    trailing = {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = "Pick time",
                            tint = Color.Black
                        )
                    },
                    onClick = { showTimePicker = true }
                )

                Spacer(Modifier.height(20.dp))

                // SAVE BUTTON
                Button(
                    onClick = {
                        onSave(
                            AddAlarmForm(
                                title = title.text.trim(),
                                description = desc.text.trim(),
                                days = days,
                                dateMillis = dateMillis,
                                hour = hour,
                                minute = minute
                            )
                        )
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .height(52.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentYellow,
                        contentColor = Color.Black
                    ),
                    contentPadding = PaddingValues(horizontal = 28.dp)
                ) {
                    Text("Save", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Default.Check, contentDescription = "Save")
                }
            }
        }

        // ----------------- DATE PICKER DIALOG -----------------
        if (showDatePicker) {
            val dateState = rememberDatePickerState(
                initialSelectedDateMillis = dateMillis
            )
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            dateMillis = dateState.selectedDateMillis
                            showDatePicker = false
                        }
                    ) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
                }
            ) {
                DatePicker(state = dateState)
            }
        }

        // ----------------- TIME PICKER DIALOG -----------------
        if (showTimePicker) {
            val timeState = rememberTimePickerState(
                initialHour = hour ?: cal.get(Calendar.HOUR_OF_DAY),
                initialMinute = minute ?: cal.get(Calendar.MINUTE),
                is24Hour = true
            )
            AlertDialog(
                onDismissRequest = { showTimePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        hour = timeState.hour
                        minute = timeState.minute
                        showTimePicker = false
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
                },
                text = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        TimePicker(state = timeState)
                    }
                }
            )
        }
    }
}

/* ======================
 *  SMALL REUSABLE UI
 * ====================== */

// TextField pill putih (single/multi-line) sesuai mockup
@Composable
private fun AppInputPill(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    placeholder: String,
    minLines: Int = 1,
    maxLines: Int = 1
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(placeholder, color = Color(0xFF9E9E9E))
        },
        minLines = minLines,
        maxLines = maxLines,
        singleLine = maxLines == 1,
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White,
            cursorColor = Color.Black,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            disabledContainerColor = Color.White
        ),
        textStyle = LocalTextStyle.current.copy(color = Color.Black, fontSize = 16.sp),
        modifier = Modifier
            .fillMaxWidth()
    )
}

// Field tombol kapsul putih untuk Date/Time
@Composable
private fun PillButtonField(
    text: String,
    leading: (@Composable (() -> Unit))? = null,
    trailing: (@Composable (() -> Unit))? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (leading != null) {
                leading()
                Spacer(Modifier.width(8.dp))
            }
            Text(
                text = text,
                color = Color(0xFF808080),
                fontSize = 16.sp
            )
        }
        if (trailing != null) {
            Spacer(Modifier.width(12.dp))
            trailing()
        }
    }
}

// Selector hari kecil (S M T W T F S) + warna kuning kecil di label seperti mockup
@Composable
private fun DaysSelector(
    days: List<Boolean>,
    onToggle: (Int) -> Unit
) {
    val labels = listOf("S","M","T","W","T","F","S")
    // Baris label kecil di atas (warna kuning lembut)
    Row(
        modifier = Modifier.padding(start = 4.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        labels.forEachIndexed { idx, s ->
            Text(
                text = s,
                color = AccentYellow,
                fontSize = 12.sp,
                modifier = Modifier
                    .width(20.dp),
                textAlign = TextAlign.Start
            )
        }
    }
    // Baris chip toggle bulat
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        labels.forEachIndexed { idx, s ->
            val selected = days[idx]
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (selected) AccentYellow.copy(alpha = 0.18f) else Color.White)
                    .clickable { onToggle(idx) }
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    text = s,
                    color = if (selected) AccentYellow else Color(0xFF444444),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}