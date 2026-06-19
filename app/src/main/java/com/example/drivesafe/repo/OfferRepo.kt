package com.example.drivesafe.repo

import com.example.drivesafe.model.OfferModel


interface OfferRepo {

    fun createOffer(
        model: OfferModel,
        callback: (Boolean, String) -> Unit
    )
}