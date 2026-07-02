package com.example.drivesafe.view

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.drivesafe.model.BookingModel
import com.example.drivesafe.viewmodel.BookingViewModel

class ManageBookings : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ManageBookingScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageBookingScreen() {
    val context = LocalContext.current
    val vm: BookingViewModel = viewModel()
    val bookings by vm.bookings.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val message by vm.message.collectAsState()
    var rejectBookingId by remember { mutableStateOf<String?>(null) }
    var rejectReason by remember { mutableStateOf("") }
    var acceptBookingId by remember { mutableStateOf<String?>(null) }
    var reasonDialogText by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        vm.loadAllBookings()
    }

    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            vm.clearMessage()
        }
    }

    if (reasonDialogText != null) {
        AlertDialog(
            onDismissRequest = { reasonDialogText = null },
            title = { Text(text = "Rejection Reason") },
            text = { Text(text = reasonDialogText ?: "") },
            confirmButton = {
                Button(
                    onClick = { reasonDialogText = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text("OK")
                }
            }
        )
    }

    if (acceptBookingId != null) {
        AlertDialog(
            onDismissRequest = { acceptBookingId = null },
            title = { Text(text = "Accept Booking") },
            text = { Text(text = "Are you sure you want to accept this booking request?") },
            confirmButton = {
                Button(
                    onClick = {
                        vm.updateBookingStatus(acceptBookingId!!, "ACCEPTED")
                        acceptBookingId = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text("Yes, Accept")
                }
            },
            dismissButton = {
                Button(
                    onClick = { acceptBookingId = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                ) {
                    Text("Cancel", color = Color.Black)
                }
            }
        )
    }

    if (rejectBookingId != null) {
        AlertDialog(
            onDismissRequest = {
                rejectBookingId = null
                rejectReason = ""
            },
            title = {
                Text(text = "Reject Booking")
            },
            text = {
                Column {
                    Text(text = "Write the reason for rejection")
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = rejectReason,
                        onValueChange = { rejectReason = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Reason") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val bookingId = rejectBookingId ?: return@Button
                        if (rejectReason.isBlank()) {
                            Toast.makeText(context, "Please enter a reason", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        vm.updateBookingStatus(bookingId, "REJECTED", rejectReason)
                        rejectBookingId = null
                        rejectReason = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
                ) {
                    Text("Reject")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        rejectBookingId = null
                        rejectReason = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                ) {
                    Text("Cancel", color = Color.Black)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Manage Bookings",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { (context as Activity).finish() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
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
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 14.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF2E7D32))
                    }
                }

                bookings.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No booking found",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(bookings) { booking ->
                            BookingCard(
                                booking = booking,
                                onAcceptClick = { acceptBookingId = booking.bookingId },
                                onRejectClick = { rejectBookingId = booking.bookingId },
                                onViewReasonClick = { reasonDialogText = booking.rejectionReason }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookingCard(
    booking: BookingModel,
    onAcceptClick: () -> Unit,
    onRejectClick: () -> Unit,
    onViewReasonClick: () -> Unit
) {
    val isRejected = booking.status.equals("REJECTED", true) && booking.rejectionReason.isNotBlank()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {

                AsyncImage(
                    model = booking.vehicleImage,
                    contentDescription = booking.vehicleName,
                    modifier = Modifier
                        .width(140.dp)
                        .height(185.dp)
                        .clip(RoundedCornerShape(18.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {

                    Text(
                        text = booking.vehicleName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32),
                        modifier = if (isRejected) Modifier.padding(end = 90.dp) else Modifier
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Name: ", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text(text = booking.fullName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = "Phone: ${booking.phoneNumber}", fontSize = 14.sp)
                        Text(text = "Plan: ${booking.rentalPlan}", fontSize = 14.sp)
                        Text(text = "Pickup Date: ${booking.pickupDate}", fontSize = 14.sp)
                        Text(text = "Pickup Time: ${booking.pickupTime}", fontSize = 14.sp)
                        Text(text = "Duration: ${booking.duration}", fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Text(text = "Status: ", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)

                        val statusColor = when (booking.status.uppercase()) {
                            "ACCEPTED" -> Color(0xFF2E7D32)
                            "REJECTED" -> Color(0xFFC62828)
                            else -> Color(0xFFFF9800)
                        }
                        val statusBg = when (booking.status.uppercase()) {
                            "ACCEPTED" -> Color(0xFFE8F5E9)
                            "REJECTED" -> Color(0xFFFFEBEE)
                            else -> Color(0xFFFFF3E0)
                        }

                        Box(
                            modifier = Modifier
                                .background(statusBg, RoundedCornerShape(50))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = booking.status,
                                color = statusColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    if (!booking.status.equals("ACCEPTED", true) &&
                        !booking.status.equals("REJECTED", true)
                    ) {
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Button(
                                onClick = onRejectClick,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(34.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
                            ) {
                                Text("Reject", fontSize = 12.sp)
                            }

                            Button(
                                onClick = onAcceptClick,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(34.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                            ) {
                                Text("Accept", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            if (isRejected) {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 10.dp, end = 10.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFFFFEBEE))
                        .clickable(onClick = onViewReasonClick)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "View Reason",
                        tint = Color(0xFFC62828),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Reason",
                        color = Color(0xFFC62828),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewManageBookingScreen() {
    ManageBookingScreen()
}
