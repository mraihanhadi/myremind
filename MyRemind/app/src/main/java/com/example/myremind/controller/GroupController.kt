package com.example.myremind.controller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.myremind.model.*

class GroupController(
    private val repo: MemoryGroupRepository
) {
    var lastError by mutableStateOf<String?>(null)
        private set

    var lastCreatedGroup by mutableStateOf<Group?>(null)
        private set

    var groupsForCurrentUser by mutableStateOf<List<Group>>(emptyList())
        private set

    fun clearError() {
        lastError = null
    }

    fun refreshGroupsFor(userEmail: String) {
        groupsForCurrentUser = repo.listGroupsForUser(userEmail)
    }

    fun createGroup(
        creatorEmail: String,
        groupName: String,
        description: String,
        onSuccess: (Group) -> Unit
    ) {
        val result = repo.createGroup(
            creatorEmail = creatorEmail,
            name = groupName,
            description = description
        )

        result
            .onSuccess { group ->
                lastCreatedGroup = group
                refreshGroupsFor(creatorEmail)
                onSuccess(group)
            }
            .onFailure { e ->
                lastError = e.message ?: "Gagal membuat grup"
            }
    }

    fun addUser(
        email: String,
        groupId: Int,
        currentUserEmail: String,
        onSuccess: (Group) -> Unit
    ) {
        val result = repo.addUserToGroup(email, groupId)
        result
            .onSuccess { group ->
                refreshGroupsFor(currentUserEmail)
                onSuccess(group)
            }
            .onFailure { e ->
                lastError = e.message ?: "Gagal menambah member"
            }
    }

    fun removeUser(
        email: String,
        groupId: Int,
        currentUserEmail: String,
        onSuccess: (Group) -> Unit
    ) {
        val result = repo.removeUserFromGroup(email, groupId)
        result
            .onSuccess { group ->
                refreshGroupsFor(currentUserEmail)
                onSuccess(group)
            }
            .onFailure { e ->
                lastError = e.message ?: "Gagal keluar dari grup"
            }
    }

    fun getGroupDetail(groupId: Int): Group? {
        return repo.getGroupById(groupId)
    }
}
