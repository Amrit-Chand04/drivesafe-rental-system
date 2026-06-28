package com.example.drivesafe.model

data class VehicleFirebaseModel(
    var vehicleId: String = "",
    var name: String = "",
    var type: String = "",
    var fuelType: String = "",
    var number: String = "",
    var status: String = "",
    var vehicleImage: String = "",
    var price: String = "",
    var description: String = "",
    var capacity: String = "",
    var engine: String = "",
    var speed: String = "",
    var offerId: String = "",
    var offerPercentage: Int = 0,
    var discountedPrice: Int = 0
)