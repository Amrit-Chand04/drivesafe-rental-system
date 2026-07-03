package com.example.drivesafe.repo

import com.example.drivesafe.model.NotificationModel

interface NotificationRepo {
    fun addNotification(title: String, message: String, callback: (Boolean, String) -> Unit = { _, _ -> })
    fun getNotifications(callback: (Boolean, String, List<NotificationModel>?) -> Unit)
}
