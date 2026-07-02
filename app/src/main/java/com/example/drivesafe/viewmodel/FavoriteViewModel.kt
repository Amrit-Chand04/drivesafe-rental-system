package com.example.drivesafe.viewmodel

import androidx.lifecycle.ViewModel
import com.example.drivesafe.repo.FavoriteRepo
import com.example.drivesafe.repo.FavoriteRepoImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FavoriteViewModel(private val repo: FavoriteRepo = FavoriteRepoImpl()) : ViewModel() {

    private val _favoriteIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadFavorites(callback: (Boolean, String) -> Unit = { _, _ -> }) {
        _isLoading.value = true
        repo.getFavorites { success, message, ids ->
            _isLoading.value = false
            if (success) {
                _favoriteIds.value = ids
            }
            callback(success, message)
        }
    }

    fun toggleFavorite(vehicleId: String) {
        val current = _favoriteIds.value.toMutableSet()
        if (current.contains(vehicleId)) {
            current.remove(vehicleId)
            repo.removeFavorite(vehicleId) { _, _ -> }
        } else {
            current.add(vehicleId)
            repo.addFavorite(vehicleId) { _, _ -> }
        }
        _favoriteIds.value = current
    }
}