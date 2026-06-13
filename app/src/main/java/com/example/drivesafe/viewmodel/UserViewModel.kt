package com.example.drivesafe.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.drivesafe.model.UserModel
import com.example.drivesafe.repo.UserRepo
import com.example.drivesafe.repo.UserRepoImpl

class UserViewModel(
    private val repo: UserRepo = UserRepoImpl()
) : ViewModel() {
    fun register(
        email: String,
        password: String,
        callback: (Boolean, String, String) -> Unit
    ) {
        repo.register(email, password, callback)
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
}
