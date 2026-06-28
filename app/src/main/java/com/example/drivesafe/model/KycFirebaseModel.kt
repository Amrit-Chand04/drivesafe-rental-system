package com.example.drivesafe.model

data class KycFirebaseModel(
    val uid: String = "",
    val name: String = "",
    val phone: String = "",
    val doc: String = "",
    val photo: String = "",
    val status: String = "pending",
    val rejectionReason: String = ""
)