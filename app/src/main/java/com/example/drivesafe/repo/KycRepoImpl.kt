package com.example.drivesafe.repo

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import android.util.Log
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.example.drivesafe.model.KycFirebaseModel
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.Executors

class KycRepoImpl(
    private val context: Context
) : KycRepo {

    private val cloudinary = Cloudinary(
        mapOf(
            "cloud_name" to "drivesafe",
            "api_key" to "212521171331851",
            "api_secret" to "jxZetRVlPIrjbcsQV0gWHAkXn3Y"
        )
    )

    private val database = FirebaseDatabase.getInstance().reference

    override fun submitKyc1(
        name: String,
        phone: String,
        doc: Uri,
        photo: Uri,
        callback: (Boolean, String) -> Unit
    ) {

        Executors.newSingleThreadExecutor().execute {

            try {

                // Upload License Document
                val docStream =
                    context.contentResolver.openInputStream(doc)

                if (docStream == null) {
                    postResult(callback, false, "Unable to read licence file")
                    return@execute
                }

                var docName =
                    getFileNameFromUri(context, doc)

                docName =
                    docName?.substringBeforeLast(".")
                        ?: "kyc_doc"

                val docResponse =
                    cloudinary.uploader().upload(
                        docStream,
                        ObjectUtils.asMap(
                            "public_id", docName,
                            "resource_type", "auto"
                        )
                    )

                val docUrl =
                    docResponse["secure_url"] as? String

                if (docUrl.isNullOrEmpty()) {
                    postResult(callback, false, "Licence upload failed")
                    return@execute
                }

                // Upload User Photo
                val photoStream =
                    context.contentResolver.openInputStream(photo)

                if (photoStream == null) {
                    postResult(callback, false, "Unable to read photo file")
                    return@execute
                }

                var photoName =
                    getFileNameFromUri(context, photo)

                photoName =
                    photoName?.substringBeforeLast(".")
                        ?: "kyc_photo"

                val photoResponse =
                    cloudinary.uploader().upload(
                        photoStream,
                        ObjectUtils.asMap(
                            "public_id", photoName,
                            "resource_type", "image"
                        )
                    )

                val photoUrl =
                    photoResponse["secure_url"] as? String

                if (photoUrl.isNullOrEmpty()) {
                    postResult(callback, false, "Photo upload failed")
                    return@execute
                }

                val kycId = database
                    .child("kyc")
                    .push()
                    .key

                if (kycId == null) {
                    postResult(
                        callback,
                        false,
                        "Failed to generate KYC ID"
                    )
                    return@execute
                }

                // Save KYC data to Firebase

                val kycData = KycFirebaseModel(
                    name = name,
                    phone = phone,
                    doc = docUrl,
                    photo = photoUrl
                )

                database
                    .child("kyc")
                    .child(kycId)
                    .setValue(kycData)
                    .addOnSuccessListener {

                        postResult(
                            callback,
                            true,
                            "KYC Submitted Successfully"
                        )
                    }
                    .addOnFailureListener { e ->

                        Log.e(
                            "FirebaseDB",
                            "KYC save failed",
                            e
                        )

                        postResult(
                            callback,
                            false,
                            "Firebase save failed: ${e.message}"
                        )
                    }

            } catch (e: Exception) {

                e.printStackTrace()

                postResult(
                    callback,
                    false,
                    e.message ?: "Submission Failed"
                )
            }
        }
    }

    private fun postResult(
        callback: (Boolean, String) -> Unit,
        success: Boolean,
        message: String
    ) {
        Handler(Looper.getMainLooper()).post {
            callback(success, message)
        }
    }

    override fun getFileNameFromUri(
        context: Context,
        imageUri: Uri
    ): String? {

        var fileName: String? = null

        val cursor: Cursor? =
            context.contentResolver.query(
                imageUri,
                null,
                null,
                null,
                null
            )

        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex =
                    it.getColumnIndex(OpenableColumns.DISPLAY_NAME)

                if (nameIndex != -1) {
                    fileName = it.getString(nameIndex)
                }
            }
        }

        return fileName
    }
}