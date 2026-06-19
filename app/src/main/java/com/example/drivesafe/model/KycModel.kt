package com.example.drivesafe.model

import android.net.Uri

data class KycModel(
    val name: String = "",
    val phone: String = "",
    val doc: Uri? = null,
    val photo: Uri? = null
)

