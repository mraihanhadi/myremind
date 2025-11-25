package com.example.myremind.ui.mapper

import com.example.myremind.model.Alarm
import com.example.myremind.ui.view.AlarmEntry
import com.example.myremind.ui.view.AlarmSmall
import com.example.myremind.ui.view.DayInfo
import java.util.Calendar

private fun Alarm.toTimeString(): Pair<String, String> {
    val cal = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour ?: 0)
        set(Calendar.MINUTE, minute ?: 0)
    }

    val h = cal.get(Calendar.HOUR_OF_DAY)
    val m = cal.get(Calendar.MINUTE)
    val ampm = if (h < 12) "AM" else "PM"
    val hour12 = if (h % 12 == 0) 12 else (h % 12)
    val time = String.format("%d:%02d", hour12, m)
    return time to ampm
}

private fun List<Boolean>.toDayInfoList(): List<DayInfo> {
    val letters = listOf("S","M","T","W","T","F","S")
    return letters.mapIndexed { idx, letter ->
        DayInfo(letter = letter, active = (getOrNull(idx) == true))
    }
}

fun Alarm.toAlarmEntry(): AlarmEntry {
    val (time, ampm) = toTimeString()
    return AlarmEntry(
        label = title,
        time = time,
        ampm = ampm,
        group = groupName ?: "Personal",
        days = repeatDays.toDayInfoList()
    )
}

fun Alarm.toAlarmSmall(): AlarmSmall {
    val (time, ampm) = toTimeString()
    return AlarmSmall(
        id = id,
        label = title,
        time = time,
        ampm = ampm,
        days = repeatDays.toDayInfoList(),
        enabled = enabled
    )
}
