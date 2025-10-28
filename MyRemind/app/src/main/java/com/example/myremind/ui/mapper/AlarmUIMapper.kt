// file: ui/mapper/AlarmUiMapper.kt
package com.example.myremind.ui.mapper

import com.example.myremind.model.Alarm
import com.example.myremind.ui.view.AlarmEntry
import com.example.myremind.ui.view.AlarmSmall
import com.example.myremind.ui.view.DayInfo
import java.util.Calendar

private fun Calendar.toTimeString(): Pair<String,String> {
    val h = get(Calendar.HOUR_OF_DAY)
    val m = get(Calendar.MINUTE)
    val ampm = if (h < 12) "AM" else "PM"
    val hour12 = if (h % 12 == 0) 12 else (h % 12)
    val time = String.format("%d:%02d", hour12, m)
    return time to ampm
}

private fun List<Boolean>.toDayInfoList(): List<DayInfo> {
    val letters = listOf("S","M","T","W","T","F","S")
    return letters.mapIndexed { idx, letter ->
        DayInfo(letter = letter, active = (this.getOrNull(idx) == true))
    }
}

// buat Home card besar
fun Alarm.toAlarmEntry(): AlarmEntry {
    val (time, ampm) = getTimeCal().toTimeString()
    return AlarmEntry(
        label = getAlarmName(),
        time = time,
        ampm = ampm,
        group = getGroupName() ?: "Personal",
        days = getRepeatDays().toDayInfoList()
    )
}

// buat Alarm grid kecil
fun Alarm.toAlarmSmall(): AlarmSmall {
    val (time, ampm) = getTimeCal().toTimeString()
    return AlarmSmall(
        id = getAlarmId(), // <--- penting buat edit
        label = getAlarmName(),
        time = time,
        ampm = ampm,
        days = getRepeatDays().toDayInfoList(),
        enabled = true // sementara semua true, nanti bisa dari modelmu
    )
}
