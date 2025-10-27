package com.example.myremind.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myremind.ui.screens.AlarmEntry
import com.example.myremind.ui.screens.AlarmScreen
import com.example.myremind.ui.screens.AlarmSmall
import com.example.myremind.ui.screens.BottomTab
import com.example.myremind.ui.screens.DayInfo
import com.example.myremind.ui.screens.HomeScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    // ------ Dummy data untuk ditampilkan di screen ------
    val days = listOf(
        DayInfo("S", true),
        DayInfo("M", true),
        DayInfo("T", true),
        DayInfo("W", true),
        DayInfo("T", true),
        DayInfo("F", true),
        DayInfo("S", true),
    )

    val homeAlarms = List(4) {
        AlarmEntry(
            label = "Work",
            time = "8:30",
            ampm = "AM",
            group = "GRUP 1",
            days = days
        )
    }

    val alarmTiles = List(6) { i ->
        AlarmSmall(
            label = "Work",
            time = "8:30",
            ampm = "AM",
            days = days,
            enabled = (i % 2 == 1)
        )
    }

    // ------ NavHost utama ------
    NavHost(
        navController = navController,
        startDestination = NavRoute.HOME
    ) {

        // ---------- HOME SCREEN ROUTE ----------
        composable(NavRoute.HOME) {
            HomeScreen(
                username = "User",
                alarms = homeAlarms,
                onClickHome = {
                    // sudah di HOME, jadi gak pindah
                },
                onClickAlarm = {
                    // pindah ke Alarm
                    navController.navigate(NavRoute.ALARM) {
                        // optional: biar gak numpuk banyak backstack home -> alarm -> home
                        launchSingleTop = true
                    }
                },
                onClickAdd = {
                    // nanti ke Add Alarm screen, belum dibuat
                    // navController.navigate("add")
                },
                onClickGroup = {
                    // nanti bikin screen group
                },
                onClickProfile = {
                    // nanti bikin screen profile
                },
                onClickBell = {
                    // aksi bell di pojok kanan atas
                }
            )
        }

        // ---------- ALARM SCREEN ROUTE ----------
        composable(NavRoute.ALARM) {
            AlarmScreen(
                alarms = alarmTiles,
                onClickHome = {
                    navController.navigate(NavRoute.HOME) {
                        launchSingleTop = true
                        // popUpTo bikin kita "balik" tanpa numpuk layar terus
                        popUpTo(NavRoute.HOME) { inclusive = false }
                    }
                },
                onClickAlarm = {
                    // sudah di ALARM
                },
                onClickAdd = {
                    // nanti ke Add Alarm form
                },
                onClickGroup = {
                    // nanti group page
                },
                onClickProfile = {
                    // nanti profile page
                },
                onClickEdit = {
                    // tombol pensil kanan atas ditekan
                }
            )
        }
    }
}
