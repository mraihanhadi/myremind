package com.example.myremind.model

class MemoryGroupRepository {
    private val groups = mutableListOf<Group>()
    private var nextId = 0

    fun createGroup(
        creatorEmail: String,
        name: String,
        description: String
    ): Result<Group> {
        val newId = nextId++
        val newGroup = Group.createGroup(
            creatorEmail = creatorEmail,
            groupId = newId,
            groupName = name,
            description = description
        )

        groups.add(newGroup)
        return Result.success(newGroup)
    }

    fun addUserToGroup(email: String, groupId: Int): Result<Group> {
        val group = groups.find { it.getGroupId() == groupId }
            ?: return Result.failure(Exception("Group tidak ditemukan"))

        group.addUser(email)
        return Result.success(group)
    }

    fun removeUserFromGroup(email: String, groupId: Int): Result<Group> {
        val group = groups.find { it.getGroupId() == groupId }
            ?: return Result.failure(Exception("Group tidak ditemukan"))

        group.leaveGroup(email)
        return Result.success(group)
    }

    fun listGroupsForUser(email: String): List<Group> {
        return groups.filter { g -> g.getMembers().contains(email) }
    }

    fun getGroupById(groupId: Int): Group? {
        return groups.find { it.getGroupId() == groupId }
    }
}