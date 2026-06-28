package com.example.drivesafe.repo

interface FavoriteRepo {

    fun getFavorites(callback: (Boolean, String, Set<String>) -> Unit)

    fun addFavorite(vehicleId: String, callback: (Boolean, String) -> Unit)

    fun removeFavorite(vehicleId: String, callback: (Boolean, String) -> Unit)
}