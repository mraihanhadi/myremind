package com.example.myremind.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.myremind.util.AlarmScheduler
import com.example.myremind.util.LocalAlarmStore

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val store = LocalAlarmStore(context)
        val scheduler = AlarmScheduler(context)

        store.getAll()
            .filter { it.enabled }
            .forEach { scheduler.schedule(it) }
    }
}
