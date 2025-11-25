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
import java.util.Calendar
import com.example.myremind.model.AddAlarmForm
import com.example.myremind.model.SelectableGroupOption
import com.example.myremind.model.Alarm

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAlarmScreen(
    alarm: Alarm,
    groupChoices: List<SelectableGroupOption>,
    onBack: () -> Unit,
    onSaveChanges: (AddAlarmForm) -> Unit
) {
    var title by remember { mutableStateOf(TextFieldValue(alarm.title)) }


    var days by remember { mutableStateOf(alarm.repeatDays.toMutableList()) }

    var dateMillis by remember { mutableStateOf(alarm.dateMillis) }
    var hour by remember { mutableStateOf(alarm.hour) }
    var minute by remember { mutableStateOf(alarm.minute) }

    var expanded by remember { mutableStateOf(false) }

    val initialSelectedTarget = remember {
        groupChoices.firstOrNull { choice ->
            if (alarm.ownerType == "personal") {
                choice.ownerType == "personal"
            } else {
                choice.ownerType == "group" && choice.groupId == alarm.groupId
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

    val cal = remember { Calendar.getInstance() }
    val dateFmt = remember { java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()) }

    val dateText = remember(dateMillis) {
        dateMillis?.let { dateFmt.format(it) } ?: "dd/mm/yyyy"
    }
    val timeText = remember(hour, minute) {
        if (hour != null && minute != null)
            String.format(java.util.Locale.getDefault(), "%02d:%02d", hour, minute)
        else "00:00"
    }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 12.dp)
                    .fillMaxWidth()
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextWhite)
                }
                Text(
                    text = "Edit Alarm",
                    color = TextWhite,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(CardDark)
                    .padding(20.dp)
            ) {
                AppInputPill(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = "JUDUL..."
                )

                Spacer(Modifier.height(16.dp))

                DaysSelector(
                    days = days,
                    onToggle = { idx ->
                        days = days.toMutableList().also { it[idx] = !it[idx] }
                    }
                )

                Spacer(Modifier.height(10.dp))

                GroupPickerField(
                    selectedLabel = selectedTarget.label,
                    onClick = { expanded = !expanded }
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    groupChoices.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.label, color = Color.Black, fontSize = 16.sp) },
                            onClick = {
                                selectedTarget = option
                                expanded = false
                            }
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                PillButtonField(
                    text = dateText,
                    trailing = {
                        Icon(Icons.Default.CalendarMonth, contentDescription = "Pick date", tint = Color.Black)
                    },
                    onClick = { showDatePicker = true }
                )

                Spacer(Modifier.height(10.dp))

                PillButtonField(
                    text = timeText,
                    trailing = {
                        Icon(Icons.Default.AccessTime, contentDescription = "Pick time", tint = Color.Black)
                    },
                    onClick = { showTimePicker = true }
                )

                Spacer(Modifier.height(20.dp))

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

        if (showDatePicker) {
            val dateState = rememberDatePickerState(initialSelectedDateMillis = dateMillis)
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        dateMillis = dateState.selectedDateMillis
                        showDatePicker = false
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
                }
            ) {
                DatePicker(state = dateState)
            }
        }

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
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        TimePicker(state = timeState)
                    }
                }
            )
        }
    }
}
