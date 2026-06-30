package com.example.drivesafe.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.drivesafe.model.VehicleFirebaseModel
import com.example.drivesafe.ui.theme.DriveSafeTheme
import com.example.drivesafe.viewmodel.VehicleViewModel


class BikeDetails : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val bikeId = intent.getStringExtra("bikeId") ?: ""

        setContent {
            DriveSafeTheme {
                BikeDetailsBody(
                    bikeId = bikeId
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BikeDetailsBody(
    bikeId: String
) {

    val context = LocalContext.current

    val viewModel: VehicleViewModel = viewModel()

    var bike by remember {
        mutableStateOf<VehicleFirebaseModel?>(null)
    }

    LaunchedEffect(bikeId) {
        viewModel.getVehicleById(bikeId) { success, message, vehicle ->
            if (success) {
                bike = vehicle
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Bike Details",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { (context as Activity).finish() }
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
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFEAF8EE))
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                thickness = 1.dp,
                color = Color.LightGray
            )

            Spacer(modifier = Modifier.height(8.dp))


            if (bike == null) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

            } else {

                val data = bike!!

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 15.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ) {

                    item {

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp),
                            shape = RoundedCornerShape(25.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            elevation = CardDefaults.cardElevation(5.dp)
                        ) {

                            AsyncImage(
                                model = data.vehicleImage,
                                contentDescription = data.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                        }

                    }

                    item {

                        Text(
                            text = data.name,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(5.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = "Status: ",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color.Black
                            )


                            Text(
                                text = data.status,
                                color = Color(0xFF00A859),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )

                        }
                    }

                    item {

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            )
                        ) {

                            Column(
                                modifier = Modifier.padding(6.dp)
                            ){
                                Text(
                                    text = "Description",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )


                                Spacer(modifier = Modifier.height(3.dp))

                                Text(
                                    text = data.description,
                                    color = Color.Gray
                                )

                            }
                        }

                    }

                    item {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

                            BikeFeatureBox(
                                title = "Fuel",
                                value = data.fuelType,
                                modifier = Modifier.weight(1f)
                            )

                            BikeFeatureBox(
                                title = "Capacity",
                                value = data.capacity,
                                modifier = Modifier.weight(1f)
                            )

                        }

                    }
                    item {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {


                            BikeFeatureBox(
                                title = "Engine",
                                value = data.engine,
                                modifier = Modifier.weight(1f)
                            )


                            BikeFeatureBox(
                                title = "Speed",
                                value = data.speed,
                                modifier = Modifier.weight(1f)
                            )

                        }

                    }

                    item {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

                            BikeFeatureBox(
                                title = "Number",
                                value = data.number,
                                modifier = Modifier.weight(1f)
                            )

                            BikeFeatureBox(
                                title = "Brand",
                                value = data.brand,
                                modifier = Modifier.weight(1f)
                            )

                        }

                    }

                    item {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

                            if (data.offerPercentage > 0 && data.discountedPrice > 0) {
                                VehiclePriceBox(
                                    originalPrice = data.price,
                                    discountedPrice = data.discountedPrice,
                                    offerPercentage = data.offerPercentage,
                                    modifier = Modifier.weight(1f)
                                )
                            } else {
                                BikeFeatureBox(
                                    title = "Price",
                                    value = data.price,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                        }

                    }
                    item {

                        Button(
                            onClick = {
                                val effectivePrice = if (data.offerPercentage > 0 && data.discountedPrice > 0)
                                    data.discountedPrice.toString() else data.price
                                val intent = Intent(context, BookingVehicleActivity::class.java)
                                intent.putExtra("vehicleId", bikeId)
                                intent.putExtra("vehiclePrice", effectivePrice)
                                context.startActivity(intent)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(55.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00A859)
                            )
                        ) {

                            Text(
                                text = "Book Now",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )

                        }

                    }

                }
            }
        }
    }
}

@Composable
fun BikeFeatureBox(
    title: String,
    value: String,
    modifier: Modifier
) {

    Card(
        modifier = modifier.height(65.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFEDEDED)
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = title,
                fontSize = 10.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = value,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Composable
fun VehiclePriceBox(
    originalPrice: String,
    discountedPrice: Int,
    offerPercentage: Int,
    modifier: Modifier
) {
    Card(
        modifier = modifier.height(65.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEDEDED))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Price", fontSize = 10.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Rs. $discountedPrice",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}