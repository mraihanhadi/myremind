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
import com.example.myremind.model.AddAlarmForm
import com.example.myremind.model.SelectableGroupOption
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAlarmScreen(
    groupChoices: List<SelectableGroupOption>,
    onBack: () -> Unit,
    onSave: (AddAlarmForm) -> Unit,
) {
    var title by remember { mutableStateOf(TextFieldValue("")) }
    var desc by remember { mutableStateOf(TextFieldValue("")) }
    var days by remember { mutableStateOf(List(7) { false }) }

    val cal = remember { Calendar.getInstance() }

    var hour by remember { mutableStateOf<Int?>(null) }
    var minute by remember { mutableStateOf<Int?>(null) }

    var expanded by remember { mutableStateOf(false) }
    var selectedTarget by remember {
        mutableStateOf(
            groupChoices.firstOrNull() ?: SelectableGroupOption(
                label = "Personal",
                ownerType = "personal",
                groupId = null,
                groupName = null
            )
        )
    }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val dateFmt = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val timeText = remember(hour, minute) {
        if (hour != null && minute != null) {
            String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
        } else {
            "00:00"
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
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {

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

                Spacer(Modifier.height(16.dp))

                DaysSelector(
                    days = days,
                    onToggle = { idx ->
                        days = days.toMutableList().also { list ->
                            list[idx] = !list[idx]
                        }
                    }
                )

                Spacer(Modifier.height(10.dp))

                GroupPickerField(
                    selectedLabel = selectedTarget.label,
                    onClick = { expanded = true }
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

                Spacer(Modifier.height(10.dp))

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

                Button(
                    onClick = {
                        onSave(
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
                    Text("Save", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Default.Check, contentDescription = "Save")
                }
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
                    TextButton(
                        onClick = {
                            hour = timeState.hour
                            minute = timeState.minute
                            showTimePicker = false
                        }
                    ) { Text("OK") }
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
fun AppInputPill(
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
fun PillButtonField(
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

@Composable
fun DaysSelector(
    days: List<Boolean>,
    onToggle: (Int) -> Unit
) {
    val labels = listOf("S","M","T","W","T","F","S")

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
fun GroupPickerField(
    selectedLabel: String,
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
            text = selectedLabel,
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
