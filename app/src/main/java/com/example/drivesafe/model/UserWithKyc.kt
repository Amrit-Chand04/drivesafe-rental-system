package com.example.drivesafe.model

data class UserWithKyc(
    val user: UserModel,
    val kyc: KycFirebaseModel? = null
)