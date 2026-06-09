package com.example.drivesafe.viewmodel

import androidx.lifecycle.ViewModel
import com.example.drivesafe.model.UserModel
import com.example.drivesafe.repo.UserRepo

class UserViewModel(
    val repo: UserRepo
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
}
