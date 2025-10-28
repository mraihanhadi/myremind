package com.example.myremind.model

data class Group(
    private var groupId: Int,
    private var groupName: String,
    private var description: String,
    private val members: MutableList<String> = mutableListOf()
) {
    fun getGroupId(): Int = groupId
    fun getGroupName(): String = groupName
    fun getDescription(): String = description

    fun setGroupName(newName: String) {
        groupName = newName
    }

    fun setDescription(newDescription: String) {
        description = newDescription
    }

    fun getMembers(): List<String> = members

    fun addUser(email: String) {
        if (!members.contains(email)) {
            members.add(email)
        }
    }

    fun leaveGroup(email: String) {
        members.remove(email)
    }

    companion object {
        fun createGroup(
            creatorEmail: String,
            groupId: Int,
            groupName: String,
            description: String
        ): Group {
            val g = Group(
                groupId = groupId,
                groupName = groupName,
                description = description,
                members = mutableListOf()
            )
            g.addUser(creatorEmail)
            return g
        }
    }
}
