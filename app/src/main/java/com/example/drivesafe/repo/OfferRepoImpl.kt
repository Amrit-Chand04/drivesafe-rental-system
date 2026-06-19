package com.example.drivesafe.repo

import com.example.drivesafe.model.OfferModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class OfferRepoImpl : OfferRepo{

    private val offerRef: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("offers")

    override fun createOffer(
        model: OfferModel,
        callback: (Boolean, String) -> Unit
    ) {
        val id = offerRef.push().key ?: ""
        model.id = id

        offerRef.child(id).setValue(model).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Offer created successfully")
            } else {
                callback(false, "${it.exception?.message}")
            }
        }
    }


}