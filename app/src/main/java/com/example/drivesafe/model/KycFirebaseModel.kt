package com.example.drivesafe.model

data class KycFirebaseModel(
    val name: String = "",
    val phone: String = "",
    val doc: String = "",
    val photo: String = "",
    val status: String = "pending"
)
