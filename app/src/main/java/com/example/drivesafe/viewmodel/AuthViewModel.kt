package com.example.drivesafe.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message

    fun sendPasswordResetEmail(email: String) {

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    _message.value =
                        "Password reset email sent successfully"

                } else {

                    _message.value =
                        task.exception?.message
                            ?: "Failed to send reset email"
                }
            }
    }
}