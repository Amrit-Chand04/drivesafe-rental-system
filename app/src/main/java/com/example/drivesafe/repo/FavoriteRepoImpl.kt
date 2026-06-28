package com.example.drivesafe.repo

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FavoriteRepoImpl : FavoriteRepo {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    private val uid get() = auth.currentUser?.uid

    override fun getFavorites(callback: (Boolean, String, Set<String>) -> Unit) {
        val uid = uid
        if (uid == null) {
            callback(false, "User not logged in", emptySet())
            return
        }

        database.getReference("users/$uid/favorites")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val ids = snapshot.children
                        .filter { it.getValue(Boolean::class.java) == true }
                        .mapNotNull { it.key }
                        .toSet()
                    callback(true, "Fetched", ids)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message, emptySet())
                }
            })
    }

    override fun addFavorite(vehicleId: String, callback: (Boolean, String) -> Unit) {
        val uid = uid
        if (uid == null) {
            callback(false, "User not logged in")
            return
        }

        database.getReference("users/$uid/favorites/$vehicleId")
            .setValue(true)
            .addOnSuccessListener { callback(true, "Added to favorites") }
            .addOnFailureListener { callback(false, it.message ?: "Failed") }
    }

    override fun removeFavorite(vehicleId: String, callback: (Boolean, String) -> Unit) {
        val uid = uid
        if (uid == null) {
            callback(false, "User not logged in")
            return
        }

        database.getReference("users/$uid/favorites/$vehicleId")
            .removeValue()
            .addOnSuccessListener { callback(true, "Removed from favorites") }
            .addOnFailureListener { callback(false, it.message ?: "Failed") }
    }
}