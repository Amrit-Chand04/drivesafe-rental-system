package com.example.drivesafe.repo

import com.example.drivesafe.model.UserModel

interface AuthRepo {
    fun register(
        email: String,
        password: String,
        callback: (Boolean, String, String) -> Unit
    )

    fun login(
        email: String,
        password: String,
        callback: (Boolean, String, UserModel?) -> Unit
    )

    fun addUser(
        uid: String,
        user: UserModel,
        callback: (Boolean, String) -> Unit
    )

    fun rollbackCurrentUserRegistration()
}
