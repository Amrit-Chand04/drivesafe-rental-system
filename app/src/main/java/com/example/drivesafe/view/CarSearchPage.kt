package com.example.drivesafe.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
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

import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drivesafe.model.VehicleModel

import com.example.drivesafe.ui.theme.DriveSafeTheme
import com.example.drivesafe.viewmodel.VehicleViewModel

class CarSearchPage : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            DriveSafeTheme {
                CarSearchBody()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarSearchBody() {

    val context = LocalContext.current
    val vehicleViewModel: VehicleViewModel = viewModel()

    var selectedIndex by remember { mutableStateOf(0) }

    var cars by remember {
        mutableStateOf<List<VehicleModel>>(emptyList())
    }

    var showFilterDialog by remember {
        mutableStateOf(false)
    }

    var selectedPriceFilter by remember {
        mutableStateOf("All")
    }

    LaunchedEffect(Unit) {
        vehicleViewModel.getVehicles { success, message, list ->
            if (success) {
                cars = list.filter {
                    it.type.equals("Car", ignoreCase = true)
                }
            }
        }
    }

    val filteredCars = when (selectedPriceFilter) {

        "Low" -> cars.filter {
            val price = it.price.filter { char -> char.isDigit() }.toIntOrNull() ?: 0
            price < 3000
        }

        "Medium" -> cars.filter {
            val price = it.price.filter { char -> char.isDigit() }.toIntOrNull() ?: 0
            price in 3000..7000
        }

        "High" -> cars.filter {
            val price = it.price.filter { char -> char.isDigit() }.toIntOrNull() ?: 0
            price > 7000
        }

        else -> cars
    }

    if (showFilterDialog) {
        AlertDialog(
            onDismissRequest = {
                showFilterDialog = false
            },
            title = {
                Text("Filter by Price")
            },
            text = {
                Column {

                    TextButton(
                        onClick = {
                            selectedPriceFilter = "All"
                            showFilterDialog = false
                        }
                    ) {
                        Text("All")
                    }

                    TextButton(
                        onClick = {
                            selectedPriceFilter = "Low"
                            showFilterDialog = false
                        }
                    ) {
                        Text("Below Rs. 3000")
                    }

                    TextButton(
                        onClick = {
                            selectedPriceFilter = "Medium"
                            showFilterDialog = false
                        }
                    ) {
                        Text("Rs. 3000 - Rs. 7000")
                    }

                    TextButton(
                        onClick = {
                            selectedPriceFilter = "High"
                            showFilterDialog = false
                        }
                    ) {
                        Text("Above Rs. 7000")
                    }
                }
            },
            confirmButton = {}
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Car Search",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(id = R.drawable.outline_arrow_back_ios_24),
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(id = R.drawable.rounded_add_alert_24),
                            contentDescription = "Notification",
                            tint = Color.Gray
                        )
                    }

                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_supervised_user_circle_24),
                            contentDescription = "Profile",
                            tint = Color.Gray
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF3F8F5)
                )
            )
        },

        bottomBar = {
            NavigationBar(containerColor = Color.White) {

                NavigationBarItem(
                    selected = selectedIndex == 0,
                    onClick = { selectedIndex = 0 },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_home_24),
                            contentDescription = "Home"
                        )
                    },
                    label = { Text("Home") }
                )

                NavigationBarItem(
                    selected = selectedIndex == 1,
                    onClick = { selectedIndex = 1 },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_inbox_24),
                            contentDescription = "Inbox"
                        )
                    },
                    label = { Text("Inbox") }
                )

                NavigationBarItem(
                    selected = selectedIndex == 2,
                    onClick = { selectedIndex = 2 },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_supervised_user_circle_24),
                            contentDescription = "Profile"
                        )
                    },
                    label = { Text("Profile") }
                )
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF3F8F5))
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {

            Spacer(modifier = Modifier.height(18.dp))

            HorizontalDivider(
                thickness = 1.dp,
                color = Color.LightGray
            )

            Spacer(modifier = Modifier.height(22.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    BrandButton(
                        text = "ALL",
                        selected = true
                    )
                }

                item {
                    BrandButton(
                        text = "Suzuki",
                        selected = false
                    )
                }
                item {
                    BrandButton(
                        text = "Toyota",
                        selected = false
                    )
                }
                item {
                    BrandButton(
                        text = "BYD",
                        selected = false
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        showFilterDialog = true
                    },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    ),
                    modifier = Modifier.size(55.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.filter),
                        contentDescription = "Filter",
                        modifier = Modifier.size(22.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Recommend For You",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Text(
                    text = selectedPriceFilter,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filteredCars) { car ->

                    CarGridCard(
                        car = car,
                        onClick = {
                            val intent = Intent(context, CarDetails::class.java)
                            intent.putExtra("vehicleId", car.vehicleId)
                            context.startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun BrandButton(
    text: String,
    selected: Boolean
) {
    Button(
        onClick = {},
        shape = RoundedCornerShape(25.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) Color(0xFF202A2A) else Color.White
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = if (selected) Color.White else Color.Black,
            fontSize = 12.sp
        )
    }
}

@Composable
fun CarGridCard(
    car: VehicleModel,
    onClick: () -> Unit
) {

    val image = if (
        car.imageName.lowercase() == "bike" ||
        car.type.equals("Bike", ignoreCase = true)
    ) {
        R.drawable.bike
    } else {
        R.drawable.car
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(125.dp)
                    .background(Color(0xFFF1F1F1)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = image),
                    contentDescription = car.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(95.dp)
                        .padding(8.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(
                    text = car.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = car.location.ifEmpty { "No location" },
                    fontSize = 11.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = car.price.ifEmpty { "No price" },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onClick,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF202A2A)
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 3.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text(
                        text = "Book now",
                        fontSize = 10.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}


@Preview(
    showBackground = true,
    widthDp = 390,
    heightDp = 800
)
@Composable
fun PreviewCarSearchPage() {
    DriveSafeTheme {
        CarSearchBody()
    }
}