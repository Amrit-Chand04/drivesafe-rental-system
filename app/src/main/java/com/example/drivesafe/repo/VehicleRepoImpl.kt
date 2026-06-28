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
import com.example.drivesafe.model.VehicleFirebaseModel
import com.example.drivesafe.model.VehicleModel
import com.google.firebase.database.*
import java.util.concurrent.Executors

class VehicleRepoImpl(private val context: Context) : VehicleRepo {

    private val cloudinary = Cloudinary(
        mapOf(
            "cloud_name" to "drivesafe",
            "api_key" to "212521171331851",
            "api_secret" to "jxZetRVlPIrjbcsQV0gWHAkXn3Y"
        )
    )

    private val vehicleRef = FirebaseDatabase.getInstance().reference.child("vehicles")

    override fun addVehicle(vehicle: VehicleModel, callback: (Boolean, String) -> Unit) {
        Executors.newSingleThreadExecutor().execute {
            try {
                val imageUri = vehicle.vehicleImage
                if (imageUri == null) {
                    postResult(callback, false, "Please select a vehicle image")
                    return@execute
                }

                val imageStream = context.contentResolver.openInputStream(imageUri)
                if (imageStream == null) {
                    postResult(callback, false, "Unable to read vehicle image")
                    return@execute
                }

                var imageName = getFileNameFromUri(context, imageUri)
                imageName = imageName?.substringBeforeLast(".") ?: "vehicle_image"

                val imageResponse = cloudinary.uploader().upload(
                    imageStream,
                    ObjectUtils.asMap(
                        "public_id", imageName,
                        "resource_type", "image"
                    )
                )

                val imageUrl = imageResponse["secure_url"] as? String
                if (imageUrl.isNullOrEmpty()) {
                    postResult(callback, false, "Vehicle image upload failed")
                    return@execute
                }

                val vehicleId = vehicleRef.push().key
                if (vehicleId == null) {
                    postResult(callback, false, "Failed to generate vehicle ID")
                    return@execute
                }

                val vehicleData = VehicleFirebaseModel(
                    vehicleId = vehicleId,
                    name = vehicle.name,
                    type = vehicle.type,
                    fuelType = vehicle.fuelType,
                    number = vehicle.number,
                    status = vehicle.status,
                    vehicleImage = imageUrl,
                    price = vehicle.price,
                    description = vehicle.description,
                    capacity = vehicle.capacity,
                    engine = vehicle.engine,
                    speed = vehicle.speed
                )

                vehicleRef.child(vehicleId).setValue(vehicleData)
                    .addOnSuccessListener {
                        postResult(callback, true, "Vehicle Added Successfully")
                    }
                    .addOnFailureListener { e ->
                        Log.e("FirebaseDB", "Vehicle save failed", e)
                        postResult(callback, false, "Firebase save failed: ${e.message}")
                    }

            } catch (e: Exception) {
                e.printStackTrace()
                postResult(callback, false, e.message ?: "Vehicle Add Failed")
            }
        }
    }

    override fun getVehicles(callback: (Boolean, String, List<VehicleFirebaseModel>) -> Unit) {
        vehicleRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val vehicles = snapshot.children.mapNotNull {
                    it.getValue(VehicleFirebaseModel::class.java)
                }
                callback(true, "Loaded", vehicles)
            }
            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, emptyList())
            }
        })
    }

    override fun getVehicleById(
        vehicleId: String,
        callback: (Boolean, String, VehicleFirebaseModel?) -> Unit
    ) {
        vehicleRef.child(vehicleId)
            .get()
            .addOnSuccessListener { snapshot ->

                if (snapshot.exists()) {

                    val vehicle = snapshot.getValue(VehicleFirebaseModel::class.java)

                    callback(true, "Vehicle Loaded", vehicle)

                } else {

                    callback(false, "Vehicle not found", null)
                }
            }
            .addOnFailureListener {

                callback(false, it.message ?: "Failed to load vehicle", null)
            }
    }

    override fun updateVehicle(
        vehicle: VehicleFirebaseModel,
        callback: (Boolean, String) -> Unit
    ) {

        val currentVehicleId = vehicle.vehicleId

        if (currentVehicleId.isNullOrEmpty()) {
            callback(false, "Invalid Vehicle ID for update")
            return
        }

        Executors.newSingleThreadExecutor().execute {

            try {

                val imageUrl = vehicle.vehicleImage


                val finalImageUrl = if (
                    imageUrl != null &&
                    (imageUrl.startsWith("http://") ||
                            imageUrl.startsWith("https://"))
                ) {

                    // keep old cloudinary image
                    imageUrl

                } else if (imageUrl != null) {

                    // new image selected
                    val imageUri = Uri.parse(imageUrl)

                    val imageStream =
                        context.contentResolver.openInputStream(imageUri)

                    if (imageStream == null) {
                        postResult(
                            callback,
                            false,
                            "Unable to read new image"
                        )
                        return@execute
                    }


                    var imageName =
                        getFileNameFromUri(context, imageUri)

                    imageName =
                        imageName?.substringBeforeLast(".")
                            ?: "vehicle_image"


                    val response =
                        cloudinary.uploader().upload(
                            imageStream,
                            ObjectUtils.asMap(
                                "public_id",
                                imageName,
                                "resource_type",
                                "image"
                            )
                        )


                    response["secure_url"] as? String


                } else {

                    // no image change
                    null
                }



                val vehicleData = VehicleFirebaseModel(

                    vehicleId = currentVehicleId,

                    name = vehicle.name,

                    type = vehicle.type,

                    fuelType = vehicle.fuelType,

                    number = vehicle.number,

                    status = vehicle.status,


                    // keep old image if no new image
                    vehicleImage =
                        finalImageUrl ?: "",


                    price = vehicle.price,

                    description = vehicle.description,

                    capacity = vehicle.capacity,

                    engine = vehicle.engine,

                    speed = vehicle.speed
                )


                vehicleRef
                    .child(currentVehicleId)
                    .setValue(vehicleData)

                    .addOnSuccessListener {

                        postResult(
                            callback,
                            true,
                            "Vehicle Updated Successfully"
                        )
                    }

                    .addOnFailureListener { e ->

                        postResult(
                            callback,
                            false,
                            e.message ?: "Update failed"
                        )
                    }


            } catch (e: Exception) {

                e.printStackTrace()

                postResult(
                    callback,
                    false,
                    e.message ?: "Vehicle Update Failed"
                )
            }
        }
    }

    override fun deleteVehicle(id: String, callback: (Boolean, String) -> Unit) {

        vehicleRef.child(id).removeValue()
            .addOnSuccessListener {
                callback(true, "Vehicle deleted successfully")
            }
            .addOnFailureListener {
                callback(false, it.message ?: "Delete failed")
            }
    }

    override fun getFileNameFromUri(context: Context, imageUri: Uri): String? {
        var fileName: String? = null
        val cursor: Cursor? = context.contentResolver.query(imageUri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = it.getString(nameIndex)
                }
            }
        }
        return fileName
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
}