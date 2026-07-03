package com.example.drivesafe.repo

import com.example.drivesafe.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AuthRepoImpl(
    val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val dbRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
) : AuthRepo {

    override fun register(
        email: String,
        password: String,
        callback: (Boolean, String, String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: ""
                    callback(true, "Account created successfully", uid)
                } else {
                    callback(false, task.exception?.message ?: "Signup failed", "")
                }
            }
    }

    override fun login(
        email: String,
        password: String,
        callback: (Boolean, String, UserModel?) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    if (uid.isNullOrBlank()) {
                        auth.signOut()
                        callback(false, "Login failed", null)
                        return@addOnCompleteListener
                    }

                    dbRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                val user = snapshot.getValue(UserModel::class.java)
                                if (user != null) {
                                    callback(true, "Login successful", user)
                                } else {
                                    callback(false, "User data not found", null)
                                }
                            } else {
                                auth.signOut()
                                callback(false, "Your account has been permanently deleted", null)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            auth.signOut()
                            callback(false, error.message, null)
                        }
                    })
                } else {
                    callback(false, task.exception?.message ?: "Login failed", null)
                }
            }
    }

    override fun addUser(
        uid: String,
        user: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        dbRef.child(uid).setValue(user)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "User added successfully")
                } else {
                    callback(false, task.exception?.message ?: "Failed to add user")
                }
            }
    }

    override fun rollbackCurrentUserRegistration() {
        auth.currentUser?.delete()
        auth.signOut()
    }
}
