package com.example.myremind.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.ui.platform.LocalContext
import com.example.myremind.alarm.AlarmScheduler
import com.example.myremind.controller.*
import com.example.myremind.model.*
import com.example.myremind.ui.mapper.*
import com.example.myremind.ui.view.*

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    val userController = remember { UserController() }
    val groupController = remember { GroupController() }
    val alarmController = remember { AlarmController() }
    val context = LocalContext.current
    val alarmScheduler = remember(context) { AlarmScheduler(context) }

    var refreshFlag by remember { mutableStateOf(0) }
    fun refreshUI() { refreshFlag++ }

    LaunchedEffect(alarmController.alarmList) {
        alarmController.alarmList.filter { it.enabled }.forEach { alarm ->
            alarmScheduler.scheduleAlarm(alarm)
        }
    }

    NavHost(
        navController = navController,
        startDestination = NavRoute.SIGNIN
    ) {


        composable(NavRoute.SIGNIN) {
            val a = refreshFlag

            LoginScreen(
                onSignIn = { identifier, password ->
                    userController.signIn(
                        identifier = identifier,
                        password = password,
                        onSuccess = {
                            navController.navigate(NavRoute.HOME) {
                                popUpTo(NavRoute.SIGNIN) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                    refreshUI()
                },
                onSignUp = {
                    userController.clearError()
                    userController.clearInfo()
                    navController.navigate(NavRoute.SIGNUP)
                },
                onResetPassword = {
                    userController.clearError()
                    userController.clearInfo()
                    navController.navigate(NavRoute.VERIFY)
                },
                loading = userController.loading,
                errorMessage = userController.lastError,
                onDismissError = {
                    userController.clearError()
                    refreshUI()
                },
                infoMessage = userController.infoMessage
            )
        }


        composable(NavRoute.SIGNUP) {
            val refresh = refreshFlag

            SignUpScreen(
                onBackToLogin = {
                    navController.navigate(NavRoute.SIGNIN) {
                        popUpTo(NavRoute.SIGNIN) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onSubmitSignUp = { username, email, pass, verify ->
                    userController.signUp(
                        username = username,
                        email = email,
                        password = pass,
                        verifyPassword = verify,
                        onSuccess = {
                            navController.navigate(NavRoute.SIGNIN) {
                                popUpTo(NavRoute.SIGNIN) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                },
                loading = userController.loading,
                errorMessage = userController.lastError,
                onDismissError = {
                    userController.clearError()
                    refreshUI()
                }
            )
        }


        composable(NavRoute.VERIFY) {
            LoginVerificationScreen(
                loading = userController.loading,
                errorMessage = userController.lastError,
                onDismissError = { userController.clearError() },
                onVerify = { identifier ->
                    userController.resetPassword(identifier) { success ->
                        if (success) {
                            navController.navigate(NavRoute.SIGNIN) {
                                popUpTo(NavRoute.VERIFY) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }
                }
            )
        }


        composable(NavRoute.HOME) {
            val email = userController.currentUser?.getEmail().orEmpty()

            LaunchedEffect(email) {
                if (email.isNotBlank()) {
                    groupController.refreshGroupsFor(email)
                }
            }


            val joinedGroupIds = groupController.groupsForCurrentUser.map { it.id }
            val joinedKey = joinedGroupIds.joinToString(",")

            LaunchedEffect(joinedKey) {
                alarmController.loadAlarms(joinedGroupIds)
            }


            val homeAlarms = alarmController.alarmList.map { it.toAlarmEntry() }

            HomeScreen(
                username = userController.currentUser?.getUsername() ?: "User",
                alarms = homeAlarms,
                onClickHome = { },
                onClickAlarm = {
                    navController.navigate(NavRoute.ALARM) { launchSingleTop = true }
                },
                onClickAdd = {
                    navController.navigate(NavRoute.ADD) { launchSingleTop = true }
                },
                onClickGroup = {
                    navController.navigate(NavRoute.GROUP) { launchSingleTop = true }
                },
                onClickProfile = {
                    navController.navigate(NavRoute.PROFILE) { launchSingleTop = true }
                },
                onClickBell = { }
            )
        }


        composable(NavRoute.ALARM) {
            val email = userController.currentUser?.getEmail().orEmpty()

            LaunchedEffect(email) {
                if (email.isNotBlank()) {
                    groupController.refreshGroupsFor(email)
                }
            }

            val joinedGroupIds = groupController.groupsForCurrentUser.map { it.id }
            val joinedKey = joinedGroupIds.joinToString(",")

            LaunchedEffect(joinedKey) {
                alarmController.loadAlarms(joinedGroupIds)
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
                onClickAlarm = { },
                onClickAdd = {
                    navController.navigate(NavRoute.ADD) { launchSingleTop = true }
                },
                onClickGroup = {
                    navController.navigate(NavRoute.GROUP) { launchSingleTop = true }
                },
                onClickProfile = {
                    navController.navigate(NavRoute.PROFILE) { launchSingleTop = true }
                },
                onClickDelete = {
                    navController.navigate(NavRoute.ALARM_DELETE) { launchSingleTop = true }
                },
                onAlarmClick = { alarmId ->
                    navController.navigate(NavRoute.editAlarmRoute(alarmId)) {
                        launchSingleTop = true
                    }
                }
            )
        }


        composable(NavRoute.ADD) {
            val email = userController.currentUser?.getEmail().orEmpty()

            LaunchedEffect(email) {
                if (email.isNotBlank()) groupController.refreshGroupsFor(email)
            }

            val groupChoices = run {
                val personal = listOf(
                    SelectableGroupOption("Personal","personal",null,null)
                )
                val fromGroups = groupController.groupsForCurrentUser.map { g ->
                    SelectableGroupOption(g.groupName, "group", g.id, g.groupName)
                }
                personal + fromGroups
            }

            AddAlarmScreen(
                groupChoices = groupChoices,
                onBack = { navController.popBackStack() },
                onSave = { form ->

                    val target = form.selectedTarget
                    val ownerType = target.ownerType.lowercase()


                    if (ownerType == "group" && target.groupId.isNullOrBlank()) {
                        alarmController.clearError()

                        return@AddAlarmScreen
                    }

                    val alarm = Alarm(
                        id = "",
                        title = form.title.trim(),
                        repeatDays = form.days,
                        hour = form.hour,
                        minute = form.minute,
                        ownerType = ownerType,
                        groupId = target.groupId,
                        groupName = target.groupName
                    )

                    val joinedGroupIds = groupController.groupsForCurrentUser.map { it.id }

                    alarmController.saveAlarm(alarm) { savedAlarm ->
                        alarmScheduler.scheduleAlarm(savedAlarm)
                        alarmController.loadAlarms(joinedGroupIds)
                        navController.navigate(NavRoute.ALARM) {
                            popUpTo(NavRoute.ADD) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }


        composable(NavRoute.PROFILE) {
            val username = userController.currentUser?.getUsername().orEmpty()
            val currentEmail = userController.currentUser?.getEmail().orEmpty()

            ProfileScreen(
                username = username,
                email = currentEmail,
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
                    navController.navigate(NavRoute.ADD) { launchSingleTop = true }
                },
                onClickGroup = {
                    navController.navigate(NavRoute.GROUP) { launchSingleTop = true }
                },
                onClickProfile = { },
                onSignOut = {
                    userController.signOut { }
                    navController.navigate(NavRoute.SIGNIN) { launchSingleTop = true }
                }
            )
        }


        composable(NavRoute.GROUP) {
            val currentEmail = userController.currentUser?.getEmail().orEmpty()

            LaunchedEffect(currentEmail) {
                if (currentEmail.isNotBlank()) {
                    groupController.refreshGroupsFor(currentEmail)
                }
            }

            GroupScreen(
                groups = groupController.groupsForCurrentUser,
                onGroupClick = { groupId ->
                    navController.navigate(NavRoute.groupInfoRoute(groupId)) {
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
                    navController.navigate(NavRoute.ADD) { launchSingleTop = true }
                },
                onClickGroup = { },
                onClickProfile = {
                    navController.navigate(NavRoute.PROFILE) { launchSingleTop = true }
                },
                onClickAddRight = {
                    navController.navigate(NavRoute.GROUP_CREATE) { launchSingleTop = true }
                }
            )
        }


        composable(NavRoute.ALARM_DELETE) {
            val email = userController.currentUser?.getEmail().orEmpty()

            LaunchedEffect(email) {
                if (email.isNotBlank()) {
                    groupController.refreshGroupsFor(email)
                }
            }

            val joinedGroupIds = groupController.groupsForCurrentUser.map { it.id }
            val joinedKey = joinedGroupIds.joinToString(",")

            LaunchedEffect(joinedKey) {
                alarmController.loadAlarms(joinedGroupIds)
            }


            val alarmTilesForDelete = alarmController.alarmList.map { it.toAlarmSmall() }

            AlarmDeleteScreen(
                alarms = alarmTilesForDelete,
                onBack = { navController.popBackStack() },
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
                    navController.navigate(NavRoute.ADD) { launchSingleTop = true }
                },
                onClickGroup = {
                    navController.navigate(NavRoute.GROUP) { launchSingleTop = true }
                },
                onClickProfile = {
                    navController.navigate(NavRoute.PROFILE) { launchSingleTop = true }
                },
                onDeleteSelected = { selectedIds -> val alarmsToDelete = selectedIds.mapNotNull { id ->
                    alarmController.getAlarmById(id)
                }
                    alarmsToDelete.forEach { alarm ->
                        alarmScheduler.cancelAlarm(alarm)
                        alarmController.deleteAlarm(alarm)
                    }
                }
            )
        }


        composable(
            route = NavRoute.EDIT_ALARM,
            arguments = listOf(navArgument("alarmId") { type = NavType.StringType })
        ) { backStackEntry ->

            val alarmIdArg = backStackEntry.arguments?.getString("alarmId") ?: ""
            val alarmToEdit = alarmController.getAlarmById(alarmIdArg)

            if (alarmToEdit == null) {
                LaunchedEffect(Unit) { navController.popBackStack() }
                return@composable
            }


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
                            label = g.groupName,
                            ownerType = "group",
                            groupId = g.id,
                            groupName = g.groupName
                        )
                    )
                }
            }

            EditAlarmScreen(
                alarm = alarmToEdit,
                groupChoices = groupChoices,
                onBack = { navController.popBackStack() },
                onSaveChanges = { editedForm ->
                    val target = editedForm.selectedTarget

                    val editedAlarm = alarmToEdit.copy(
                        title = editedForm.title,
                        repeatDays = editedForm.days,
                        hour = editedForm.hour,
                        minute = editedForm.minute,
                        ownerType = target.ownerType,
                        groupId = target.groupId,
                        groupName = target.groupName
                    )
                    val joinedGroupIds = groupController.groupsForCurrentUser.map { it.id }

                    alarmController.saveAlarm(editedAlarm) { savedAlarm ->
                        alarmScheduler.scheduleAlarm(savedAlarm)
                        alarmController.loadAlarms(joinedGroupIds)
                        navController.navigate(NavRoute.ALARM) {
                            popUpTo(NavRoute.ALARM) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }


        composable(
            route = NavRoute.GROUP_INFO_PATTERN,
            arguments = listOf(navArgument("groupId") { type = NavType.StringType })
        ) { backStackEntry ->

            val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
            val currentEmail = userController.currentUser?.getEmail().orEmpty()

            var groupDetail by remember { mutableStateOf<GroupDetail?>(null) }

            LaunchedEffect(groupId) {
                groupController.getGroupDetail(groupId) { group ->
                    groupDetail =
                        if (group != null) {
                            GroupDetail(
                                id = group.id,
                                name = group.groupName,
                                description = group.description,
                                members = group.members.mapIndexed { index, email ->
                                    GroupMember(
                                        name = email,
                                        role = if (index == 0) "Admin" else null
                                    )
                                }
                            )
                        } else null
                }
            }

            val uiDetail = groupDetail ?: GroupDetail(
                id = groupId,
                name = "Unknown Group",
                description = "",
                members = emptyList()
            )

            GroupInfoScreen(
                group = uiDetail,
                onBack = { navController.popBackStack() },
                onMemberClick = { },
                onAddMember = {
                    navController.navigate(NavRoute.groupAddMemberRoute(groupId)) {
                        launchSingleTop = true
                    }
                },
                onLeaveGroup = {
                    if (groupId.isNotBlank() && currentEmail.isNotBlank()) {
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
            val creatorEmail = userController.currentUser?.getEmail().orEmpty()

            GroupCreateScreen(
                loading = groupController.loading,
                errorMessage = groupController.lastError,
                onDismissError = { groupController.clearError() },
                onBack = { navController.popBackStack() },
                onCreateGroup = { groupName, description ->
                    groupController.createGroup(
                        creatorEmail = creatorEmail,
                        groupName = groupName,
                        description = description
                    ) { _ ->
                        navController.navigate(NavRoute.GROUP) {
                            popUpTo(NavRoute.GROUP) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }


        composable(
            route = NavRoute.GROUP_ADD_MEMBER_PATTERN,
            arguments = listOf(navArgument("groupId") { type = NavType.StringType })
        ) { backStackEntry ->

            val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
            val currentUserEmail = userController.currentUser?.getEmail().orEmpty()

            val groupName = groupController.groupsForCurrentUser
                .firstOrNull { it.id == groupId }
                ?.groupName ?: "Group"

            GroupAddMemberScreen(
                groupName = groupName,
                loading = groupController.loading,
                errorMessage = groupController.lastError,
                onDismissError = { groupController.clearError() },
                onBack = { navController.popBackStack() },
                onAddMember = { emailToAdd ->
                    if (groupId.isNotBlank() && currentUserEmail.isNotBlank()) {
                        groupController.addUser(
                            email = emailToAdd,
                            groupId = groupId,
                            currentUserEmail = currentUserEmail
                        ) { _ ->
                            groupController.refreshGroupsFor(currentUserEmail)
                            navController.popBackStack()
                        }
                    }
                }
            )
        }
    }
}
