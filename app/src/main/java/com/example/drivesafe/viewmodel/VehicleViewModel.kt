package com.example.drivesafe.viewmodel

import androidx.lifecycle.ViewModel
import com.example.drivesafe.model.VehicleModel
import com.example.drivesafe.repo.VehicleRepo
import com.example.drivesafe.repo.VehicleRepoImpl

class VehicleViewModel : ViewModel() {

    private val repo: VehicleRepo = VehicleRepoImpl()

    fun addVehicle(vehicle: VehicleModel, callback: (Boolean, String) -> Unit) {
        repo.addVehicle(vehicle, callback)
    }

    fun getVehicles(callback: (Boolean, String, List<VehicleModel>) -> Unit) {
        repo.getVehicles(callback)
    }

    fun updateVehicle(id: String, vehicle: VehicleModel, callback: (Boolean, String) -> Unit) {
        repo.updateVehicle(id, vehicle, callback)
    }

    fun deleteVehicle(id: String, callback: (Boolean, String) -> Unit) {
        repo.deleteVehicle(id, callback)
    }
}