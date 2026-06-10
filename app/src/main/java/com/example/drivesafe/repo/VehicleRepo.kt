package com.example.drivesafe.repo

import com.example.drivesafe.model.VehicleModel

interface VehicleRepo {
    fun addVehicle(vehicle: VehicleModel, callback: (Boolean, String) -> Unit)
    fun getVehicles(callback: (Boolean, String, List<VehicleModel>) -> Unit)
    fun updateVehicle(id: String, vehicle: VehicleModel, callback: (Boolean, String) -> Unit)
    fun deleteVehicle(id: String, callback: (Boolean, String) -> Unit)
}