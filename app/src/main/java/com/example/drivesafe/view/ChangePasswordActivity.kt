package com.example.drivesafe.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drivesafe.R
import com.example.drivesafe.repo.UserRepoImpl
import com.example.drivesafe.view.ui.theme.DriveSafeTheme
import com.example.drivesafe.viewmodel.UserViewModel

class ChangePasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChangePasswordBody()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordBody() {

    val viewModel: UserViewModel = viewModel()

    val context = LocalContext.current
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val message = viewModel.message.value
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFE8F5E9),
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = {
                        var currentContext = context
                        while (currentContext is android.content.ContextWrapper) {
                            if (currentContext is ComponentActivity) {
                                currentContext.finish()
                                break
                            }
                            currentContext = currentContext.baseContext
                        }
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.outline_arrow_back_ios_24),
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 17.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(6.dp))

            // App Logo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.logo_for_app),
                    contentDescription = null,
                    modifier = Modifier.size(130.dp)
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            // Main Form Card
            Column(
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(
                        color = Color(0xFFFFFFFF),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(13.dp)
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                // Title
                Text(
                    text = "Change Password",
                    style = TextStyle(
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Please enter and confirm your new strong password.",
                    fontSize = 18.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(26.dp))

                // Old Password Input
                Text(
                    text = "Old Password",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = oldPassword,
                    onValueChange = { oldPassword = it },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Enter old password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = Color.Transparent,
                        unfocusedContainerColor = Color.Gray.copy(alpha = 0.1f),
                        focusedContainerColor = Color.Gray.copy(alpha = 0.1f),
                        focusedIndicatorColor = Color(0xFF0066FF),
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // New Password Input
                Text(
                    text = "New Password",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Enter new password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = Color.Transparent,
                        unfocusedContainerColor = Color.Gray.copy(alpha = 0.1f),
                        focusedContainerColor = Color.Gray.copy(alpha = 0.1f),
                        focusedIndicatorColor = Color(0xFF0066FF),
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Confirm Password Input
                Text(
                    text = "Confirm Password",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Confirm your password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = Color.Transparent,
                        unfocusedContainerColor = Color.Gray.copy(alpha = 0.1f),
                        focusedContainerColor = Color.Gray.copy(alpha = 0.1f),
                        focusedIndicatorColor = Color(0xFF0066FF),
                    )
                )

                Spacer(modifier = Modifier.height(30.dp))

                // Change Password Button
                ElevatedButton(
                    onClick = {
                        if (
                            oldPassword.isBlank() ||
                            newPassword.isBlank() ||
                            confirmPassword.isBlank()
                        ) {
                            Toast.makeText(
                                context,
                                "All fields are required",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@ElevatedButton
                        }
                        viewModel.changePassword(
                            oldPassword,
                            newPassword,
                            confirmPassword
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(18.dp),
                    elevation = ButtonDefaults.elevatedButtonElevation(
                        defaultElevation = 8.dp
                    ),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = Color.Transparent
                    ),
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF0066FF),
                                        Color(0xFF16D64D)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Change Password",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (message.isNotEmpty()) {

                    Text(
                        text = message,
                        color = if (
                            message.contains("successfully", true)
                        ) Color(0xFF16D64D)
                        else Color.Red,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChangePasswordPreview() {
    DriveSafeTheme {
        ChangePasswordBody()
    }
}
