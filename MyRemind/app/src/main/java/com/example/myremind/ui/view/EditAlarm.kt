package com.example.myremind.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.example.myremind.model.AddAlarmForm
import com.example.myremind.model.SelectableGroupOption
import com.example.myremind.model.EditableAlarmData


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAlarmScreen(
    alarm: EditableAlarmData,
    groupChoices: List<SelectableGroupOption>,
    onBack: () -> Unit,
    onSaveChanges: (AddAlarmForm) -> Unit
) {
    // mirip AddAlarmScreen tapi state awal = alarm.* dan label tombol beda

    // title
    var title by remember { mutableStateOf(TextFieldValue(alarm.title)) }

    // days (copy agar bisa toggle)
    var days by remember { mutableStateOf(alarm.repeatDays.toMutableList()) }

    // date/hours
    var dateMillis by remember { mutableStateOf(alarm.dateMillis) }
    var hour by remember { mutableStateOf(alarm.hour) }
    var minute by remember { mutableStateOf(alarm.minute) }

    // dropdown group
    var expanded by remember { mutableStateOf(false) }

    // kita pilih opsi awal berdasarkan alarm.ownerType/groupId
    val initialSelectedTarget = remember {
        // coba cari kecocokan di groupChoices
        groupChoices.firstOrNull { choice ->
            if (alarm.ownerType == "personal") {
                choice.ownerType == "personal"
            } else {
                choice.ownerType == "group" &&
                        choice.groupId == alarm.groupId
            }
        } ?: SelectableGroupOption(
            label = if (alarm.ownerType == "personal") "Personal"
            else (alarm.groupName ?: "Unknown Group"),
            ownerType = alarm.ownerType,
            groupId = alarm.groupId,
            groupName = alarm.groupName
        )
    }

    var selectedTarget by remember { mutableStateOf(initialSelectedTarget) }

    // date/time text same logic as AddAlarmScreen
    val cal = remember { java.util.Calendar.getInstance() }

    val dateFmt = remember { java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()) }
    val dateText = remember(dateMillis) {
        dateMillis?.let { dateFmt.format(it) } ?: "dd/mm/yyyy"
    }
    val timeText = remember(hour, minute) {
        if (hour != null && minute != null) {
            String.format(java.util.Locale.getDefault(), "%02d:%02d", hour, minute)
        } else "00:00"
    }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    // UI layout sangat mirip AddAlarmScreen
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
            // HEADER
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
                    text = "Edit Alarm",
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
                // TITLE
                AppInputPill(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = "JUDUL..."
                )

                Spacer(Modifier.height(16.dp))

                // Repeat days
                DaysSelector(
                    days = days,
                    onToggle = { idx ->
                        days = days.toMutableList().also { it[idx] = !it[idx] }
                    }
                )

                Spacer(Modifier.height(10.dp))

                // GROUP PICKER
                GroupPickerField(
                    selected = selectedTarget.label,
                    onClick = { expanded = !expanded }
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    groupChoices.forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = option.label,
                                    color = Color.Black,
                                    fontSize = 16.sp
                                )
                            },
                            onClick = {
                                selectedTarget = option
                                expanded = false
                            }
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                // DATE PICKER
                PillButtonField(
                    text = dateText,
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

                // TIME PICKER
                PillButtonField(
                    text = timeText,
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

                // SAVE CHANGES BUTTON
                Button(
                    onClick = {
                        onSaveChanges(
                            AddAlarmForm(
                                title = title.text.trim(),
                                days = days,
                                hour = hour,
                                minute = minute,
                                selectedTarget = selectedTarget
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
                    Text("Save Changes", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Default.Check, contentDescription = "Save Changes")
                }
            }
        }

        // date picker dialog
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

        // time picker dialog
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
            Text(
                placeholder,
                color = Color(0xFF9E9E9E)
            )
        },
        minLines = minLines,
        maxLines = maxLines,
        singleLine = maxLines == 1,
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            cursorColor = Color.Black,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
        ),
        textStyle = LocalTextStyle.current.copy(
            color = Color.Black,
            fontSize = 16.sp
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun PillButtonField(
    text: String,
    trailing: (@Composable () -> Unit)? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            color = Color(0xFF808080),
            fontSize = 16.sp
        )

        if (trailing != null) {
            Spacer(Modifier.width(12.dp))
            trailing()
        }
    }
}

// Baris hari: label "S M T W T F S" + chip toggle
@Composable
private fun DaysSelector(
    days: List<Boolean>,
    onToggle: (Int) -> Unit
) {
    val labels = listOf("S","M","T","W","T","F","S")

    // baris label kecil kuning
    Row(
        modifier = Modifier.padding(start = 4.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        labels.forEach { label ->
            Text(
                text = label,
                color = AccentYellow,
                fontSize = 12.sp,
                modifier = Modifier.width(20.dp),
                textAlign = TextAlign.Start
            )
        }
    }

    // baris chip toggle
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        labels.forEachIndexed { idx, s ->
            val selected = days[idx]
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (selected) AccentYellow.copy(alpha = 0.18f)
                        else Color.White
                    )
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        onToggle(idx)
                    }
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

@Composable
private fun GroupPickerField(
    selected: String,
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
        Text(
            text = selected,
            color = Color(0xFF444444),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "▼",
            color = Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

