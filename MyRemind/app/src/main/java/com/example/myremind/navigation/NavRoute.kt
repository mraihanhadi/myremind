package com.example.myremind.navigation

object NavRoute {
    const val HOME = "home"
    const val ALARM = "alarm"
    // nanti kamu bisa tambah: GROUP, PROFILE, ADD, dsb

    const val ADD = "add"
    const val PROFILE = "profile"
    const val GROUP = "group"
    const val ALARM_DELETE = "alarm_delete"

    const val GROUP_INFO_PATTERN = "group_info/{groupId}"
    fun groupInfo(groupId: String) = "group_info/$groupId"
}
