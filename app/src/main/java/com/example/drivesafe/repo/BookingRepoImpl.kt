package com.example.drivesafe.repo

import com.example.drivesafe.model.BookingModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BookingRepoImpl : BookingRepo {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().reference

    override fun addBooking(
        booking: BookingModel,
        callback: (Boolean, String) -> Unit
    ) {
        val uid = auth.currentUser?.uid
        if (uid.isNullOrBlank()) {
            callback(false, "User not logged in")
            return
        }

        val bookingRef = db.child("bookings").push()
        val bookingId = bookingRef.key
        if (bookingId.isNullOrBlank()) {
            callback(false, "Unable to create booking id")
            return
        }

        val bookingWithId = booking.copy(
            userId = uid,
            bookingId = bookingId
        )

        bookingRef
            .setValue(bookingWithId)
            .addOnSuccessListener {
                callback(true, "Booking Saved")
            }
            .addOnFailureListener { error ->
                callback(false, error.localizedMessage ?: error.toString())
            }
    }

    override fun getMyBookings(callback: (Boolean, String, List<BookingModel>) -> Unit) {
        val uid = auth.currentUser?.uid
        if (uid.isNullOrBlank()) {
            callback(false, "User not logged in", emptyList())
            return
        }

        db.child("bookings")
            .orderByChild("userId")
            .equalTo(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val bookings = snapshot.children.mapNotNull { child ->
                        child.getValue(BookingModel::class.java)?.copy(
                            bookingId = child.key ?: "",
                            userId = uid
                        )
                    }
                    callback(
                        true,
                        if (bookings.isEmpty()) "No bookings found" else "Bookings loaded",
                        bookings
                    )
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message, emptyList())
                }
            })
    }

    override fun getAllBookings(callback: (Boolean, String, List<BookingModel>) -> Unit) {
        db.child("bookings")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val bookings = snapshot.children.mapNotNull { child ->
                        child.getValue(BookingModel::class.java)?.copy(
                            bookingId = child.key ?: ""
                        )
                    }
                    callback(
                        true,
                        if (bookings.isEmpty()) "No bookings found" else "Bookings loaded",
                        bookings
                    )
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message, emptyList())
                }
            })
    }

    override fun updateBookingStatus(
        bookingId: String,
        status: String,
        rejectionReason: String,
        callback: (Boolean, String) -> Unit
    ) {
        if (bookingId.isBlank()) {
            callback(false, "Invalid booking id")
            return
        }

        val updates = mutableMapOf<String, Any>("status" to status)
        if (status.equals("REJECTED", ignoreCase = true)) {
            updates["rejectionReason"] = rejectionReason
        } else {
            updates["rejectionReason"] = ""
        }

        db.child("bookings")
            .child(bookingId)
            .updateChildren(updates)
            .addOnSuccessListener {
                callback(true, "Booking $status")
            }
            .addOnFailureListener { error ->
                callback(false, error.localizedMessage ?: "Failed to update booking")
            }
    }
}

