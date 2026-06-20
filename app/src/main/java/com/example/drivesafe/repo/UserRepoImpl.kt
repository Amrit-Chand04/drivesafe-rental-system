package com.example.drivesafe.repo

import com.example.drivesafe.model.UserModel
import com.example.drivesafe.model.UserWithKyc
import com.example.drivesafe.model.KycFirebaseModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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
                val uid = auth.currentUser?.uid

                if (uid.isNullOrBlank()) {
                    auth.signOut()
                    callback(false, "Login failed")
                    return@addOnSuccessListener
                }

                ref.child(uid)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                callback(true, "Login successful")
                            } else {
                                auth.signOut()
                                callback(false, "Your account has been permanently deleted")
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            auth.signOut()
                            callback(false, error.message)
                        }
                    })
            }
            .addOnFailureListener {
                callback(false, it.message ?: "Login failed")
            }
    }

    override fun sendPasswordResetEmail(
        email: String,
        callback: (Boolean, String) -> Unit
    ) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Password reset email sent successfully")
                } else {
                    callback(
                        false,
                        task.exception?.message ?: "Failed to send reset email"
                    )
                }
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

    override fun getAllUser(callback: (Boolean, String, List<UserModel?>) -> Unit) {
        ref.addValueEventListener(object  : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val allUsers = mutableListOf<UserModel>()
                    for(user in snapshot.children){
                        val data = user.getValue(UserModel::class.java)
                        if(data != null){
                            allUsers.add(data)
                        }
                    }
                    callback(true,"fetched",allUsers)
                } else {
                    callback(true, "No users found", emptyList())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false,error.message,emptyList())
            }
        })
    }

    override fun getAllUserWithKyc(callback: (Boolean, String, List<UserWithKyc>) -> Unit) {
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(userSnapshot: DataSnapshot) {
                if (!userSnapshot.exists()) {
                    callback(true, "No users found", emptyList())
                    return
                }

                FirebaseDatabase.getInstance()
                    .getReference("kyc")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(kycSnapshot: DataSnapshot) {
                            val kycByPhone = mutableMapOf<String, KycFirebaseModel>()

                            for (kycChild in kycSnapshot.children) {
                                val kyc = kycChild.getValue(KycFirebaseModel::class.java)
                                if (kyc != null && kyc.phone.isNotBlank()) {
                                    kycByPhone[kyc.phone] = kyc
                                }
                            }

                            val allUsers = mutableListOf<UserWithKyc>()

                            for (userChild in userSnapshot.children) {
                                val user = userChild.getValue(UserModel::class.java)
                                if (user != null) {
                                    allUsers.add(
                                        UserWithKyc(
                                            user = user,
                                            kyc = kycByPhone[user.phone]
                                        )
                                    )
                                }
                            }

                            callback(true, "fetched", allUsers)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            callback(false, error.message, emptyList())
                        }
                    })
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, emptyList())
            }
        })
    }

    override fun rollbackCurrentUserRegistration() {
        auth.currentUser?.delete()
        auth.signOut()
    }

    override fun deleteUser(
        uid: String,
        callback: (Boolean, String) -> Unit
    ) {
        if (uid.isBlank()) {
            callback(false, "Invalid user id")
            return
        }

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var removedAny = false

                for (child in snapshot.children) {
                    val childUid = child.child("uid").getValue(String::class.java)
                    if (child.key == uid || childUid == uid) {
                        child.ref.removeValue()
                        removedAny = true
                    }
                }

                if (removedAny) {
                    callback(true, "User deleted successfully")
                } else {
                    callback(false, "User not found")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message)
            }
        })
    }
}