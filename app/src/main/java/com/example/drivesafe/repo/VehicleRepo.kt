package com.example.drivesafe.repo

import android.content.Context
import android.net.Uri
import com.example.drivesafe.model.VehicleFirebaseModel
import com.example.drivesafe.model.VehicleModel

interface VehicleRepo {
    fun addVehicle(vehicle: VehicleModel, callback: (Boolean, String) -> Unit)
    fun getVehicles(callback: (Boolean, String, List<VehicleFirebaseModel>) -> Unit)

    fun getVehicleById(
        vehicleId: String,
        callback: (Boolean, String, VehicleFirebaseModel?) -> Unit
    )
    fun updateVehicle(vehicle: VehicleFirebaseModel, callback: (Boolean, String) -> Unit)
    fun deleteVehicle(id: String, callback: (Boolean, String) -> Unit)

    fun getFileNameFromUri(
        context: Context,
        imageUri: Uri
    ): String?
}