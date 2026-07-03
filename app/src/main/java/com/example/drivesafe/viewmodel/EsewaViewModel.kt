package com.example.drivesafe.viewmodel

import androidx.lifecycle.ViewModel
import com.example.drivesafe.repo.EsewaRepo
import com.example.drivesafe.repo.EsewaRepoImpl
import com.f1soft.esewapaymentsdk.EsewaConfiguration
import com.f1soft.esewapaymentsdk.EsewaPayment

class EsewaViewModel(private val repo: EsewaRepo = EsewaRepoImpl()) : ViewModel() {

    fun buildConfiguration(): EsewaConfiguration = repo.buildConfiguration()

    fun buildPayment(amount: String, productName: String, productId: String): EsewaPayment =
        repo.buildPayment(amount, productName, productId)
}
