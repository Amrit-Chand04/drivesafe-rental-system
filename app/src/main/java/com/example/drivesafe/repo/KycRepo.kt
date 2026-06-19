package com.example.drivesafe.repo

import android.content.Context
import android.net.Uri
interface KycRepo {

    fun submitKyc1(
        name: String,
        phone: String,
        doc: Uri,
        photo: Uri,
        callback: (Boolean, String) -> Unit
    )

    fun getFileNameFromUri(
        context: Context,
        imageUri: Uri
    ): String?
}