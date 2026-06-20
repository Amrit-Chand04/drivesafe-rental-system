package com.example.drivesafe.viewmodel

import androidx.lifecycle.ViewModel
import com.example.drivesafe.model.OfferModel
import com.example.drivesafe.repo.OfferRepo
import com.example.drivesafe.repo.OfferRepoImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OfferViewModel(
    private val repo: OfferRepo = OfferRepoImpl()
) : ViewModel() {
    private val _toast = MutableStateFlow<String?>(null)
    val toast: StateFlow<String?> = _toast

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val today = sdf.parse(sdf.format(Date()))

    fun createOffer(model: OfferModel, callback: (Boolean, String) -> Unit) {

        val start = sdf.parse(model.startDate)
        val end = sdf.parse(model.endDate)

        when {
            model.title.isBlank() -> _toast.value = "Enter title"
            model.description.isBlank() -> _toast.value = "Enter description"
            model.discount <= 0 -> _toast.value = "Enter valid discount"
            model.discount > 100 -> _toast.value = "Discount cannot exceed 100%"
            model.startDate.isBlank() -> _toast.value = "Select start date"
            model.endDate.isBlank() -> _toast.value = "Select end date"
            start!!.before(today) ->
                _toast.value = "Start date cannot be in the past"

            end!!.before(start) ->
                _toast.value = "End date must be after start date"


            else -> {
                _isLoading.value = true

                repo.createOffer(model) { success, message ->

                    _isLoading.value = false
                    _toast.value = message
                    callback(success, message)
                }
            }
        }
    }

    fun clear() {
        _toast.value = null
    }

    private val _offers = MutableStateFlow<List<OfferModel>>(emptyList())
    val offers: StateFlow<List<OfferModel>> = _offers


    fun loadOffers() {
        repo.getOffers { success, message, list ->

            if (success && list != null) {
                _offers.value = list
            } else {
                _offers.value = emptyList()
            }
        }

    }

    fun deleteOffer(id: String, callback: (Boolean, String) -> Unit){
        repo.deleteOffer(id,callback)
    }
}