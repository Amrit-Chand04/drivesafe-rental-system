package com.example.drivesafe.viewmodel

import androidx.lifecycle.ViewModel
import com.example.drivesafe.model.UserModel
import com.example.drivesafe.repo.UserRepo
import com.example.drivesafe.repo.UserRepoImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel(
    private val repo: UserRepo = UserRepoImpl()
) : ViewModel() {

    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message.asStateFlow()

    private val _role = MutableStateFlow<String?>(null)
    val role: StateFlow<String?> = _role.asStateFlow()

    private val _user = MutableStateFlow<UserModel?>(null)
    val user: StateFlow<UserModel?> = _user.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _isLoggedOut = MutableStateFlow(false)
    val isLoggedOut = _isLoggedOut.asStateFlow()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _message.value = "Please fill all fields"
            return
        }

        _loading.value = true
        repo.login(email.trim(), password) { success, msg, userData ->
            _loading.value = false
            _message.value = msg

            if (success && userData != null) {
                _user.value = userData
            }
        }
    }

    fun sendPasswordResetEmail(email: String) {
        if (email.isBlank()) {
            _message.value = "Please enter email"
            return
        }

        _loading.value = true
        repo.sendPasswordResetEmail(email.trim()) { _, msg ->
            _loading.value = false
            _message.value = msg
        }
    }

    fun clearMessage() {
        _message.value = ""
    }

    fun logOut(){
        _loading.value = true

        repo.logOut { success, msg ->

            _loading.value = false
            _message.value = msg

            if (success) {
                _user.value = null
                _isLoggedOut.value = true
            }
        }
    }

    fun loadCurrentUser() {
        repo.getCurrentUser { success, userData ->
            if (success && userData != null) {
                _user.value = userData
            }
        }
    }
}