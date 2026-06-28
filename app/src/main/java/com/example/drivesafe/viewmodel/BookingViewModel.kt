package com.example.drivesafe.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BookingViewModel : ViewModel() {

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    private val _estimatedPrice = MutableStateFlow<Double?>(null)
    val estimatedPrice: StateFlow<Double?> = _estimatedPrice.asStateFlow()

    fun calculatePrice(perDayPrice: String, rentalPlan: String, duration: String) {
        val price = perDayPrice.toDoubleOrNull()
        val dur = duration.toIntOrNull()
        _estimatedPrice.value = if (price != null && dur != null && dur > 0) {
            when (rentalPlan) {
                "Hourly" -> (price / 24.0) * dur
                "Daily" -> price * dur
                else -> null
            }
        } else null
    }

    fun validateAndBook(
        fullName: String,
        phoneNumber: String,
        rentalPlan: String,
        pickupDate: String,
        pickupTime: String,
        duration: String
    ) {
        when {
            fullName.isBlank() -> _message.value = "Enter Full Name"
            phoneNumber.isBlank() -> _message.value = "Enter Phone Number"
            pickupDate.isBlank() -> _message.value = "Select Pickup Date"
            pickupTime.isBlank() -> _message.value = "Select Pickup Time"
            duration.isBlank() -> _message.value = "Enter Duration"
            else -> _message.value = "Booking Successful"
        }
    }

    fun clearMessage() {
        _message.value = null
    }
}
