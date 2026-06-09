package com.example.drivesafe.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.drivesafe.R
import com.example.drivesafe.ui.theme.DriveSafeTheme

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

data class CarModel(
    val image: Int,
    val name: String,
    val location: String,
    val price: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarSearchBody() {

    var selectedIndex by remember {
        mutableStateOf(0)
    }

    val cars = emptyList<CarModel>()

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
                            painter = painterResource(
                                id = R.drawable.outline_arrow_back_ios_24
                            ),
                            contentDescription = null,
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
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    }

                    IconButton(onClick = {}) {

                        Icon(
                            painter = painterResource(
                                id = R.drawable.baseline_supervised_user_circle_24
                            ),
                            contentDescription = null,
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
                    onClick = { selectedIndex = 0 },

                    icon = {

                        Icon(
                            painter = painterResource(
                                id = R.drawable.baseline_home_24
                            ),
                            contentDescription = "Home"
                        )
                    },

                    label = {
                        Text("Home")
                    }
                )

                NavigationBarItem(
                    selected = selectedIndex == 1,
                    onClick = { selectedIndex = 1 },

                    icon = {

                        Icon(
                            painter = painterResource(
                                id = R.drawable.baseline_inbox_24
                            ),
                            contentDescription = "Inbox"
                        )
                    },

                    label = {
                        Text("Inbox")
                    }
                )

                NavigationBarItem(
                    selected = selectedIndex == 2,
                    onClick = { selectedIndex = 2 },

                    icon = {

                        Icon(
                            painter = painterResource(
                                id = R.drawable.baseline_supervised_user_circle_24
                            ),
                            contentDescription = "Profile"
                        )
                    },

                    label = {
                        Text("Profile")
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

                Spacer(modifier = Modifier.height(20.dp))

                HorizontalDivider(
                    thickness = 1.dp,
                    color = Color.LightGray
                )

                Spacer(modifier = Modifier.height(20.dp))
            }

            item {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {

                    Button(
                        onClick = {},

                        shape = CircleShape,

                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White
                        ),

                        modifier = Modifier.size(55.dp),

                        contentPadding = PaddingValues(0.dp)
                    ) {

                        Image(
                            painter = painterResource(
                                id = R.drawable.filter
                            ),

                            contentDescription = "Filter",

                            modifier = Modifier.size(22.dp),

                            contentScale = ContentScale.Fit
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }


            if (cars.isNotEmpty()) {

                items(cars) { car ->

                    CarCard(car)

                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }
    }
}

@Composable
fun CarCard(car: CarModel) {

    Card(

        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),

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
                painter = painterResource(id = car.image),

                contentDescription = null,

                modifier = Modifier
                    .width(110.dp)
                    .height(80.dp),

                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = car.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = car.location,
                    color = Color.Gray,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = car.price,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00A859),
                    fontSize = 13.sp
                )
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