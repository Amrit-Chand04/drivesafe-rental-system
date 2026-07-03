package com.example.drivesafe.repo

import com.khalti.checkout.data.KhaltiPayConfig

interface KhaltiRepo {
    /** Calls Khalti's Initiate Payment API and returns the pidx, or null on failure. */
    suspend fun initiatePayment(amount: Int, purchaseOrderId: String, purchaseOrderName: String): String?

    fun buildConfig(pidx: String): KhaltiPayConfig
}
