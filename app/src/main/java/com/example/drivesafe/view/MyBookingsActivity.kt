package com.example.drivesafe.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.drivesafe.viewmodel.BookingViewModel

class MyBookingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyBookingScreen()
        }
    }
}

@Composable
fun MyBookingScreen() {
    val vm: BookingViewModel = viewModel()
    val bookings by vm.bookings.collectAsState()
    val isLoading by vm.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        vm.loadMyBookings()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "My Bookings",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(20.dp))

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
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(210.dp),
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {

                            Box(modifier = Modifier.fillMaxSize()) {

                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    // IMAGE FROM CLOUDINARY URL
                                    AsyncImage(
                                        model = booking.vehicleImage,
                                        contentDescription = booking.vehicleName,
                                        modifier = Modifier
                                            .width(144.dp)
                                            .height(185.dp)
                                            .clip(RoundedCornerShape(16.dp)),

                                        contentScale = ContentScale.Crop
                                    )

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {

                                        Text(
                                            text = booking.vehicleName,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )


                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text(text = "Plan: ${booking.rentalPlan}")

                                        Text(text = "Pickup Date: ${booking.pickupDate}")

                                        Text(text = "Pickup Time: ${booking.pickupTime}")

                                        Text(text = "Duration: ${booking.duration}")

                                        val isRedStatus = booking.status.equals("pending", ignoreCase = true) ||
                                                booking.status.equals("rejected", ignoreCase = true)

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Status: ",
                                                fontWeight = FontWeight.Medium
                                            )

                                            if (isRedStatus) {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(50))
                                                        .background(Color(0xFFFFEBEE))
                                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = booking.status,
                                                        color = Color(0xFFC62828),
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 10.sp
                                                    )
                                                }
                                            } else {

                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(50))
                                                        .background(Color(0xFFE8F5E9))
                                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = booking.status,
                                                        color = Color(0xFF2E7D32),
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 10.sp
                                                    )
                                                }
                                            }
                                        }

                                        Text(text = "Payment: ${booking.paymentStatus}")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewMyBookingScreen() {
    MyBookingScreen()
}
