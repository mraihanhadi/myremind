package com.example.myremind.navigation

object NavRoute {
    const val SIGNIN = "signin"
    const val SIGNUP = "signup"

    const val VERIFY = "verify"
    const val HOME = "home"
    const val ALARM = "alarm"
    // nanti kamu bisa tambah: GROUP, PROFILE, ADD, dsb

    const val ADD = "add"
    const val PROFILE = "profile"
    const val GROUP = "group"
    const val ALARM_DELETE = "alarm_delete"
    const val EDIT_ALARM = "edit_alarm/{alarmId}"

    fun editAlarmRoute(alarmId: Int): String {
        return "edit_alarm/$alarmId"
    }
    const val GROUP_CREATE = "group_create"
    const val GROUP_INFO_PATTERN = "group_info/{groupId}"
    fun groupInfo(groupId: String) = "group_info/$groupId"

    const val CHANGE_PASSWORD = "change_password/{emailArg}"


    const val GROUP_ADD_MEMBER = "group_add_member"
}
