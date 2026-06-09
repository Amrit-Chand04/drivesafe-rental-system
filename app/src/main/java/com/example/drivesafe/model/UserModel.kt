package com.example.drivesafe.model

data class UserModel(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = ""
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "fullName" to fullName,
            "email" to email,
            "phone" to phone
        )
    }
}