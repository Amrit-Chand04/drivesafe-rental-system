package com.example.drivesafe.repo

import android.content.Context
import android.net.Uri
import com.example.drivesafe.model.KycFirebaseModel
interface KycRepo {

    fun submitKyc1(
        name: String,
        phone: String,
        doc: Uri,
        photo: Uri,
        callback: (Boolean, String) -> Unit
    )

    fun getMyKycStatus(callback: (Boolean) -> Unit)

    fun getMyKycRecord(callback: (KycFirebaseModel?) -> Unit)

    fun getAllKycRecords(callback: (List<KycFirebaseModel>) -> Unit)

    fun updateKycStatus(uid: String, status: String, callback: (Boolean) -> Unit)

    fun getFileNameFromUri(
        context: Context,
        imageUri: Uri
    ): String?
}