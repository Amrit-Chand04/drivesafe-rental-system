package com.example.drivesafe.repo

import com.example.drivesafe.model.OfferModel


interface OfferRepo {

    fun createOffer(
        model: OfferModel,
        callback: (Boolean, String) -> Unit
    )

    fun getOffers(
        callback: (Boolean, String, List<OfferModel>?) -> Unit
    )

    fun deleteOffer(
        id: String,
        callback: (Boolean, String) -> Unit
    )

    fun setActiveOffer(discount: Int, callback: (Boolean) -> Unit)

    fun clearActiveOffer(callback: (Boolean) -> Unit)
}