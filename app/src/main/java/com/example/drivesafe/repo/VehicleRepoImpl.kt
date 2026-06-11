package com.example.drivesafe.repo

import com.example.drivesafe.model.VehicleModel
import com.google.firebase.database.*

class VehicleRepoImpl : VehicleRepo {

    private val vehicleRef = FirebaseDatabase.getInstance().reference.child("vehicles")

    override fun addVehicle(vehicle: VehicleModel, callback: (Boolean, String) -> Unit) {
        val id = vehicleRef.push().key ?: ""

        vehicle.vehicleId = id

        vehicleRef.child(id).setValue(vehicle)
            .addOnSuccessListener {
                callback(true, "Vehicle added successfully")
            }
             .addOnFailureListener {
                callback(false, it.message ?: "Failed to add vehicle")
            }
    }

    override fun getVehicles(callback: (Boolean, String, List<VehicleModel>) -> Unit) {
        vehicleRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<VehicleModel>()

                for (data in snapshot.children) {
                    val vehicle = data.getValue(VehicleModel::class.java)
                    if (vehicle != null) {
                        list.add(vehicle)
                    }
                }

                callback(true, "Loaded", list)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, emptyList())
            }
        })
    }

    override fun updateVehicle(
        id: String,
        vehicle: VehicleModel,
        callback: (Boolean, String) -> Unit
    ) {
        vehicleRef.child(id).setValue(vehicle)
            .addOnSuccessListener {
                callback(true, "Vehicle updated successfully")
            }
            .addOnFailureListener {
                callback(false, it.message ?: "Update failed")
            }
    }

    override fun deleteVehicle(
        id: String,
        callback: (Boolean, String) -> Unit
    ) {
        vehicleRef.child(id).removeValue()
            .addOnSuccessListener {
                callback(true, "Vehicle deleted successfully")
            }
            .addOnFailureListener {
                callback(false, it.message ?: "Delete failed")
            }
    }
}