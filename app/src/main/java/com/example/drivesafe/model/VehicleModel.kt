package com.example.drivesafe.model

import android.net.Uri

data class VehicleModel(
    var vehicleId: String = "",
    var name: String = "",
    var type: String = "",
    var number: String = "",
    var brand: String = "",
    var status: String = "",
    var vehicleImage: Uri? = null,
    var price: String = "",
    var description: String = "",
    var capacity: String = "",
    var engine: String = "",
    var speed: String = "",
    var fuelType: String = "",
)