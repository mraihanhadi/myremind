package com.example.myremind.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myremind.ui.view.*
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.compose.runtime.LaunchedEffect
import com.example.myremind.controller.AuthController
import com.example.myremind.controller.GroupController
import com.example.myremind.model.*

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val authRepo = remember { MemoryAuthRepository() }
    val authController = remember { AuthController(authRepo) }
    val groupRepository = remember { MemoryGroupRepository() }
    val groupController = remember { GroupController(groupRepository) }
    var refreshFlag by remember { mutableStateOf(0) }
    fun refreshUI() { refreshFlag++ }

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
        startDestination = NavRoute.SIGNIN
    ) {

        composable(NavRoute.SIGNIN) {
            val a = refreshFlag

            LoginScreen(
                onSignIn = { identifier, password ->
                    authController.signIn(
                        identifier = identifier,
                        password = password,
                        onSuccess = {
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                    refreshUI()
                },
                onSignUp = {
                    navController.navigate("signup")
                },
                onResetPassword = {
                    navController.navigate(route = NavRoute.VERIFY)
                },
                loading = authController.loading,
                errorMessage = authController.lastError,
                onDismissError = {
                    authController.clearError()
                    refreshUI()
                }
            )
        }

        composable(NavRoute.SIGNUP) {
            val refresh = refreshFlag

            SignUpScreen(
                onBackToLogin = {
                    navController.navigate(route = NavRoute.SIGNIN) {
                        popUpTo(route = NavRoute.SIGNIN) { inclusive = true }
                        launchSingleTop = true
                    }
                },

                onSubmitSignUp = { username, email, pass, verify ->
                    authController.signUp(
                        username = username,
                        email = email,
                        password = pass,
                        verifyPassword = verify,
                        onSuccess = {
                            navController.navigate(route = NavRoute.SIGNIN) {
                                popUpTo(route = NavRoute.SIGNIN) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                },
                loading = authController.loading,
                errorMessage = authController.lastError,
                onDismissError = {
                    authController.clearError()
                    refreshUI()
                }
            )
        }

        composable(NavRoute.VERIFY) {
            LoginVerificationScreen(
                loading = authController.loading,
                errorMessage = authController.lastError,
                onDismissError = { authController.clearError() },
                onResend = { /* optional, bisa kosong */ },
                onVerify = { identifier ->
                    // langsung navigate ke CHANGE_PASSWORD sambil bawa email/username itu
                    navController.navigate(
                        "change_password/${identifier}"
                    ) {
                        popUpTo(NavRoute.VERIFY) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = NavRoute.CHANGE_PASSWORD,
            arguments = listOf(
                navArgument("emailArg") { defaultValue = "" }
            )
        ) { backStackEntry ->
            val emailArg = backStackEntry.arguments?.getString("emailArg") ?: ""

            ChangePasswordScreen(
                loading = authController.loading,
                errorMessage = authController.lastError,
                onDismissError = { authController.clearError() },
                onSubmit = { newPassword ->
                    authController.resetPassword(
                        email = emailArg,
                        newPassword = newPassword,
                        onDone = {
                            // setelah password diganti, balik ke LOGIN
                            navController.navigate(NavRoute.SIGNIN) {
                                popUpTo(NavRoute.SIGNIN) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                }
            )
        }

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
            val username = authController.currentUser?.getUsername()
            val currentEmail = authController.currentUser?.getEmail()
            ProfileScreen(
                username = username.toString(),
                email = currentEmail.toString(),
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
                onSignOut = {
                    navController.navigate(NavRoute.SIGNIN) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(NavRoute.GROUP) {
            val currentEmail = authController.currentUser?.getEmail() ?:""
            LaunchedEffect(currentEmail) {
                if (currentEmail.isNotBlank()) {
                    groupController.refreshGroupsFor(currentEmail)
                }
            }

            GroupScreen(
                groups = groupController.groupsForCurrentUser,
                onGroupClick = { groupId ->
                    navController.navigate("group_info/$groupId") {
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
                onClickAddCenter = {
                    navController.navigate(NavRoute.ADD){
                        launchSingleTop = true
                    }
                },
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
                navArgument("groupId") { type = NavType.IntType }
            )

        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getInt("groupId") ?: -1
            val groupEntity = groupController.getGroupDetail(groupId)
            val uiDetail = if (groupEntity != null) {
                val membersEmails = groupEntity.getMembers()

                GroupDetail(
                    id = groupEntity.getGroupId().toString(),
                    name = groupEntity.getGroupName(),
                    description = groupEntity.getDescription(),
                    members = membersEmails.mapIndexed { index, email ->
                        GroupMember(
                            name = email,
                            role = if (index == 0) "Admin" else null
                        )
                    }
                )
            } else {
                GroupDetail(
                    id = groupId.toString(),
                    name = "Unknown Group",
                    description = "",
                    members = emptyList()
                )
            }

            val currentEmail = authController.currentUser?.getEmail().orEmpty()

            GroupInfoScreen(
                group = uiDetail,
                onBack = { navController.popBackStack() },
                onMemberClick = { member ->

                },
                onAddMember = {
                    navController.navigate(NavRoute.GROUP_ADD_MEMBER) {
                        launchSingleTop = true
                    }
                },
                onLeaveGroup = {
                    if (groupId != -1 && currentEmail.isNotBlank()) {
                        groupController.removeUser(
                            email = currentEmail,
                            groupId = groupId,
                            currentUserEmail = currentEmail
                        ) {
                            navController.navigate(NavRoute.GROUP) {
                                popUpTo(NavRoute.GROUP) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }
                }
            )
        }

        composable(NavRoute.GROUP_CREATE) {
            val creatorEmail = authController.currentUser?.getEmail().orEmpty()

            GroupCreateScreen(
                loading = false,
                errorMessage = groupController.lastError,
                onDismissError = { groupController.clearError() },
                onBack = { navController.popBackStack() },
                onCreateGroup = { groupName, description ->
                    groupController.createGroup(
                        creatorEmail = creatorEmail,
                        groupName = groupName,
                        description = description
                    ) {
                        _newgroup ->
                        navController.navigate(NavRoute.GROUP) {
                            popUpTo(NavRoute.GROUP) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }

        composable(NavRoute.GROUP_ADD_MEMBER) {
            val currentUserEmail = authController.currentUser?.getEmail().orEmpty()
            val currentGroup = groupController.lastCreatedGroup
                ?: groupController.groupsForCurrentUser.lastOrNull()

            val groupId = currentGroup?.getGroupId() ?: -1
            val groupName = currentGroup?.getGroupName() ?: "Unknown Group"
            GroupAddMemberScreen(
                groupName = groupName,
                loading = false,
                errorMessage = groupController.lastError,
                onDismissError = { groupController.clearError() },
                onBack = {
                    navController.popBackStack()
                },
                onAddMember = { emailToAdd ->
                    if (groupId != -1 && currentUserEmail.isNotBlank()) {
                        groupController.addUser(
                            email = emailToAdd,
                            groupId = groupId,
                            currentUserEmail = currentUserEmail
                        ) { updatedGroup ->
                            // Berhasil nambah member → refresh list & balik
                            groupController.refreshGroupsFor(currentUserEmail)
                            navController.popBackStack()
                        }
                    }
                }
            )
        }

    }
}
