package com.example.drivesafe.viewmodel

import androidx.lifecycle.ViewModel
import com.example.drivesafe.model.NotificationModel
import com.example.drivesafe.repo.NotificationRepo
import com.example.drivesafe.repo.NotificationRepoImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NotificationViewModel(
    private val repo: NotificationRepo = NotificationRepoImpl()
) : ViewModel() {

    private val _notifications = MutableStateFlow<List<NotificationModel>>(emptyList())
    val notifications: StateFlow<List<NotificationModel>> = _notifications

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadNotifications() {
        _isLoading.value = true
        repo.getNotifications { success, _, list ->
            _isLoading.value = false
            _notifications.value = if (success) list ?: emptyList() else emptyList()
        }
    }
}
