package com.example.drivesafe.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.drivesafe.model.KycModel
import com.example.drivesafe.repo.KycRepo
import com.example.drivesafe.repo.KycRepoImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class KycViewModel(application: Application) : AndroidViewModel(application) {
    private val repo: KycRepo = KycRepoImpl(application.applicationContext)

    private val _toast = MutableStateFlow<String?>(null)
    val toast: StateFlow<String?> = _toast

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun submitKyc(model: KycModel, callback: (Boolean, String) -> Unit){
        when {
            model.name.isBlank() -> _toast.value = "Enter name"
            model.phone.isBlank() -> _toast.value = "Enter phone"
            model.doc == null -> _toast.value = "Upload licence"
            model.photo == null -> _toast.value = "Upload photo"

            else -> {

                _isLoading.value = true

                repo.submitKyc1(
                    model.name,
                    model.phone,
                    model.doc,
                    model.photo
                ){
                    success,message ->

                    _isLoading.value = false
                    _toast.value =message
                    callback(success, message)
                }
            }

        }
    }

    fun clear() {
        _toast.value = null
    }
}