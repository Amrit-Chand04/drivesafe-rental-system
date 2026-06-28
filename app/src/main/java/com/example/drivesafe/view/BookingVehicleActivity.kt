package com.example.drivesafe.view

import android.app.Activity
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drivesafe.viewmodel.BookingViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class BookingVehicleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val vehicleId = intent.getStringExtra("vehicleId") ?: ""
        setContent {
            BookingVehicle(vehicleId = vehicleId)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingVehicle(vehicleId: String = "") {

    val context = LocalContext.current

    var fullName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var rentalPlan by remember { mutableStateOf("Hourly") }

    var pickupDate by remember { mutableStateOf("") }
    var pickupTime by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }

    var openDatePicker by remember { mutableStateOf(false) }
    var openTimePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()

    val bookingViewModel: BookingViewModel = viewModel()
    val message by bookingViewModel.message.collectAsState()

    LaunchedEffect(message) {
        message?.let {
            if (it.isNotEmpty()) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                bookingViewModel.clearMessage()
            }
        }
    }

    if (openTimePicker) {
        val cal = Calendar.getInstance()
        TimePickerDialog(
            context,
            { _, h, m -> pickupTime = String.format("%02d:%02d", h, m) },
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            true
        ).apply {
            setOnDismissListener { openTimePicker = false }
            show()
        }
        openTimePicker = false
    }

    if (openDatePicker) {
        DatePickerDialog(
            onDismissRequest = { openDatePicker = false },
            confirmButton = {
                Button(
                    onClick = {
                        pickupDate = datePickerState.selectedDateMillis
                            ?.let { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it)) }
                            ?: ""
                        openDatePicker = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A859))
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { openDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

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
                    IconButton(onClick = { (context as Activity).finish() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFEAF8EE)
                )
            )
        },
        containerColor = Color(0xFFEAF8EE)
    ) { padding ->

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
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {

                    Column(modifier = Modifier.padding(16.dp)) {

                        Text(text = "Personal Details", fontWeight = FontWeight.Bold)

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("Full Name") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Phone Number") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            leadingIcon = { Icon(Icons.Default.Call, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(text = "Rental Plan", fontWeight = FontWeight.Bold)

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = rentalPlan == "Hourly",
                                onClick = {
                                    rentalPlan = "Hourly"
                                    duration = ""
                                    pickupDate = ""
                                    pickupTime = ""
                                }
                            )
                            Text("Hourly")
                            Spacer(modifier = Modifier.width(16.dp))
                            RadioButton(
                                selected = rentalPlan == "Daily",
                                onClick = {
                                    rentalPlan = "Daily"
                                    duration = ""
                                    pickupDate = ""
                                    pickupTime = ""
                                }
                            )
                            Text("Daily")
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // DATE PICKER
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { openDatePicker = true }
                                .border(1.dp, Color(0xFF00A859), RoundedCornerShape(12.dp))
                                .padding(14.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (pickupDate.isEmpty()) "Select Pickup Date" else pickupDate,
                                    color = if (pickupDate.isEmpty()) Color.Gray else Color.Black,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = null,
                                    tint = Color(0xFF00A859)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // TIME PICKER
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { openTimePicker = true }
                                .border(1.dp, Color(0xFF00A859), RoundedCornerShape(12.dp))
                                .padding(14.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (pickupTime.isEmpty()) "Select Pickup Time" else pickupTime,
                                    color = if (pickupTime.isEmpty()) Color.Gray else Color.Black,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = Color(0xFF00A859)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = duration,
                            onValueChange = { duration = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Duration") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        bookingViewModel.validateAndBook(
                            fullName = fullName,
                            phoneNumber = phoneNumber,
                            rentalPlan = rentalPlan,
                            pickupDate = pickupDate,
                            pickupTime = pickupTime,
                            duration = duration
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
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
