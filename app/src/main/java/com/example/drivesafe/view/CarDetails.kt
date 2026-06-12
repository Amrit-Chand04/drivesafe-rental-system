package com.example.drivesafe.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.drivesafe.R

import com.example.drivesafe.model.VehicleModel

import com.example.drivesafe.ui.theme.DriveSafeTheme
import com.google.firebase.database.FirebaseDatabase

class CarDetails : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val vehicleId = intent.getStringExtra("vehicleId") ?: ""

        setContent {
            DriveSafeTheme {
                CarDetailsBody(vehicleId)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarDetailsBody(vehicleId: String = "") {

    val context = LocalContext.current

    var selectedIndex by remember {
        mutableStateOf(0)
    }

    var vehicle by remember {
        mutableStateOf<VehicleModel?>(null)
    }

    LaunchedEffect(vehicleId) {
        if (vehicleId.isNotEmpty()) {
            FirebaseDatabase.getInstance()
                .reference
                .child("vehicles")
                .child(vehicleId)
                .get()
                .addOnSuccessListener { snapshot ->
                    vehicle = snapshot.getValue(VehicleModel::class.java)
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Car Details",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                navigationIcon = {

                    IconButton(
                        onClick = {
                            (context as? ComponentActivity)?.finish()
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.outline_arrow_back_ios_24),
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFE8F5E9)
                )
            )
        },

    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE8F5E9))
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            item {
                Spacer(modifier = Modifier.height(9.dp))

                HorizontalDivider(
                    thickness = 1.dp,
                    color = Color.LightGray
                )

                Spacer(modifier = Modifier.height(21.dp))

                if (vehicle == null) {

                    Text(
                        text = "No Car Selected",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                } else {

                    val image = if (
                        vehicle!!.imageName.lowercase() == "bike" ||
                        vehicle!!.type.equals("Bike", ignoreCase = true)
                    ) {
                        R.drawable.bike
                    } else {
                        R.drawable.car
                    }

                    Image(
                        painter = painterResource(id = image),
                        contentDescription = vehicle!!.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp),
                        contentScale = ContentScale.Fit
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = vehicle!!.name,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = vehicle!!.description.ifEmpty {
                            "A comfortable vehicle available for rent."
                        },
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = "Price: ${vehicle!!.price.ifEmpty { "Rs.7000/Day" }}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Text(
                        text = "Location: ${vehicle!!.location.ifEmpty { "Kathmandu, Nepal" }}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Text(
                        text = "Status: ${vehicle!!.status}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00A859)
                    )
                }

                Spacer(modifier = Modifier.height(19.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = "Car Features",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

                            EmptyFeatureBox(
                                title = "Capacity",
                                value = vehicle?.capacity?.ifEmpty { "--" } ?: "--",
                                modifier = Modifier.weight(1f)
                            )

                            EmptyFeatureBox(
                                title = "Engine",
                                value = vehicle?.engine?.ifEmpty { "--" } ?: "--",
                                modifier = Modifier.weight(1f)
                            )

                            EmptyFeatureBox(
                                title = "Speed",
                                value = vehicle?.speed?.ifEmpty { "--" } ?: "--",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(11.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

                            EmptyFeatureBox(
                                title = "Battery",
                                value = vehicle?.battery?.ifEmpty { "--" } ?: "--",
                                modifier = Modifier.weight(1f)
                            )

                            EmptyFeatureBox(
                                title = "Parking",
                                value = vehicle?.parking?.ifEmpty { "--" } ?: "--",
                                modifier = Modifier.weight(1f)
                            )

                            EmptyFeatureBox(
                                title = "Safety",
                                value = vehicle?.safety?.ifEmpty { "--" } ?: "--",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF202A2A)
                    )
                ) {
                    Text(
                        text = "Book Now",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyFeatureBox(
    title: String,
    value: String,
    modifier: Modifier
) {

    Card(
        modifier = modifier.height(90.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFEAF7F0)
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = title,
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 390,
    heightDp = 800
)
@Composable
fun CarDetailsPreview() {
    DriveSafeTheme {
        CarDetailsBody()
    }
}