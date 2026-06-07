package com.example.drivesafe.repo

import com.example.drivesafe.model.UserModel

interface  UserRepo {
    fun login(
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    )

}