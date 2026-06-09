package com.example.drivesafe.repo

import com.example.drivesafe.model.UserModel

interface UserRepo {

    fun register(
        email: String,
        password: String,
        callback: (Boolean, String, String) -> Unit
    )

    fun addUser(
        uid: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    )
}