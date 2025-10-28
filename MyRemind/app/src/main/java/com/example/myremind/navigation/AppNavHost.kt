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
import com.example.myremind.controller.*
import com.example.myremind.model.*
import com.example.myremind.ui.mapper.*

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val authRepo = remember { MemoryAuthRepository() }
    val authController = remember { AuthController(authRepo) }
    val groupRepository = remember { MemoryGroupRepository() }
    val groupController = remember { GroupController(groupRepository) }
    var refreshFlag by remember { mutableStateOf(0) }
    val alarmRepo = remember { MemoryAlarmRepository() }
    val alarmController = remember { AlarmController(alarmRepo) }
    val userGroupNames = groupController.groupsForCurrentUser.map { it.getGroupName() }
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
            id = i + 1,
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

            // pastikan membership group user aktual
            val email = authController.currentUser?.getEmail() ?: ""
            // (kalau belum dipanggil sebelumnya, panggil di sini)
            LaunchedEffect(email) {
                groupController.refreshGroupsFor(email)
            }

            // list groupId yg user join
            val joinedGroupIds = groupController.groupsForCurrentUser
                .map { it.getGroupId() }

            // refresh alarm yg visible buat user ini
            LaunchedEffect(joinedGroupIds) {
                alarmController.refresh(joinedGroupIds)
            }

            // map alarm → UI model buat HOME
            val homeAlarms = alarmController.alarmList.map { it.toAlarmEntry() }

            HomeScreen(
                username = authController.currentUser?.getUsername() ?: "User",
                alarms = homeAlarms,
                onClickHome = {  },
                onClickAlarm = {
                    navController.navigate(NavRoute.ALARM) {
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
                onClickBell = {}
            )
        }

        // ---------- ALARM SCREEN ROUTE ----------
        composable(NavRoute.ALARM) {

            val email = authController.currentUser?.getEmail() ?: ""
            LaunchedEffect(email) {
                groupController.refreshGroupsFor(email)
            }

            val joinedGroupIds = groupController.groupsForCurrentUser
                .map { it.getGroupId() }

            LaunchedEffect(joinedGroupIds) {
                alarmController.refresh(joinedGroupIds)
            }

            val alarmTiles = alarmController.alarmList.map { it.toAlarmSmall() }

            AlarmScreen(
                alarms = alarmTiles,
                onClickHome = {
                    navController.navigate(NavRoute.HOME) {
                        launchSingleTop = true
                        popUpTo(NavRoute.HOME) { inclusive = false }
                    }
                },
                onClickAlarm = { /* already here */ },
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
                    navController.navigate(NavRoute.ALARM_DELETE) {
                        launchSingleTop = true
                    }
                },
                onAlarmClick = { alarmId ->
                    navController.navigate(NavRoute.editAlarmRoute(alarmId)) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(NavRoute.ADD) {

            // ambil user login
            val email = authController.currentUser?.getEmail() ?: ""

            // refresh groups user ini dulu
            LaunchedEffect(email) {
                groupController.refreshGroupsFor(email)
            }

            // buat daftar pilihan dropdown (Personal + group)
            val groupChoices: List<SelectableGroupOption> = run {
                val personal = listOf(
                    SelectableGroupOption(
                        label = "Personal",
                        ownerType = "personal",
                        groupId = null,
                        groupName = null
                    )
                )

                val fromGroups = groupController.groupsForCurrentUser.map { g ->
                    SelectableGroupOption(
                        label = g.getGroupName(),   // ex: "GRUP 1"
                        ownerType = "group",
                        groupId = g.getGroupId(),
                        groupName = g.getGroupName()
                    )
                }

                personal + fromGroups
            }

            AddAlarmScreen(
                groupChoices = groupChoices,
                onBack = { navController.popBackStack() },
                onSave = { form ->

                    // form.selectedTarget berisi info ownerType/groupId/groupName
                    val target = form.selectedTarget

                    // bikin alarm baru di repo
                    alarmController.createAlarm(
                        title = form.title,
                        hour = form.hour,
                        minute = form.minute,
                        repeatDays = form.days,
                        ownerType = target.ownerType,
                        groupId = target.groupId,
                        groupName = target.groupName,
                        onSuccess = {

                            // habis bikin, refresh alarm visible buat user ini
                            val joinedGroupIdsAfter = groupController.groupsForCurrentUser
                                .map { it.getGroupId() }

                            alarmController.refresh(joinedGroupIdsAfter)

                            // balik ke ALARM list
                            navController.navigate(NavRoute.ALARM) {
                                popUpTo(NavRoute.ALARM) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
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

            val email = authController.currentUser?.getEmail() ?: ""
            // pastikan group dan alarm up to date
            LaunchedEffect(email) {
                groupController.refreshGroupsFor(email)
            }

            val joinedGroupIds = groupController.groupsForCurrentUser
                .map { it.getGroupId() }

            LaunchedEffect(joinedGroupIds) {
                alarmController.refresh(joinedGroupIds)
            }

            // map controller → UI kecil grid
            val alarmTilesForDelete = alarmController.alarmList.map { it.toAlarmSmall() }

            AlarmDeleteScreen(
                alarms = alarmTilesForDelete,
                onBack = {
                    navController.popBackStack()
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
                onDeleteSelected = { selectedIds ->

                    // jalankan delete di controller
                    alarmController.deleteAlarms(
                        ids = selectedIds,
                        joinedGroupIdsAfterDelete = joinedGroupIds,
                        onSuccess = {
                            // setelah sukses hapus, langsung ke halaman Alarm list
                            navController.navigate(NavRoute.ALARM) {
                                popUpTo(NavRoute.ALARM) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                }
            )
        }


        composable(
            route = NavRoute.EDIT_ALARM,
            arguments = listOf(
                navArgument("alarmId") { type = NavType.IntType }
            )
        ) { backStackEntry ->

            val alarmIdArg = backStackEntry.arguments?.getInt("alarmId") ?: -1

            // 1. ambil data alarm existing dari controller / repo
            val alarmToEdit = alarmController.getAlarmById(alarmIdArg)

            // fallback kalau gak ketemu
            if (alarmToEdit == null) {
                // optionally langsung popBackStack atau tampilkan error sederhana
                // untuk sekarang: cukup balik
                LaunchedEffect(Unit) {
                    navController.popBackStack()
                }
                return@composable
            }

            // 2. siapkan list pilihan group (sama seperti AddAlarmScreen)
            val groupChoices: List<SelectableGroupOption> = buildList {
                add(
                    SelectableGroupOption(
                        label = "Personal",
                        ownerType = "personal",
                        groupId = null,
                        groupName = null
                    )
                )
                groupController.groupsForCurrentUser.forEach { g ->
                    add(
                        SelectableGroupOption(
                            label = g.getGroupName(),
                            ownerType = "group",
                            groupId = g.getGroupId(),
                            groupName = g.getGroupName()
                        )
                    )
                }
            }

            EditAlarmScreen(
                alarm = alarmToEdit,
                groupChoices = groupChoices,
                onBack = { navController.popBackStack() },
                onSaveChanges = { editedForm ->

                    // update alarm di controller
                    alarmController.updateAlarm(
                        id = alarmIdArg,
                        title = editedForm.title,
                        days = editedForm.days,
                        hour = editedForm.hour,
                        minute = editedForm.minute,
                        ownerType = editedForm.selectedTarget.ownerType,
                        groupId = editedForm.selectedTarget.groupId,
                        groupName = editedForm.selectedTarget.groupName,
                        onSuccess = {
                            // refresh list alarm yang kelihatan oleh user
                            val joinedGroupIdsAfter = groupController.groupsForCurrentUser
                                .map { it.getGroupId() }
                            alarmController.refresh(joinedGroupIdsAfter)

                            // balik ke halaman alarm
                            navController.navigate(NavRoute.ALARM) {
                                popUpTo(NavRoute.ALARM) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
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
