package com.example.drivesafe.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.drivesafe.model.UserModel
import com.example.drivesafe.model.UserWithKyc
import com.example.drivesafe.repo.UserRepo
import com.example.drivesafe.repo.UserRepoImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserViewModel(private val repo: UserRepo = UserRepoImpl()) : ViewModel() {
    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    fun clearUiMessage() {
        _uiMessage.value = null
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
            _uiMessage.value = "Please fill all fields"
            return
        }

        if (password != confirmPassword) {
            _uiMessage.value = "Passwords do not match"
            return
        }

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
                    if (addSuccess) {
                        _uiMessage.value = "Signup Successful"
                        onSuccess()
                    } else {
                        repo.rollbackCurrentUserRegistration()
                        _uiMessage.value = addMessage
                    }
                }
            } else {
                _uiMessage.value = if (message.contains("already", ignoreCase = true)) {
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

    // change password

    var message = mutableStateOf("")

    fun changePassword(
        oldPassword: String,
        newPassword: String,
        confirmPassword: String
    ) {

        // ui validation

        if (newPassword.length < 6) {
            message.value = "Password must be at least 6 characters"
            return
        }

        if (newPassword != confirmPassword) {
            message.value = "New Password and Confirm Password do not match"
            return
        }


        repo.changePassword(oldPassword, newPassword) { success, msg ->
            message.value = msg
        }
    }

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _allUsers = MutableStateFlow<List<UserWithKyc>>(emptyList())
    val allUsers: StateFlow<List<UserWithKyc>> = _allUsers.asStateFlow()

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