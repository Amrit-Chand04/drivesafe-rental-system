package com.example.drivesafe.model

data class OfferModel(
    var id: String = "",
    var title: String = "",
    var description: String = "",
    var discount: Int = 0,
    var startDate: String = "",
    var endDate: String = "",
    var createdAt: Long = 0L
)