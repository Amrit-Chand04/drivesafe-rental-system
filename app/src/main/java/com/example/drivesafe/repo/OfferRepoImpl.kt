package com.example.drivesafe.repo

import com.example.drivesafe.model.OfferModel
import com.example.drivesafe.model.VehicleFirebaseModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class OfferRepoImpl(
    private val notificationRepo: NotificationRepo = NotificationRepoImpl()
) : OfferRepo {

    private val offerRef: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("offers")

    private val activeOfferRef: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("activeOffer")

    private val vehicleRef: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("vehicles")

    override fun createOffer(
        model: OfferModel,
        callback: (Boolean, String) -> Unit
    ) {
        val id = offerRef.push().key ?: ""
        model.id = id
        model.createdAt = System.currentTimeMillis()

        offerRef.child(id).setValue(model).addOnCompleteListener {
            if (it.isSuccessful) {
                notificationRepo.addNotification(
                    title = "New Offer: ${model.title}",
                    message = "${model.description} (${model.discount}% OFF)"
                )
                callback(true, "Offer created successfully")
            } else {
                callback(false, "${it.exception?.message}")
            }
        }
    }

    override fun getOffers(callback: (Boolean, String, List<OfferModel>?) -> Unit) {

        offerRef.get()
            .addOnSuccessListener { snapshot ->

                try {
                    val list = snapshot.children.mapNotNull { snap ->
                        snap.getValue(OfferModel::class.java)
                    }

                    callback(true, "Loaded", list)
                } catch (e: Exception) {
                    callback(false, "Parse crash: ${e.message}", null)
                }
            }
            .addOnFailureListener { error ->
                callback(false, error.message ?: "Error", null)
            }
    }

    override fun deleteOffer(
        id: String,
        callback: (Boolean, String) -> Unit
    ) {
        offerRef.child(id).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Offer deleted succesfully")
            } else {
                callback(false, "${it.exception?.message}")
            }
        }
    }

    override fun setActiveOffer(discount: Int, callback: (Boolean) -> Unit) {
        activeOfferRef.child("discount").setValue(discount)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    override fun clearActiveOffer(callback: (Boolean) -> Unit) {
        activeOfferRef.removeValue()
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    override fun applyOfferToVehicles(discount: Int, callback: (Boolean) -> Unit) {
        vehicleRef.get().addOnSuccessListener { snapshot ->
            val vehicles = snapshot.children.mapNotNull {
                it.getValue(VehicleFirebaseModel::class.java)
            }
            if (vehicles.isEmpty()) {
                callback(true)
                return@addOnSuccessListener
            }
            var remaining = vehicles.size
            var allSuccess = true
            vehicles.forEach { vehicle ->
                val originalPrice = vehicle.price.filter { it.isDigit() }.toIntOrNull() ?: 0
                val discounted = originalPrice - (originalPrice * discount / 100)
                vehicleRef.child(vehicle.vehicleId).updateChildren(
                    mapOf("offerPercentage" to discount, "discountedPrice" to discounted)
                ).addOnCompleteListener { task ->
                    if (!task.isSuccessful) allSuccess = false
                    remaining--
                    if (remaining == 0) callback(allSuccess)
                }
            }
        }.addOnFailureListener { callback(false) }
    }

    override fun clearOfferFromVehicles(callback: (Boolean) -> Unit) {
        vehicleRef.get().addOnSuccessListener { snapshot ->
            val vehicles = snapshot.children.mapNotNull {
                it.getValue(VehicleFirebaseModel::class.java)
            }
            if (vehicles.isEmpty()) {
                callback(true)
                return@addOnSuccessListener
            }
            var remaining = vehicles.size
            var allSuccess = true
            vehicles.forEach { vehicle ->
                vehicleRef.child(vehicle.vehicleId).updateChildren(
                    mapOf("offerPercentage" to 0, "discountedPrice" to 0)
                ).addOnCompleteListener { task ->
                    if (!task.isSuccessful) allSuccess = false
                    remaining--
                    if (remaining == 0) callback(allSuccess)
                }
            }
        }.addOnFailureListener { callback(false) }
    }

}