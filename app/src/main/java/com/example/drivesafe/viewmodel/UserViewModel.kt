package com.example.drivesafe.viewmodel

import androidx.lifecycle.ViewModel
import com.example.drivesafe.model.UserModel
import com.example.drivesafe.model.UserWithKyc
import com.example.drivesafe.repo.UserRepo
import com.example.drivesafe.repo.UserRepoImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserViewModel(private val repo: UserRepo = UserRepoImpl()) : ViewModel() {

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    private val _user = MutableStateFlow<UserModel?>(null)
    val user: StateFlow<UserModel?> = _user.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _isLoggedOut = MutableStateFlow(false)
    val isLoggedOut = _isLoggedOut.asStateFlow()

    private val _allUsers = MutableStateFlow<List<UserWithKyc>>(emptyList())
    val allUsers: StateFlow<List<UserWithKyc>> = _allUsers.asStateFlow()

    fun clearMessage() {
        _message.value = null
    }

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

    fun logOut() {
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

    fun registerUser(
        fullName: String,
        email: String,
        phone: String,
        password: String,
        confirmPassword: String,
        onSuccess: () -> Unit
    ) {
        if (fullName.isBlank() || email.isBlank() || phone.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            _message.value = "Please fill all fields"
            return
        }

        if (password != confirmPassword) {
            _message.value = "Passwords do not match"
            return
        }

        _loading.value = true
        repo.register(email, password) { success, message, uid ->
            if (success) {
                val user = UserModel(
                    uid = uid,
                    fullName = fullName,
                    email = email,
                    phone = phone,
                    role = "user"
                )

                repo.addUser(uid, user) { addSuccess, addMessage ->
                    _loading.value = false
                    if (addSuccess) {
                        _message.value = "Signup Successful"
                        onSuccess()
                    } else {
                        repo.rollbackCurrentUserRegistration()
                        _message.value = addMessage
                    }
                }
            } else {
                _loading.value = false
                _message.value = if (message.contains("already", ignoreCase = true)) {
                    "Email already in use."
                } else {
                    message
                }
            }
        }
    }

    fun addUser(
        id: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        repo.addUser(id, model, callback)
    }

    fun changePassword(
        oldPassword: String,
        newPassword: String,
        confirmPassword: String
    ) {
        if (oldPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
            _message.value = "All fields are required"
            return
        }

        if (newPassword.length < 6) {
            _message.value = "Password must be at least 6 characters"
            return
        }

        if (newPassword != confirmPassword) {
            _message.value = "New Password and Confirm Password do not match"
            return
        }

        if (oldPassword == newPassword) {
            _message.value = "New password cannot be same as old password"
            return
        }

        _loading.value = true
        repo.changePassword(oldPassword, newPassword) { success, msg ->
            _loading.value = false
            _message.value = msg
        }
    }

    fun updateUser(uid: String, fullName: String, phone: String) {
        if (fullName.isBlank() || phone.isBlank()) {
            _message.value = "All fields are required"
            return
        }

        _loading.value = true
        repo.updateUser(UserModel(uid = uid, fullName = fullName, phone = phone)) { success, msg ->
            _loading.value = false
            _message.value = msg
        }
    }

    fun getAllUsers() {
        _loading.value = true
        repo.getAllUserWithKyc { success, message, list ->
            _loading.value = false
            if (success) {
                _allUsers.value = list
            } else {
                _allUsers.value = emptyList()
            }
        }
    }

    fun deleteUser(
        uid: String,
        callback: (Boolean, String) -> Unit
    ) {
        repo.deleteUser(uid) { success, message ->
            callback(success, message)
            if (success) {
                getAllUsers()
            }
        }
    }
}