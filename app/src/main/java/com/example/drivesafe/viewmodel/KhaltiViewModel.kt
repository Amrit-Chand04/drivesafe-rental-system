package com.example.drivesafe.viewmodel

import androidx.lifecycle.ViewModel
import com.example.drivesafe.repo.KhaltiRepo
import com.example.drivesafe.repo.KhaltiRepoImpl
import com.khalti.checkout.data.KhaltiPayConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class KhaltiViewModel(private val repo: KhaltiRepo = KhaltiRepoImpl()) : ViewModel() {

    fun startPayment(
        amount: Int,
        purchaseOrderId: String,
        purchaseOrderName: String,
        onResult: (KhaltiPayConfig?) -> Unit
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            val pidx = repo.initiatePayment(amount, purchaseOrderId, purchaseOrderName)
            onResult(pidx?.let { repo.buildConfig(it) })
        }
    }
}
