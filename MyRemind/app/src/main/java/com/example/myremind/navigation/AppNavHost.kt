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
import androidx.navigation.NavType
import androidx.navigation.navArgument

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
                onClickDelete = {
                    navController.navigate(NavRoute.ALARM_DELETE){
                        launchSingleTop = true
                    }
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
                    navController.navigate(NavRoute.GROUP) {
                        launchSingleTop = true
                    }
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
                onGroupClick = { item ->
                    // ⬇️ kirim ID saja
                    navController.navigate(NavRoute.groupInfo(item.id)) {
                        launchSingleTop = true
                    }
                },
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
                onClickProfile = {
                    navController.navigate(NavRoute.PROFILE) { launchSingleTop = true }
                },
                onClickAddRight = {
                    navController.navigate(NavRoute.GROUP_CREATE) { launchSingleTop = true }
                }
            )
        }


        composable(NavRoute.ALARM_DELETE) {
            AlarmDeleteScreen(
                alarms = alarmTiles, // sama data dummy List<AlarmSmall>
                onBack = {
                    navController.popBackStack() // balik ke Alarm normal
                },
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
                    // nanti: tambah alarm
                },
                onClickGroup = {
                    // nanti group
                },
                onClickProfile = {
                    // nanti profile
                },
                onDeleteSelected = { selectedIds ->
                    // TODO: hapus alarm dengan id di selectedIds
                    // lalu setelah delete selesai, mungkin popBackStack()
                }
            )
        }

        composable(
            route = NavRoute.GROUP_INFO_PATTERN,
            arguments = listOf(
                navArgument("groupId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getString("groupId") ?: "0"

            // 🔧 Contoh simple loader detail berdasar ID (mock):
            val detail = when (groupId) {
                "1" -> GroupDetail(
                    id = "1",
                    name = "GRUP 1",
                    description = "Test group 1",
                    members = listOf(
                        GroupMember("Member 1", "Admin"),
                        GroupMember("Member 2", null)
                    )
                )
                "2" -> GroupDetail(
                    id = "2",
                    name = "GRUP 2",
                    description = "Test group 2",
                    members = listOf(
                        GroupMember("Alice", "Admin"),
                        GroupMember("Bob", null)
                    )
                )
                else -> GroupDetail(
                    id = groupId,
                    name = "Unknown Group",
                    description = "",
                    members = emptyList()
                )
            }

            GroupInfoScreen(
                group = detail,
                onBack = { navController.popBackStack() },
                onMemberClick = { /* TODO: detail member */ },
                onAddMember = {
                    navController.navigate(NavRoute.GROUP_ADD_MEMBER) {
                        launchSingleTop = true
                    }
                },
                onLeaveGroup = { /* TODO: leave */ }
            )
        }

        composable(NavRoute.GROUP_CREATE) {
            GroupCreateScreen(
                onBack = { navController.popBackStack() },
                onCreateGroup = { newName ->
                    // TODO: simpan grup baru pakai newName
                    // lalu mungkin kembali:
                    navController.popBackStack()
                }
            )
        }

        composable(NavRoute.GROUP_ADD_MEMBER) {
            GroupAddMemberScreen(
                onBack = {
                    navController.popBackStack()
                },
                onAddMember = { usernameOrEmail ->
                    // TODO: logic nambah anggota group
                    // contoh: viewModel.addMember(usernameOrEmail)
                }
            )
        }

    }
}
