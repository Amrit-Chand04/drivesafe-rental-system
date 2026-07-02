package com.example.drivesafe.view

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.vector.ImageVector
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

    LaunchedEffect(Unit) {
        vm.loadAllBookings()
    }

    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            vm.clearMessage()
        }
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
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(bookings) { booking ->
                            BookingCard(
                                booking = booking,
                                onAcceptClick = { acceptBookingId = booking.bookingId },
                                onRejectClick = { rejectBookingId = booking.bookingId }
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class StatusStyle(
    val label: String,
    val icon: ImageVector,
    val color: Color,
    val background: Color
)

private fun statusStyleOf(status: String): StatusStyle = when (status.uppercase()) {
    "ACCEPTED" -> StatusStyle("Booking Confirmed", Icons.Default.CheckCircle, Color(0xFF2E7D32), Color(0xFFE8F5E9))
    "REJECTED" -> StatusStyle("Booking Rejected", Icons.Default.Close, Color(0xFFC62828), Color(0xFFFFEBEE))
    else -> StatusStyle("Pending Review", Icons.Default.Schedule, Color(0xFFEF6C00), Color(0xFFFFF3E0))
}

@Composable
fun BookingCard(
    booking: BookingModel,
    onAcceptClick: () -> Unit,
    onRejectClick: () -> Unit
) {
    val isPending = !booking.status.equals("ACCEPTED", true) &&
            !booking.status.equals("REJECTED", true)
    val statusStyle = statusStyleOf(booking.status)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {

            // Status banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(statusStyle.background)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = statusStyle.icon,
                    contentDescription = null,
                    tint = statusStyle.color,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = statusStyle.label,
                    color = statusStyle.color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            Column(modifier = Modifier.padding(14.dp)) {

                Row(verticalAlignment = Alignment.CenterVertically) {

                    AsyncImage(
                        model = booking.vehicleImage,
                        contentDescription = booking.vehicleName,
                        modifier = Modifier
                            .size(width = 100.dp, height = 130.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {

                        Text(
                            text = booking.vehicleName,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B1B1B)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        InfoRow(icon = Icons.Default.Person, text = booking.fullName)
                        Spacer(modifier = Modifier.height(4.dp))
                        InfoRow(icon = Icons.Default.Phone, text = booking.phoneNumber)

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Payment",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            val paymentPaid = booking.paymentStatus.equals("PAID", true)
                            StatusChip(
                                label = booking.paymentStatus,
                                textColor = if (paymentPaid) Color(0xFF1565C0) else Color(0xFFE65100),
                                backgroundColor = if (paymentPaid) Color(0xFFE3F2FD) else Color(0xFFFFF3E0)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    InfoTile(
                        icon = Icons.Default.CalendarMonth,
                        label = "Pickup",
                        value = "${booking.pickupDate} • ${booking.pickupTime}",
                        modifier = Modifier.weight(1f)
                    )
                    InfoTile(
                        icon = Icons.Default.Timer,
                        label = "Duration",
                        value = "${booking.rentalPlan} • ${booking.duration}",
                        modifier = Modifier.weight(1f)
                    )
                }

                if (booking.status.equals("REJECTED", true) && booking.rejectionReason.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFF5F5), RoundedCornerShape(12.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "Reason: ${booking.rejectionReason}",
                            fontSize = 12.sp,
                            color = Color(0xFFC62828)
                        )
                    }
                }

                if (isPending) {
                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Color(0xFFEEEEEE))
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onRejectClick,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reject")
                        }

                        Button(
                            onClick = onAcceptClick,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Accept")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = text, fontSize = 13.sp, color = Color.DarkGray)
    }
}

@Composable
fun InfoTile(icon: ImageVector, label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Color(0xFFF4F6F4), RoundedCornerShape(14.dp))
            .padding(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF2E7D32),
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1B1B1B))
    }
}

@Composable
fun StatusChip(label: String, textColor: Color, backgroundColor: Color) {
    Box(
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewManageBookingScreen() {
    ManageBookingScreen()
}
