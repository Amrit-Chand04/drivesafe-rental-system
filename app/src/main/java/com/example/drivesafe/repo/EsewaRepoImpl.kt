package com.example.drivesafe.repo

import com.example.drivesafe.model.EsewaConfig
import com.f1soft.esewapaymentsdk.EsewaConfiguration
import com.f1soft.esewapaymentsdk.EsewaPayment

class EsewaRepoImpl : EsewaRepo {

    override fun buildConfiguration(): EsewaConfiguration {
        return EsewaConfiguration(
            EsewaConfig.CLIENT_ID,
            EsewaConfig.SECRET_KEY,
            EsewaConfiguration.ENVIRONMENT_TEST
        )
    }

    override fun buildPayment(amount: String, productName: String, productId: String): EsewaPayment {
        return EsewaPayment(
            amount,
            productName,
            productId,
            EsewaConfig.CALLBACK_URL
        )
    }
}
