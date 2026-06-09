package com.example.drivesafe.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.drivesafe.R
import com.example.drivesafe.model.UserModel
import com.example.drivesafe.repo.UserRepoImpl
import com.example.drivesafe.viewmodel.UserViewModel

class SignUpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SignUpScreen()
        }
    }
}

@Composable
fun SignUpScreen(enableBackend: Boolean = true) {

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val userViewModel = remember {
        if (enableBackend) UserViewModel(UserRepoImpl()) else null
    }

    Scaffold { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE8F5E9))
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(50.dp))

            Image(
                painter = painterResource(id = R.drawable.logo_for_app),
                contentDescription = "Logo",
                modifier = Modifier.size(130.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .shadow(10.dp, RoundedCornerShape(25.dp))
                    .background(Color.White, RoundedCornerShape(25.dp))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Sign Up",
                    style = TextStyle(fontSize = 30.sp, fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(25.dp))

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Full Name") },
                    singleLine = true,
                    shape = RoundedCornerShape(15.dp)
                )

                Spacer(modifier = Modifier.height(15.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Email") },
                    singleLine = true,
                    shape = RoundedCornerShape(15.dp)
                )

                Spacer(modifier = Modifier.height(15.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Phone Number") },
                    singleLine = true,
                    shape = RoundedCornerShape(15.dp)
                )

                Spacer(modifier = Modifier.height(15.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Password") },
                    singleLine = true,
                    visualTransformation =
                        if (passwordVisible) VisualTransformation.None
                        else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = {
                            passwordVisible = !passwordVisible
                        }) {
                            Icon(
                                painter =
                                    if (passwordVisible) painterResource(R.drawable.baseline_visibility_24)
                                    else painterResource(R.drawable.baseline_visibility_off_24),
                                contentDescription = null,
                                tint = Color(0xFF2E7D32)
                            )
                        }
                    },
                    shape = RoundedCornerShape(15.dp)
                )

                Spacer(modifier = Modifier.height(15.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Confirm Password") },
                    singleLine = true,
                    visualTransformation =
                        if (confirmPasswordVisible) VisualTransformation.None
                        else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = {
                            confirmPasswordVisible = !confirmPasswordVisible
                        }) {
                            Icon(
                                painter =
                                    if (confirmPasswordVisible) painterResource(R.drawable.baseline_visibility_24)
                                    else painterResource(R.drawable.baseline_visibility_off_24),
                                contentDescription = null,
                                tint = Color(0xFF2E7D32)
                            )
                        }
                    },
                    shape = RoundedCornerShape(15.dp)
                )

                Spacer(modifier = Modifier.height(25.dp))

                ElevatedButton(
                    onClick = {

                        if (!enableBackend) {
                            return@ElevatedButton
                        }

                        if (
                            fullName.isBlank() ||
                            email.isBlank() ||
                            phone.isBlank() ||
                            password.isBlank() ||
                            confirmPassword.isBlank()
                        ) {
                            Toast.makeText(
                                context,
                                "Please fill all fields",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else if (password != confirmPassword) {
                            Toast.makeText(
                                context,
                                "Passwords do not match",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            userViewModel?.register(
                                email = email,
                                password = password
                            ) { success, message, uid ->

                                if (success) {

                                    val user = UserModel(
                                        uid = uid,
                                        fullName = fullName,
                                        email = email,
                                        phone = phone
                                    )

                                    userViewModel.addUser(
                                        id = uid,
                                        model = user
                                    ) { addSuccess, addMessage ->

                                        if (addSuccess) {
                                            Toast.makeText(
                                                context,
                                                "Signup Successful",
                                                Toast.LENGTH_LONG
                                            ).show()

                                            fullName = ""
                                            email = ""
                                            phone = ""
                                            password = ""
                                            confirmPassword = ""
                                        } else {
                                            Toast.makeText(
                                                context,
                                                addMessage,
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }

                                } else {

                                    val errorMessage =
                                        if (message.contains("already", ignoreCase = true)) {
                                            "Email already in use"
                                        } else {
                                            message
                                        }

                                    Toast.makeText(
                                        context,
                                        errorMessage,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = Color(0xFF2E7D32),
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.elevatedButtonElevation(
                        defaultElevation = 8.dp
                    )
                ) {

                    Text(
                        text = "Sign Up",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpPreview() {
    SignUpScreen(enableBackend = false)
}