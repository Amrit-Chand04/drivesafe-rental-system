package com.example.drivesafe.repo

import com.f1soft.esewapaymentsdk.EsewaConfiguration
import com.f1soft.esewapaymentsdk.EsewaPayment

interface EsewaRepo {
    fun buildConfiguration(): EsewaConfiguration
    fun buildPayment(amount: String, productName: String, productId: String): EsewaPayment
}
