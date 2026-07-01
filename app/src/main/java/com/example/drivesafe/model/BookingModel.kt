package com.example.drivesafe.model


data class BookingModel(
    val userId: String = "",
    val bookingId: String = "",
    val fullName: String = "",
    val phoneNumber: String = "",
    val rentalPlan: String = "",
    val pickupDate: String = "",
    val pickupTime: String = "",
    val duration: String = "",
    val vehicleId: String = "",
    val vehicleName: String = "",
    val vehicleImage: String = "",
    val vehiclePrice: String = "",
    val status: String = "PENDING",
    val paymentStatus: String = "PAID_FAKE"
)
