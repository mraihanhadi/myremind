package com.example.myremind.controller

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myremind.alarm.AlarmScheduler
import com.example.myremind.model.Alarm
import com.example.myremind.model.GroupDetail
import com.example.myremind.model.GroupMember
import com.example.myremind.model.SelectableGroupOption
import com.example.myremind.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch

class AlarmController : ViewModel() {

    var alarmList by mutableStateOf<List<Alarm>>(emptyList())
        private set

    var loading by mutableStateOf(false)
        private set

    var lastError by mutableStateOf<String?>(null)
        private set

    fun clearError() { lastError = null }
    fun saveAlarm(alarm: Alarm, onSuccess: (Alarm) -> Unit = {}) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            lastError = "User belum login."
            return
        }

        loading = true
        lastError = null

        viewModelScope.launch {
            try {
                val fs = FirebaseFirestore.getInstance()

                
                val alarmsRef = when (alarm.ownerType) {
                    "personal" -> {
                        fs.collection("users")
                            .document(uid)
                            .collection("alarms")
                    }

                    "group" -> {
                        val gid = alarm.groupId
                        if (gid.isNullOrBlank()) {
                            throw IllegalArgumentException("groupId kosong.")
                        }
                        fs.collection("groups")
                            .document(gid)
                            .collection("alarms")
                    }

                    else -> throw IllegalArgumentException("ownerType tidak valid.")
                }

                
                val docRef = if (alarm.id.isBlank()) {
                    alarmsRef.document() 
                } else {
                    alarmsRef.document(alarm.id)
                }

                val alarmToSave = alarm.copy(id = docRef.id)
                docRef.set(alarmToSave).await()

                onSuccess(alarmToSave)

            } catch (e: Exception) {
                lastError = e.message ?: "Gagal menyimpan alarm."
            } finally {
                loading = false
            }
        }
    }
    fun loadAlarms(joinedGroupIds: List<String>) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        loading = true
        lastError = null

        viewModelScope.launch {
            try {
                val fs = FirebaseFirestore.getInstance()

                
                val personalSnap = fs.collection("users")
                    .document(uid)
                    .collection("alarms")
                    .get()
                    .await()

                val personalAlarms = personalSnap.documents
                    .mapNotNull { it.toSafeAlarm() }

                
                val groupAlarms = mutableListOf<Alarm>()
                for (gid in joinedGroupIds) {
                    val groupSnap = fs.collection("groups")
                        .document(gid)
                        .collection("alarms")
                        .get()
                        .await()

                    groupAlarms += groupSnap.documents
                        .mapNotNull { it.toSafeAlarm() }
                }

                alarmList = personalAlarms + groupAlarms

            } catch (e: Exception) {
                lastError = e.message
            } finally {
                loading = false
            }
        }
    }
    fun deleteAlarm(alarm: Alarm, onSuccess: () -> Unit = {}) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            lastError = "User belum login."
            return
        }

        loading = true
        lastError = null

        viewModelScope.launch {
            try {
                val fs = FirebaseFirestore.getInstance()

                val docRef = if (alarm.ownerType == "group") {
                    val gid = alarm.groupId ?: throw IllegalArgumentException("groupId kosong.")
                    fs.collection("groups")
                        .document(gid)
                        .collection("alarms")
                        .document(alarm.id)
                } else {
                    fs.collection("users")
                        .document(uid)
                        .collection("alarms")
                        .document(alarm.id)
                }

                docRef.delete().await()
                onSuccess()

            } catch (e: Exception) {
                lastError = e.message ?: "Gagal menghapus alarm."
            } finally {
                loading = false
            }
        }
    }
    fun getAlarmById(id: String): Alarm? = alarmList.firstOrNull { it.id == id }

    private fun DocumentSnapshot.toSafeAlarm(): Alarm? {
        val alarm = try {
            toObject(Alarm::class.java)
        } catch (error: Exception) {
            Log.w(TAG, "Failed to parse alarm document ${id}", error)
            null
        } ?: return null

        val safeHour = runCatching { alarm.hour }.getOrNull()
        val safeMinute = runCatching { alarm.minute }.getOrNull()
        if (safeHour == null || safeMinute == null) {
            Log.w(TAG, "Skipping alarm ${alarm.id} due to missing time: hour=$safeHour minute=$safeMinute")
            return null
        }

        val normalizedRepeatDays = runCatching { alarm.repeatDays }
            .getOrElse { emptyList() }
            .let(AlarmScheduler::normalizeRepeatDays)

        return alarm.copy(
            hour = safeHour,
            minute = safeMinute,
            repeatDays = normalizedRepeatDays
        )
    }

    companion object {
        private const val TAG = "AlarmController"
    }
}

