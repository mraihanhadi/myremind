package com.example.myremind.controller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myremind.model.Group
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class GroupController : ViewModel() {
    var lastError by mutableStateOf<String?>(null)
        private set

    var lastCreatedGroup by mutableStateOf<Group?>(null)
        private set

    var groupsForCurrentUser by mutableStateOf<List<Group>>(emptyList())
        private set

    var loading by mutableStateOf(false)
        private set

    fun clearError() {
        lastError = null
    }
    fun refreshGroupsFor(userEmail: String) {
        if (userEmail.isBlank()) {
            groupsForCurrentUser = emptyList()
            return
        }

        loading = true
        lastError = null

        viewModelScope.launch {
            try {
                val firestore = FirebaseFirestore.getInstance()

                val snapshot = firestore
                    .collection("groups")
                    .whereArrayContains("members", userEmail)
                    .get()
                    .await()

                groupsForCurrentUser = snapshot.toObjects(Group::class.java)

                loading = false
            } catch (e: Exception) {
                loading = false
                lastError = e.message ?: "Gagal mengambil daftar grup."
            }
        }
    }
    fun createGroup(
        creatorEmail: String,
        groupName: String,
        description: String,
        onSuccess: (Group) -> Unit
    ) {
        if (creatorEmail.isBlank() || groupName.isBlank()) {
            lastError = "Creator dan nama grup wajib diisi."
            return
        }

        loading = true
        lastError = null

        viewModelScope.launch {
            try {
                val firestore = FirebaseFirestore.getInstance()

                val groupsRef = firestore.collection("groups")
                val docRef = groupsRef.document() 

                val group = Group(
                    id = docRef.id,
                    groupName = groupName,
                    description = description,
                    members = listOf(creatorEmail)
                )

                docRef.set(group).await()

                lastCreatedGroup = group
                refreshGroupsFor(creatorEmail)
                loading = false
                onSuccess(group)

            } catch (e: Exception) {
                loading = false
                lastError = e.message ?: "Gagal membuat grup."
            }
        }
    }
    fun addUser(
        email: String,
        groupId: String,
        currentUserEmail: String,
        onSuccess: (Group) -> Unit
    ) {
        if (email.isBlank() || groupId.isBlank()) {
            lastError = "Email / groupId tidak valid."
            return
        }

        loading = true
        lastError = null

        viewModelScope.launch {
            try {
                val firestore = FirebaseFirestore.getInstance()

                val docRef = firestore
                    .collection("groups")
                    .document(groupId)

                
                docRef.update("members", FieldValue.arrayUnion(email)).await()

                
                val updated = docRef.get().await().toObject(Group::class.java)

                refreshGroupsFor(currentUserEmail)
                loading = false

                if (updated != null) onSuccess(updated)

            } catch (e: Exception) {
                loading = false
                lastError = e.message ?: "Gagal menambah member."
            }
        }
    }
    fun removeUser(
        email: String,
        groupId: String,
        currentUserEmail: String,
        onSuccess: (Group) -> Unit
    ) {
        if (email.isBlank() || groupId.isBlank()) {
            lastError = "Email / groupId tidak valid."
            return
        }

        loading = true
        lastError = null

        viewModelScope.launch {
            try {
                val firestore = FirebaseFirestore.getInstance()

                val docRef = firestore
                    .collection("groups")
                    .document(groupId)

                docRef.update("members", FieldValue.arrayRemove(email)).await()

                val updated = docRef.get().await().toObject(Group::class.java)

                refreshGroupsFor(currentUserEmail)
                loading = false

                if (updated != null) onSuccess(updated)

            } catch (e: Exception) {
                loading = false
                lastError = e.message ?: "Gagal keluar dari grup."
            }
        }
    }
    fun getGroupDetail(groupId: String, onResult: (Group?) -> Unit) {
        if (groupId.isBlank()) {
            onResult(null)
            return
        }

        viewModelScope.launch {
            try {
                val firestore = FirebaseFirestore.getInstance()

                val group = firestore
                    .collection("groups")
                    .document(groupId)
                    .get()
                    .await()
                    .toObject(Group::class.java)

                onResult(group)

            } catch (e: Exception) {
                lastError = e.message ?: "Gagal mengambil detail grup."
                onResult(null)
            }
        }
    }
}
