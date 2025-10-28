package com.example.myremind.model

data class Group(
    private var groupId: Int,
    private var groupName: String,
    private var description: String,
    // simpan anggota di memori sebagai list email
    private val members: MutableList<String> = mutableListOf()
) {

    // ====== getter sesuai diagram ======
    fun getGroupId(): Int = groupId
    fun getGroupName(): String = groupName
    fun getDescription(): String = description

    // ====== setter sesuai diagram ======
    fun setGroupName(newName: String) {
        groupName = newName
    }

    fun setDescription(newDescription: String) {
        description = newDescription
    }

    // (tambahan opsional: lihat anggota)
    fun getMembers(): List<String> = members

    // ====== behavior sesuai diagram ======

    // AddUser(Email: string, group_id: integer)
    // Catatan: di UML kamu, method ini butuh group_id, tapi di class kita
    // group_id sudah ada di dalam objek. Jadi kita bisa sederhanakan.
    fun addUser(email: String) {
        if (!members.contains(email)) {
            members.add(email)
        }
    }

    // LeaveGroup(Email: string, group_id: integer)
    fun leaveGroup(email: String) {
        members.remove(email)
    }

    companion object {

        // CreateGroup(Email: string, group_name: string, description: string)
        // - Email di sini kita anggap creator pertama jadi anggota awal
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
