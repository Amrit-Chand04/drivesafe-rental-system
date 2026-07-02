package com.example.drivesafe.repo

import android.content.ContentValues
import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.provider.MediaStore
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
            bookingId = bookingId,
            email = auth.currentUser?.email ?: ""
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

    override fun generateBookingPdf(
        context: Context,
        booking: BookingModel,
        callback: (Boolean, String) -> Unit
    ) {
        try {
            val fileName = "Booking_${booking.bookingId.ifBlank { System.currentTimeMillis().toString() }}.pdf"

            val pdfDocument = PdfDocument()
            val page = pdfDocument.startPage(PdfDocument.PageInfo.Builder(595, 842, 1).create())
            val canvas = page.canvas

            val titlePaint = Paint().apply { textSize = 18f; isFakeBoldText = true }
            val textPaint = Paint().apply { textSize = 13f }

            var y = 50f
            canvas.drawText("VEHICLE BOOKING RECEIPT", 40f, y, titlePaint)
            y += 30f
            canvas.drawText("Booking ID: ${booking.bookingId}", 40f, y, textPaint); y += 20f
            canvas.drawText("Booking Date: ${booking.bookingDate}", 40f, y, textPaint); y += 20f
            canvas.drawText("Status: ${booking.status}", 40f, y, textPaint); y += 20f
            canvas.drawText("Name: ${booking.fullName}", 40f, y, textPaint); y += 20f
            canvas.drawText("Phone: ${booking.phoneNumber}", 40f, y, textPaint); y += 20f
            canvas.drawText("Email: ${booking.email}", 40f, y, textPaint); y += 20f
            canvas.drawText("Vehicle Name: ${booking.vehicleName}", 40f, y, textPaint); y += 20f
            canvas.drawText("Vehicle Number: ${booking.vehicleNumber}", 40f, y, textPaint); y += 20f
            canvas.drawText("Rental Plan: ${booking.rentalPlan}", 40f, y, textPaint); y += 20f
            canvas.drawText("Pickup Location: ${booking.pickupLocation}", 40f, y, textPaint); y += 20f
            canvas.drawText("Pickup Date & Time: ${booking.pickupDate} ${booking.pickupTime}", 40f, y, textPaint); y += 20f
            canvas.drawText("Duration: ${booking.duration}", 40f, y, textPaint); y += 20f
            canvas.drawText("Total Paid: Rs. ${booking.vehiclePrice}", 40f, y, textPaint)

            pdfDocument.finishPage(page)

            val resolver = context.contentResolver
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)

            if (uri == null) {
                pdfDocument.close()
                callback(false, "Failed to save PDF")
                return
            }

            resolver.openOutputStream(uri)?.use { pdfDocument.writeTo(it) }
            pdfDocument.close()

            callback(true, "Downloaded to Downloads/$fileName")
        } catch (e: Exception) {
            callback(false, "Failed to generate PDF: ${e.message}")
        }
    }
}