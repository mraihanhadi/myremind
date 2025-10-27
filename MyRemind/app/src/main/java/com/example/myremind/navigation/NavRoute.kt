package com.example.myremind.navigation

object NavRoute {
    const val SIGNIN = "signin"
    const val SIGNUP = "signup"
    const val HOME = "home"
    const val ALARM = "alarm"
    const val ADD = "add"
    const val PROFILE = "profile"
    const val GROUP = "group"
    const val ALARM_DELETE = "alarm_delete"
    const val GROUP_CREATE = "group_create"
    const val GROUP_INFO_PATTERN = "group_info/{groupId}"
    fun groupInfo(groupId: String) = "group_info/$groupId"
    const val GROUP_ADD_MEMBER = "group_add_member"
}
