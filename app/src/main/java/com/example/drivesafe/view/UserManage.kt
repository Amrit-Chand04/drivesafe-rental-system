package com.example.drivesafe.view

import android.os.Bundle
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.drivesafe.model.UserModel


class UserManage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            UserScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen() {

    // DEMO DATA ONLY
    val users = remember {
        listOf(
            UserModel("1", "Amrit Chand Thakuri", "ammu@gmail.com"),
            UserModel("2", "Aman", "aman@gmail.com"),
            UserModel("3", "Gyanu", "gyanu@gmail.com"),
            UserModel("4", "Prajeeta", "prajeeta@gmail.com")
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

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(users) { user ->
                        UserCard(user)
                    }
                }
            }
        }
    }
}


@Composable
fun UserCard(user: UserModel) {

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
                    text = user.fullName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = user.email,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White
                        )
                    ) {
                        Text("View", color = Color.Black)
                    }

                    Button(
                        onClick = { },
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


@Preview(showBackground = true)
@Composable
fun UserScreenPreview() {
    UserScreen()
}