package com.example.drivesafe.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.drivesafe.model.BookingModel
import com.example.drivesafe.repo.BookingRepo
import com.example.drivesafe.repo.BookingRepoImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BookingViewModel : ViewModel() {

    private val repo: BookingRepo = BookingRepoImpl()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    private val _bookings = MutableStateFlow<List<BookingModel>>(emptyList())
    val bookings: StateFlow<List<BookingModel>> = _bookings.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

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

    fun validate(
        fullName: String,
        phoneNumber: String,
        pickupLocation: String,
        pickupDate: String,
        pickupTime: String,
        duration: String
    ): Boolean {
        val error = when {
            fullName.isBlank() -> "Enter Full Name"
            phoneNumber.isBlank() -> "Enter Phone Number"
            pickupLocation.isBlank() -> "Enter Pickup Location"
            pickupDate.isBlank() -> "Select Pickup Date"
            pickupTime.isBlank() -> "Select Pickup Time"
            duration.isBlank() -> "Enter Duration"
            else -> null
        }

        _message.value = error
        return error == null
    }

    fun confirmBooking(
        fullName: String,
        phoneNumber: String,
        pickupLocation: String,
        rentalPlan: String,
        pickupDate: String,
        pickupTime: String,
        duration: String,
        vehicleId: String,
        vehicleName: String,
        vehicleImage: String,
        vehiclePrice: String,
        vehicleNumber: String,
        transactionRefId: String
    ) {
        val booking = BookingModel(
            userId = "",
            fullName = fullName,
            phoneNumber = phoneNumber,
            pickupLocation = pickupLocation,
            rentalPlan = rentalPlan,
            pickupDate = pickupDate,
            pickupTime = pickupTime,
            duration = duration,
            vehicleId = vehicleId,
            vehicleName = vehicleName,
            vehicleImage = vehicleImage,
            vehiclePrice = vehiclePrice,
            vehicleNumber = vehicleNumber,
            bookingDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
            status = "PENDING",
            paymentStatus = "PAID",
            transactionRefId = transactionRefId
        )

        repo.addBooking(booking) { success, msg ->
            _message.value = if (success) "Booking Confirmed" else "Booking failed: $msg"
        }
    }

    fun clearMessage() {
        _message.value = null
    }

    fun generateBookingPdf(context: Context, booking: BookingModel) {
        repo.generateBookingPdf(context, booking) { success, msg ->
            _message.value = msg
        }
    }

    fun loadMyBookings() {
        _isLoading.value = true
        repo.getMyBookings { success, msg, list ->
            _isLoading.value = false
            if (success) {
                _bookings.value = list
            } else {
                _bookings.value = emptyList()
                _message.value = msg
            }
        }
    }

    fun loadAllBookings() {
        _isLoading.value = true
        repo.getAllBookings { success, msg, list ->
            _isLoading.value = false
            if (success) {
                _bookings.value = list
            } else {
                _bookings.value = emptyList()
                _message.value = msg
            }
        }
    }

    fun updateBookingStatus(bookingId: String, status: String, rejectionReason: String = "") {
        repo.updateBookingStatus(bookingId, status, rejectionReason) { success, msg ->
            _message.value = msg
            if (success) {
                loadAllBookings()
            }
        }
    }
}