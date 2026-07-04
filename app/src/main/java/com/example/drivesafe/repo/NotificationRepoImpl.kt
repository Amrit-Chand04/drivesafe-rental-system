package com.example.drivesafe.repo

import com.example.drivesafe.model.NotificationModel
import com.google.firebase.database.FirebaseDatabase

class NotificationRepoImpl : NotificationRepo {

    private val notificationRef = FirebaseDatabase.getInstance().getReference("notifications")

    override fun addNotification(
        title: String,
        message: String,
        callback: (Boolean, String) -> Unit
    ) {
        val id = notificationRef.push().key ?: ""

        val notification = NotificationModel(
            id = id,
            title = title,
            message = message,
            createdAt = System.currentTimeMillis()
        )

        notificationRef.child(id).setValue(notification).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Notification added")
            } else {
                callback(false, "${it.exception?.message}")
            }
        }
    }

    override fun getNotifications(callback: (Boolean, String, List<NotificationModel>?) -> Unit) {
        notificationRef.get()
            .addOnSuccessListener { snapshot ->
                try {
                    val list = snapshot.children.mapNotNull { snap ->
                        snap.getValue(NotificationModel::class.java)
                    }
                    callback(true, "Loaded", list)
                } catch (e: Exception) {
                    callback(false, "Parse crash: ${e.message}", null)
                }
            }
            .addOnFailureListener { error ->
                callback(false, error.message ?: "Error", null)
            }
    }
}
