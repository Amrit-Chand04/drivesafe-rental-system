package com.example.drivesafe.repo

import com.example.drivesafe.model.OfferModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class OfferRepoImpl : OfferRepo{

    private val offerRef: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("offers")

    private val activeOfferRef: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("activeOffer")

    override fun createOffer(
        model: OfferModel,
        callback: (Boolean, String) -> Unit
    ) {
        val id = offerRef.push().key ?: ""
        model.id = id
        model.createdAt = System.currentTimeMillis()

        offerRef.child(id).setValue(model).addOnCompleteListener {
            if (it.isSuccessful) {
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

}