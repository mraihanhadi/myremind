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
        val email = userEmail.trim().lowercase()
        if (email.isBlank()) {
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
                    .whereArrayContains("members", email)
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
        val creator = creatorEmail.trim().lowercase()
        val name = groupName.trim()

        if (creator.isBlank() || name.isBlank()) {
            lastError = "Creator dan nama grup wajib diisi."
            return
        }

        loading = true
        lastError = null

        viewModelScope.launch {
            try {
                val firestore = FirebaseFirestore.getInstance()

                val docRef = firestore.collection("groups").document()

                val group = Group(
                    id = docRef.id,
                    groupName = name,
                    description = description,
                    members = listOf(creator)
                )

                docRef.set(group).await()

                lastCreatedGroup = group
                refreshGroupsFor(creator)

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
        val targetEmail = email.trim().lowercase()
        val currentEmail = currentUserEmail.trim().lowercase()

        if (targetEmail.isBlank() || groupId.isBlank()) {
            lastError = "Email / groupId tidak valid."
            return
        }

        loading = true
        lastError = null

        viewModelScope.launch {
            try {
                val firestore = FirebaseFirestore.getInstance()

                // Prevent adding yourself (optional but useful UX)
                if (targetEmail == currentEmail) {
                    loading = false
                    lastError = "Tidak bisa menambahkan diri sendiri."
                    return@launch
                }

                // Check existence via user_lookup (avoids querying /users)
                val lookupDoc = firestore.collection("user_lookup")
                    .document(targetEmail)
                    .get()
                    .await()

                if (!lookupDoc.exists()) {
                    loading = false
                    lastError = "User tidak ditemukan."
                    return@launch
                }

                val docRef = firestore.collection("groups").document(groupId)

                docRef.update("members", FieldValue.arrayUnion(targetEmail)).await()

                val updated = docRef.get().await().toObject(Group::class.java)

                refreshGroupsFor(currentEmail)
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
        onSuccess: (groupDeleted: Boolean) -> Unit
    ) {
        val targetEmail = email.trim().lowercase()
        val currentEmail = currentUserEmail.trim().lowercase()

        if (targetEmail.isBlank() || groupId.isBlank()) {
            lastError = "Email / groupId tidak valid."
            return
        }

        loading = true
        lastError = null

        viewModelScope.launch {
            try {
                val firestore = FirebaseFirestore.getInstance()
                val docRef = firestore.collection("groups").document(groupId)

                val groupDeleted = firestore.runTransaction { tx ->
                    val snap = tx.get(docRef)

                    val members = (snap.get("members") as? List<*>)
                        ?.filterIsInstance<String>()
                        ?: emptyList()

                    val newMembers = members.filter { it.trim().lowercase() != targetEmail }

                    if (newMembers.isEmpty()) {
                        tx.delete(docRef)
                        true
                    } else {
                        tx.update(docRef, "members", newMembers)
                        false
                    }
                }.await()

                refreshGroupsFor(currentEmail)

                loading = false
                onSuccess(groupDeleted)
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