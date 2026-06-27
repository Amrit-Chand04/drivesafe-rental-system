package com.example.drivesafe.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drivesafe.model.UserWithKyc
import com.example.drivesafe.viewmodel.UserViewModel
import com.google.firebase.FirebaseApp


class UserManage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)

        setContent {
            UserScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen() {

    val context = LocalContext.current
    val viewModel: UserViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

    val users by viewModel.allUsers.collectAsState()
    val isLoading by viewModel.loading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getAllUsers()
    }

    var selectedUser by remember {
        mutableStateOf<UserWithKyc?>(null)
    }

    var deleteUser by remember {
        mutableStateOf<UserWithKyc?>(null)
    }

    // Dialog
    selectedUser?.let { user ->

        AlertDialog(
            onDismissRequest = {
                selectedUser = null
            },
            title = {
                Text(
                    text = "User Details",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {

                Column {

                    Text("Name: ${user.user.fullName}")

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Phone: ${user.user.phone}")

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Email: ${user.user.email}")

                    Spacer(modifier = Modifier.height(8.dp))

                    if (user.user.role == "admin") {

                        Text(
                            text = "Role: Admin"
                        )

                    } else {

                        Text(
                            text = "KYC Status: ${user.kyc?.status ?: "Not submitted"}"
                        )

                    }
                }
            },
            confirmButton = {

                Button(
                    onClick = {
                        selectedUser = null
                    }
                ) {
                    Text("Close")
                }
            }
        )
    }

    deleteUser?.let { user ->

        AlertDialog(
            onDismissRequest = {
                deleteUser = null
            },
            title = {
                Text(
                    text = "Confirm Delete",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("Do you want to delete ${user.user.fullName}?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteUser(user.user.uid) { success, message ->
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                        deleteUser = null
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        deleteUser = null
                    }
                ) {
                    Text("No")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "User Management",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFEAF8EE)
                )
            )
        },
        containerColor = Color(0xFFEAF8EE)
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            Text(
                text = "All Users",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(18.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .border(1.dp, Color.Black, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }

                    users.isEmpty() -> {
                        Text(
                            text = "No registered members found",
                            modifier = Modifier.align(Alignment.Center),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    else -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(users) { user ->

                                UserCard(
                                    user = user,
                                    onView = {
                                        selectedUser = user
                                    },
                                    onDelete = {
                                        deleteUser = user
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun UserCard(
    user: UserWithKyc,
    onView: () -> Unit,
    onDelete: () -> Unit
) {

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {

        Box(
            modifier = Modifier
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF1E88E5), Color(0xFF00C853))
                    )
                )
                .padding(16.dp)
        ) {

            Column {

                Text(
                    text = user.user.fullName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = user.user.email,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Button(
                        onClick = onView ,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White
                        )
                    ) {
                        Text("View", color = Color.Black)
                    }

                    if (user.user.role != "admin") {

                        Button(
                            onClick = onDelete,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red
                            )
                        ) {
                            Text("Delete", color = Color.White)
                        }

                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun UserScreenPreview() {
    UserScreen()
}