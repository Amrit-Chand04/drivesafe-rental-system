package com.example.drivesafe.repo

import com.example.drivesafe.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class UserRepoImplementation : UserRepo {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val ref = database.getReference("users")

    override fun login(
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    ) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->

                val uid = result.user?.uid

                if (uid == null) {
                    callback(false, "UID not found")
                    return@addOnSuccessListener
                }

                // ⚠️ OPTIONAL ONLY: if you really want to fetch/save user data
                val user = UserModel(email = email)

                ref.child(uid).setValue(user)
                    .addOnSuccessListener {
                        callback(true, "Login successful")
                    }
                    .addOnFailureListener { e ->
                        callback(false, "Login successful but DB error: ${e.message}")
                    }
            }
            .addOnFailureListener { e ->
                callback(false, e.message ?: "Login failed")
            }
    }
}