package com.example.drivesafe.repo

import com.example.drivesafe.model.BookingModel

interface BookingRepo {
    fun addBooking(booking: BookingModel, callback: (Boolean, String) -> Unit)
    fun getMyBookings(callback: (Boolean, String, List<BookingModel>) -> Unit)
    fun getAllBookings(callback: (Boolean, String, List<BookingModel>) -> Unit)
    fun updateBookingStatus(
        bookingId: String,
        status: String,
        rejectionReason: String = "",
        callback: (Boolean, String) -> Unit
    )
}