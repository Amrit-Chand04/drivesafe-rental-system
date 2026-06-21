package com.example.drivesafe.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class BookingVehicleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BookingVehicle()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingVehicle() {

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Booking Details",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { }
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFEAF8EE)
                )
            )
        },
        containerColor = Color(0xFFEAF8EE)
    ){ padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            item {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = "Personal Details",
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = "",
                            onValueChange = {},
                            label = { Text("Full Name") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = "",
                            onValueChange = {},
                            label = { Text("Phone Number") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Call,
                                    contentDescription = null
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "Rental Plan",
                            fontWeight = FontWeight.Bold
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = true,
                                onClick = {},
                                colors = RadioButtonDefaults.colors()
                            )
                            Text("Hourly")

                            Spacer(modifier = Modifier.width(16.dp))

                            RadioButton(
                                selected = false,
                                onClick = {},
                                colors = RadioButtonDefaults.colors()
                            )
                            Text("Daily")
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Pickup Date") },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.DateRange,
                                    contentDescription = null
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Pickup Time") },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.DateRange,
                                    contentDescription = null
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = "",
                            onValueChange = {},
                            label = { Text("Duration") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // BOOK BUTTON
                Button(
                    onClick = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2E7D32)
                    )
                ) {
                    Text("Book Now", color = Color.White)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BookingScreenPreview() {
    BookingVehicle()
}