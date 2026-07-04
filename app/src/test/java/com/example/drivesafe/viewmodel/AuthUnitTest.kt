package com.example.drivesafe.viewmodel

import com.example.drivesafe.model.UserModel
import com.example.drivesafe.repo.AuthRepoImpl
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import junit.framework.TestCase
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any

class AuthUnitTest {
    @Mock
    private lateinit var mockAuth: FirebaseAuth

    @Mock
    private lateinit var mockTask: Task<AuthResult>

    @Mock
    private lateinit var mockFirebaseUser: FirebaseUser

    @Mock
    private lateinit var mockDbRef: DatabaseReference

    @Mock
    private lateinit var mockChildRef: DatabaseReference

    @Mock
    private lateinit var mockSnapshot: DataSnapshot

    private lateinit var authRepo: AuthRepoImpl

    @Captor
    private lateinit var authCaptor: ArgumentCaptor<OnCompleteListener<AuthResult>>

    @Captor
    private lateinit var dbCaptor: ArgumentCaptor<ValueEventListener>

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        authRepo = AuthRepoImpl(mockAuth, mockDbRef)
    }

    @Test
    fun testLogin_Successful() {
        val email = "test@example.com"
        val password = "testPassword"
        val uid = "testUid"
        var actualMessage = ""

        val userModel = UserModel(uid = uid, email = email, fullName = "Test User")

        // Mock Auth Success
        `when`(mockAuth.signInWithEmailAndPassword(email, password)).thenReturn(mockTask)
        `when`(mockTask.isSuccessful).thenReturn(true)
        `when`(mockAuth.currentUser).thenReturn(mockFirebaseUser)
        `when`(mockFirebaseUser.uid).thenReturn(uid)

        // Mock Database Success
        `when`(mockDbRef.child(uid)).thenReturn(mockChildRef)
        `when`(mockSnapshot.exists()).thenReturn(true)
        `when`(mockSnapshot.getValue(UserModel::class.java)).thenReturn(userModel)

        // Call login
        authRepo.login(email, password) { success, message, user ->
            actualMessage = message
        }

        // Trigger Auth Callback
        verify(mockTask).addOnCompleteListener(authCaptor.capture())
        authCaptor.value.onComplete(mockTask)

        // Trigger Database Callback
        verify(mockChildRef).addListenerForSingleValueEvent(dbCaptor.capture())
        dbCaptor.value.onDataChange(mockSnapshot)

        // Assert
        TestCase.assertEquals("Login successful", actualMessage)
    }

//    @Test
//    fun testLogin_Failure() {
//        val email = "test@example.com"
//        val password = "wrongPassword"
//        var actualMessage = ""
//
//        // Mock Auth Failure
//        `when`(mockAuth.signInWithEmailAndPassword(email, password)).thenReturn(mockTask)
//        `when`(mockTask.isSuccessful).thenReturn(false)
//        `when`(mockTask.exception).thenReturn(java.lang.Exception("Invalid credentials"))
//
//        // Call login
//        authRepo.login(email, password) { success, message, user ->
//            actualMessage = message
//        }
//
//        // Trigger Auth Callback
//        verify(mockTask).addOnCompleteListener(authCaptor.capture())
//        authCaptor.value.onComplete(mockTask)
//
//        // Assert
//        TestCase.assertEquals("Invalid credentials", actualMessage)
//    }

    @Test
    fun testRegister_Successful() {
        val email = "test@example.com"
        val password = "password123"
        val uid = "newUid"
        var actualMessage = ""

        // Mock Register Success
        `when`(mockAuth.createUserWithEmailAndPassword(email, password)).thenReturn(mockTask)
        `when`(mockTask.isSuccessful).thenReturn(true)
        `when`(mockAuth.currentUser).thenReturn(mockFirebaseUser)
        `when`(mockFirebaseUser.uid).thenReturn(uid)

        // Call register
        authRepo.register(email, password) { success, message, returnedUid ->
            actualMessage = message
        }

        // Trigger Auth Callback
        verify(mockTask).addOnCompleteListener(authCaptor.capture())
        authCaptor.value.onComplete(mockTask)

        // Assert
        TestCase.assertEquals("Account created successfully", actualMessage)
    }
}
