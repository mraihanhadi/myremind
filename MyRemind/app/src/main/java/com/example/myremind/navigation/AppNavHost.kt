package com.example.myremind.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myremind.ui.screens.*

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
                    navController.navigate(NavRoute.ADD) {
                        launchSingleTop = true
                    }
                },
                onClickGroup = {
                    navController.navigate(NavRoute.GROUP) {
                        launchSingleTop = true
                    }
                },
                onClickProfile = {
                    navController.navigate(NavRoute.PROFILE) {
                        launchSingleTop = true
                    }
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
                    navController.navigate(NavRoute.ADD) {
                        launchSingleTop = true
                    }
                },
                onClickGroup = {
                    navController.navigate(NavRoute.GROUP) {
                        launchSingleTop = true
                    }
                },
                onClickProfile = {
                    navController.navigate(NavRoute.PROFILE) {
                        launchSingleTop = true
                    }
                },
                onClickEdit = {
                    // tombol pensil kanan atas ditekan
                }
            )
        }

        composable(NavRoute.ADD) {
            AddAlarmScreen(
                onBack = { navController.popBackStack() },
                onSave = { form ->
                    // TODO: simpan ke database / ViewModel
                    navController.popBackStack() // balik setelah save
                }
            )
        }

        composable(NavRoute.PROFILE) {
            ProfileScreen(
                username = "Username",
                email = "yesking67@gmail.com",
                onClickHome = {
                    navController.navigate(NavRoute.HOME) {
                        launchSingleTop = true
                        popUpTo(NavRoute.HOME) { inclusive = false }
                    }
                },
                onClickAlarm = {
                    navController.navigate(NavRoute.ALARM) {
                        launchSingleTop = true
                        popUpTo(NavRoute.ALARM) { inclusive = false }
                    }
                },
                onClickAdd = {
                    navController.navigate(NavRoute.ADD){
                        launchSingleTop = true
                    }
                },
                onClickGroup = {
                    // nanti bikin GroupScreen
                },
                onClickProfile = {
                    // udah di Profile
                },
                onEditUsername = {
                    // open username edit dialog
                },
                onEditEmail = {
                    // open email edit dialog
                },
                onSignOut = {
                    // handle logout
                }
            )
        }
        composable(NavRoute.GROUP) {
            val groups = listOf(
                GroupItem("1", "GRUP 1"),
                GroupItem("2", "GRUP 2")
            )
            GroupScreen(
                groups = groups,
                onGroupClick = { /* TODO: ke detail group */ },
                onClickHome = {
                    navController.navigate(NavRoute.HOME) {
                        launchSingleTop = true
                        popUpTo(NavRoute.HOME)
                    }
                },
                onClickAlarm = {
                    navController.navigate(NavRoute.ALARM) { launchSingleTop = true }
                },
                onClickAddCenter = { /* FAB kuning di tengah */ },
                onClickGroup = { /* stay */ },
                onClickProfile = { /* nanti */ },
                onClickAddRight = { /* mini FAB putih kanan bawah */ }
            )
        }
    }
}
