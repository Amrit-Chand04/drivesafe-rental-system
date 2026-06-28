package com.example.drivesafe.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.drivesafe.model.KycFirebaseModel
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

    private val _myKycRecord = MutableStateFlow<KycFirebaseModel?>(null)
    val myKycRecord: StateFlow<KycFirebaseModel?> = _myKycRecord

    private val _isLoadingRecord = MutableStateFlow(true)
    val isLoadingRecord: StateFlow<Boolean> = _isLoadingRecord

    fun loadMyKycRecord() {
        _isLoadingRecord.value = true
        repo.getMyKycRecord { record ->
            _myKycRecord.value = record
            _isLoadingRecord.value = false
        }
    }

    private val _isCheckingKyc = MutableStateFlow(false)
    val isCheckingKyc: StateFlow<Boolean> = _isCheckingKyc

    fun checkKycStatus(callback: (Boolean) -> Unit) {
        _isCheckingKyc.value = true
        repo.getMyKycStatus { isVerified ->
            _isCheckingKyc.value = false
            callback(isVerified)
        }
    }

    fun clear() {
        _toast.value = null
    }

    private val _allKycList = MutableStateFlow<List<KycFirebaseModel>>(emptyList())
    val allKycList: StateFlow<List<KycFirebaseModel>> = _allKycList

    private val _isLoadingAll = MutableStateFlow(false)
    val isLoadingAll: StateFlow<Boolean> = _isLoadingAll

    fun loadAllKycRecords() {
        _isLoadingAll.value = true
        repo.getAllKycRecords { list ->
            _allKycList.value = list
            _isLoadingAll.value = false
        }
    }

    fun updateKycStatus(uid: String, status: String) {
        repo.updateKycStatus(uid, status) { success ->
            if (success) {
                _allKycList.value = _allKycList.value.map { kyc ->
                    if (kyc.uid == uid) kyc.copy(status = status) else kyc
                }
            }
        }
    }
}