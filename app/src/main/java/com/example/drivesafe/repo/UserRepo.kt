package com.example.drivesafe.repo

import android.text.BoringLayout
import com.example.drivesafe.model.UserModel
import com.example.drivesafe.model.UserWithKyc
import com.example.drivesafe.view.ForgetPassword

interface UserRepo {

    fun register(
        email: String,
        password: String,
        callback: (Boolean, String, String) -> Unit
    )

    fun login(
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    )

    fun addUser(
        uid: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    )

    fun changePassword(
        oldPassword: String,
        newPassword: String,
        callback: (Boolean, String) -> Unit
    )

    fun getAllUser(callback: (Boolean, String, List<UserModel?>) -> Unit)

    fun getAllUserWithKyc(callback: (Boolean, String, List<UserWithKyc>) -> Unit)


    fun deleteUser(
        uid: String,
        callback: (Boolean, String) -> Unit
    )
}