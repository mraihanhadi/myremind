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
    val userController = remember { UserController() }
    val groupRepository = remember { MemoryGroupRepository() }
    val groupController = remember { GroupController(groupRepository) }
    var refreshFlag by remember { mutableStateOf(0) }
    val alarmRepo = remember { MemoryAlarmRepository() }
    val alarmController = remember { AlarmController(alarmRepo) }
    val userGroupNames = groupController.groupsForCurrentUser.map { it.getGroupName() }
    fun refreshUI() { refreshFlag++ }

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
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                    refreshUI()
                },
                onSignUp = {
                    userController.clearError()
                    userController.clearInfo()
                    navController.navigate("signup")
                },
                onResetPassword = {
                    userController.clearError()
                    userController.clearInfo()
                    navController.navigate(route = NavRoute.VERIFY)
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
                    navController.navigate(route = NavRoute.SIGNIN) {
                        popUpTo(route = NavRoute.SIGNIN) { inclusive = true }
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
                            navController.navigate(route = NavRoute.SIGNIN) {
                                popUpTo(route = NavRoute.SIGNIN) { inclusive = true }
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
                        userController.resetPassword(identifier) { success -> if(success){
                                navController.navigate(NavRoute.SIGNIN) {
                                    popUpTo(NavRoute.VERIFY) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        }
                }
            )
        }

//        composable(
//            route = NavRoute.CHANGE_PASSWORD,
//            arguments = listOf(
//                navArgument("emailArg") { defaultValue = "" }
//            )
//        ) { backStackEntry ->
//            val emailArg = backStackEntry.arguments?.getString("emailArg") ?: ""
//
//            ChangePasswordScreen(
//                loading = userController.loading,
//                errorMessage = userController.lastError,
//                onDismissError = { userController.clearError() },
//                onSubmit = { newPassword ->
//                    userController.resetPassword(
//                        email = emailArg,
//                        newPassword = newPassword,
//                        onDone = {
//                            navController.navigate(NavRoute.SIGNIN) {
//                                popUpTo(NavRoute.SIGNIN) { inclusive = true }
//                                launchSingleTop = true
//                            }
//                        }
//                    )
//                }
//            )
//        }

        composable(NavRoute.HOME) {
            val email = userController.currentUser?.getEmail() ?: ""
            LaunchedEffect(email) {
                groupController.refreshGroupsFor(email)
            }
            val joinedGroupIds = groupController.groupsForCurrentUser
                .map { it.getGroupId() }
            LaunchedEffect(joinedGroupIds) {
                alarmController.refresh(joinedGroupIds)
            }
            val homeAlarms = alarmController.alarmList.map { it.toAlarmEntry() }

            HomeScreen(
                username = userController.currentUser?.getUsername() ?: "User",
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

        composable(NavRoute.ALARM) {

            val email = userController.currentUser?.getEmail() ?: ""
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
                onClickAlarm = {  },
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

            val email = userController.currentUser?.getEmail() ?: ""

            LaunchedEffect(email) {
                groupController.refreshGroupsFor(email)
            }

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
                        label = g.getGroupName(),
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

                    val target = form.selectedTarget

                    alarmController.createAlarm(
                        title = form.title,
                        hour = form.hour,
                        minute = form.minute,
                        repeatDays = form.days,
                        ownerType = target.ownerType,
                        groupId = target.groupId,
                        groupName = target.groupName,
                        onSuccess = {

                            val joinedGroupIdsAfter = groupController.groupsForCurrentUser
                                .map { it.getGroupId() }

                            alarmController.refresh(joinedGroupIdsAfter)

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
            val username = userController.currentUser?.getUsername()
            val currentEmail = userController.currentUser?.getEmail()
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
                },
                onSignOut = {
                    userController.signOut {  }
                    navController.navigate(NavRoute.SIGNIN) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(NavRoute.GROUP) {
            val currentEmail = userController.currentUser?.getEmail() ?:""
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
                onClickGroup = {  },
                onClickProfile = {
                    navController.navigate(NavRoute.PROFILE) { launchSingleTop = true }
                },
                onClickAddRight = {
                    navController.navigate(NavRoute.GROUP_CREATE) { launchSingleTop = true }
                }
            )
        }


        composable(NavRoute.ALARM_DELETE) {

            val email = userController.currentUser?.getEmail() ?: ""
            LaunchedEffect(email) {
                groupController.refreshGroupsFor(email)
            }

            val joinedGroupIds = groupController.groupsForCurrentUser
                .map { it.getGroupId() }

            LaunchedEffect(joinedGroupIds) {
                alarmController.refresh(joinedGroupIds)
            }

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
                    alarmController.deleteAlarms(
                        ids = selectedIds,
                        joinedGroupIdsAfterDelete = joinedGroupIds,
                        onSuccess = {
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
            val alarmToEdit = alarmController.getAlarmById(alarmIdArg)
            if (alarmToEdit == null) {
                LaunchedEffect(Unit) {
                    navController.popBackStack()
                }
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
                    alarmController.EditDetailAlarm(
                        id = alarmIdArg,
                        title = editedForm.title,
                        days = editedForm.days,
                        hour = editedForm.hour,
                        minute = editedForm.minute,
                        ownerType = editedForm.selectedTarget.ownerType,
                        groupId = editedForm.selectedTarget.groupId,
                        groupName = editedForm.selectedTarget.groupName,
                        onSuccess = {
                            val joinedGroupIdsAfter = groupController.groupsForCurrentUser
                                .map { it.getGroupId() }
                            alarmController.refresh(joinedGroupIdsAfter)
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

            val currentEmail = userController.currentUser?.getEmail().orEmpty()

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
            val creatorEmail = userController.currentUser?.getEmail().orEmpty()

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
            val currentUserEmail = userController.currentUser?.getEmail().orEmpty()
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
                            groupController.refreshGroupsFor(currentUserEmail)
                            navController.popBackStack()
                        }
                    }
                }
            )
        }

    }
}
