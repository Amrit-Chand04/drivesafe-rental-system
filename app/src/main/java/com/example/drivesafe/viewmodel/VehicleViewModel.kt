package com.example.drivesafe.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.drivesafe.model.VehicleFirebaseModel
import com.example.drivesafe.model.VehicleModel
import com.example.drivesafe.repo.VehicleRepo
import com.example.drivesafe.repo.VehicleRepoImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class VehicleViewModel(application: Application) : AndroidViewModel(application) {

    private val repo: VehicleRepo = VehicleRepoImpl(application.applicationContext)
    private val _vehicles = MutableStateFlow<List<VehicleFirebaseModel>>(emptyList())
    val vehicles = _vehicles.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun addVehicle(vehicle: VehicleModel, callback: (Boolean, String) -> Unit) {

        _isLoading.value = true

        repo.addVehicle(vehicle) { success, message ->

            _isLoading.value = false

            callback(success, message)
        }
    }
    // amrir chand thakuri this is  one
    fun getVehicles(callback: (Boolean, String, List<VehicleFirebaseModel>) -> Unit) {

        _isLoading.value = true

        repo.getVehicles { success, message, list ->

            _isLoading.value = false

            if (success) {
                _vehicles.value = list
            }

            callback(success, message, list)
        }
    }

    fun getVehicleById(
        vehicleId: String,
        callback: (Boolean, String, VehicleFirebaseModel?) -> Unit
    ) {
        if (vehicleId.isBlank()) {
            callback(false, "Invalid vehicle id", null)
            return
        }

        repo.getVehicles { success, message, list ->
            if (!success) {
                callback(false, message, null)
                return@getVehicles
            }

            val vehicle = list.firstOrNull { it.vehicleId == vehicleId }

            callback(true, message,vehicle )
        }
    }

    fun updateVehicle(vehicle: VehicleFirebaseModel, callback: (Boolean, String) -> Unit) {

        _isLoading.value = true

        repo.updateVehicle(vehicle) { success, message ->

            _isLoading.value = false

            callback(success, message)
        }
    }

    fun deleteVehicle(id: String, callback: (Boolean, String) -> Unit) {

        repo.deleteVehicle(id) { success, message ->
            if (success) {
                _vehicles.value =
                    _vehicles.value.filter { it.vehicleId != id }
            }
            callback(success, message)
        }
    }
}