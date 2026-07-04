package com.example.drivesafe.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drivesafe.R
import com.example.drivesafe.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SplashBody()
        }
    }
}

@Composable
fun SplashBody(userViewModel: UserViewModel = viewModel()) {
    val context = LocalContext.current
    val activity = context as? Activity

    val user by userViewModel.user.collectAsState()

    LaunchedEffect(Unit) {
        delay(3000)

        if (FirebaseAuth.getInstance().currentUser == null) {
            context.startActivity(Intent(context, LoginActivity::class.java))
            activity?.finish()
        } else {
            userViewModel.loadCurrentUser()
        }
    }

    LaunchedEffect(user) {
        user?.let {
            val destination = if (it.role == "admin") AdminDashboard::class.java else UserDashboard::class.java
            context.startActivity(Intent(context, destination))
            activity?.finish()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(
            brush = Brush.linearGradient(
                colors = listOf(
                    // TOP
                    Color(0xFFF9FAFB),
                    // MIDDLE
                    Color(0xFFEAFBF3),
                    // BOTTOM
                    Color(0xFFD4F5E9)

                )
            )
        ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(R.drawable.logo_main),
            contentDescription = null,
            modifier = Modifier.size(400.dp)

        )

        CircularProgressIndicator()

    }
}
