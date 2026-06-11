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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drivesafe.model.VehicleModel
import com.example.drivesafe.ui.theme.DriveSafeTheme
import com.example.drivesafe.viewmodel.VehicleViewModel
import com.example.drivesafe.R


class BikeSearchPage : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            DriveSafeTheme {
                BikeSearchBody()
            }
        }
    }
}

data class BikeModel(
    val image: Int,
    val name: String,
    val price: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BikeSearchBody() {

    val context = LocalContext.current
    val vehicleViewModel: VehicleViewModel = viewModel()

    var selectedIndex by remember {
        mutableStateOf(0)
    }

    var bikes by remember {
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
                bikes = list.filter {
                    it.type.equals("Bike", ignoreCase = true)
                }
            }
        }
    }

    val filteredBikes = when (selectedPriceFilter) {

        "Low" -> bikes.filter {
            val price = it.price.filter { char -> char.isDigit() }.toIntOrNull() ?: 0
            price < 3000
        }

        "Medium" -> bikes.filter {
            val price = it.price.filter { char -> char.isDigit() }.toIntOrNull() ?: 0
            price in 3000..7000
        }

        "High" -> bikes.filter {
            val price = it.price.filter { char -> char.isDigit() }.toIntOrNull() ?: 0
            price > 7000
        }

        else -> bikes
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
                        text = "Bike Search",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(
                                id = R.drawable.outline_arrow_back_ios_24
                            ),
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(
                                id = R.drawable.rounded_add_alert_24
                            ),
                            contentDescription = "Notification",
                            tint = Color.Gray
                        )
                    }

                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(
                                id = R.drawable.baseline_supervised_user_circle_24
                            ),
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
            NavigationBar(
                containerColor = Color.White
            ) {
                NavigationBarItem(
                    selected = selectedIndex == 0,
                    onClick = {
                        selectedIndex = 0
                    },
                    icon = {
                        Icon(
                            painter = painterResource(
                                id = R.drawable.baseline_home_24
                            ),
                            contentDescription = "Home"
                        )
                    },
                    label = {
                        Text(text = "Home")
                    }
                )

                NavigationBarItem(
                    selected = selectedIndex == 1,
                    onClick = {
                        selectedIndex = 1
                    },
                    icon = {
                        Icon(
                            painter = painterResource(
                                id = R.drawable.baseline_inbox_24
                            ),
                            contentDescription = "Inbox"
                        )
                    },
                    label = {
                        Text(text = "Inbox")
                    }
                )

                NavigationBarItem(
                    selected = selectedIndex == 2,
                    onClick = {
                        selectedIndex = 2
                    },
                    icon = {
                        Icon(
                            painter = painterResource(
                                id = R.drawable.baseline_supervised_user_circle_24
                            ),
                            contentDescription = "Profile"
                        )
                    },
                    label = {
                        Text(text = "Profile")
                    }
                )
            }
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF3F8F5))
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {

            item {
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    thickness = 1.dp,
                    color = Color.LightGray
                )

                Spacer(modifier = Modifier.height(22.dp))
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        BikeBrandButton(
                            text = "ALL",
                            selected = true
                        )
                    }

                    item {
                        BikeBrandButton(
                            text = "Honda",
                            selected = false
                        )
                    }

                    item {
                        BikeBrandButton(
                            text = "Yamaha",
                            selected = false
                        )
                    }

                    item {
                        BikeBrandButton(
                            text = "Duke",
                            selected = false
                        )
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))
            }

            item {
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

                Spacer(modifier = Modifier.height(20.dp))
            }

            item {
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
            }

            items(filteredBikes) { bike ->

                BikeCard(
                    bike = bike,
                    onClick = {
                        val intent = Intent(context, BikeDetails::class.java)
                        intent.putExtra("vehicleId", bike.vehicleId)
                        context.startActivity(intent)
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }
}

@Composable
fun BikeBrandButton(
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
fun BikeCard(
    bike: VehicleModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(135.dp)
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.bike),
                contentDescription = bike.name,
                modifier = Modifier
                    .width(115.dp)
                    .height(85.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = bike.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = bike.location,
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = bike.price,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00A859)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Button(
                    onClick = onClick,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF202A2A)
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 3.dp),
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
fun BikePreview() {
    DriveSafeTheme {
        BikeSearchBody()
    }
}