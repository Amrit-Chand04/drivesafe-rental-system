package com.example.drivesafe.repo

import com.example.drivesafe.model.UserModel
import com.example.drivesafe.model.UserWithKyc

interface UserRepo {

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

    fun sendPasswordResetEmail(
        email: String,
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

    fun rollbackCurrentUserRegistration()

    fun getCurrentUser(callback: (Boolean, UserModel?) -> Unit)

    fun updateUser(user: UserModel, callback: (Boolean, String) -> Unit)


    fun deleteUser(
        uid: String,
        callback: (Boolean, String) -> Unit
    )

    fun logOut(callback : (Boolean, String) -> Unit)
}