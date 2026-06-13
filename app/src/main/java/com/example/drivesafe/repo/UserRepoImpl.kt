package com.example.drivesafe.repo

import com.example.drivesafe.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class UserRepoImpl : UserRepo {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val ref = database.getReference("users")

    override fun register(
        email: String,
        password: String,
        callback: (Boolean, String, String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: ""

                    callback(
                        true,
                        "Account created successfully",
                        uid
                    )
                } else {
                    callback(
                        false,
                        task.exception?.message ?: "Signup failed",
                        ""
                    )
                }
            }
    }

    override fun login(
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                callback(true, "Login successful")
            }
            .addOnFailureListener {
                callback(false, it.message ?: "Login failed")
            }
    }

    override fun addUser(
        uid: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(uid)
            .setValue(model)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    callback(true, "Signup Successful")
                } else {
                    callback(false, task.exception?.message ?: "Failed to save user")
                }
            }
    }

    override fun changePassword(
        oldPassword: String,
        newPassword: String,
        callback: (Boolean, String) -> Unit
    ) {
        val user = auth.currentUser

        // User not logged in
        if (user == null) {
            callback(false, "User not logged in")
            return
        }

        val email = user.email

        // Email not available
        if (email.isNullOrEmpty()) {
            callback(false, "Email not found")
            return
        }

        val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(
            email,
            oldPassword
        )

        user.reauthenticate(credential)
            .addOnSuccessListener {

                user.updatePassword(newPassword)
                    .addOnSuccessListener {
                        callback(true, "Password changed successfully")
                    }
                    .addOnFailureListener {
                        callback(false, it.message ?: "Failed to update password")
                    }
            }
            .addOnFailureListener {
                callback(false, "Old password is incorrect")
            }
    }
}