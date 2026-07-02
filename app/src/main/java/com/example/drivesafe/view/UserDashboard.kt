package com.example.drivesafe.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.drivesafe.R
import com.example.drivesafe.ui.theme.DriveSafeTheme
import android.content.Context
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drivesafe.viewmodel.ChatViewModel
import com.example.drivesafe.viewmodel.UserViewModel
import com.example.drivesafe.viewmodel.VehicleViewModel

class UserDashboard : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DriveSafeTheme {
                User()
            }
        }
    }
}
@Composable
fun User() {

    val context = LocalContext.current

    var selectedIndex by remember { mutableStateOf(0) }

    val vm: UserViewModel = viewModel()
    val user by vm.user.collectAsState()

    val chatViewModel: ChatViewModel = viewModel()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        vm.loadCurrentUser()
    }

    UserDashboardScaffold(
        selectedIndex = selectedIndex,
        onItemSelected = { index -> selectedIndex = index }
    ) { padding ->

        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            when (selectedIndex) {
                0 -> UserBody(
                    userName = user?.fullName ?: "User",
                    onCarClick = {
                        context.startActivity(Intent(context, CarSearchPage::class.java))
                    },
                    onBikeClick = {
                        context.startActivity(
                            Intent(context, BikeSearchPage::class.java)
                        )
                    }
                )
                1 -> UserAdminChatBody(chatViewModel)
                2 -> MyBookingScreen()
                3 -> SettingsScreen()
            }
        }
    }
}

@Composable
fun UserDashboardScaffold(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFE8F5E9),

        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 10.dp)
                    .clip(RoundedCornerShape(24.dp)),

                containerColor = Color.White,
                tonalElevation = 6.dp
            ) {

                val items = listOf(
                    "Home" to Icons.Default.Home,
                    "Inbox" to Icons.Default.ChatBubbleOutline,
                    "Booking" to Icons.Default.Add,
                    "Settings" to Icons.Default.Settings
                )

                items.forEachIndexed { index, item ->

                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = { onItemSelected(index) },

                        icon = {
                            Icon(
                                item.second,
                                contentDescription = item.first,
                                modifier = Modifier.size(22.dp)
                            )
                        },

                        label = {
                            Text(
                                item.first,
                                fontSize = 12.sp
                            )
                        },

                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF24C16B),
                            selectedTextColor = Color(0xFF24C16B),

                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,

                            indicatorColor = Color(0xFFEAF8EF)
                        )
                    )
                }
            }
        }

    ) { padding -> content(padding) }
}



@Composable
fun UserBody(
    userName: String,
    onCarClick: () -> Unit,
    onBikeClick: () -> Unit
) {

    val context = LocalContext.current
    var text by remember { mutableStateOf("") }
    val vehicleViewModel: VehicleViewModel = viewModel()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)

    ) {

        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth(),

            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(R.drawable.logo_for_app),
                contentDescription = "Logo",
                modifier = Modifier.size(64.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(onClick = {context.startActivity(Intent(context, NotificationActivity::class.java))}) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(onClick = {context.startActivity(Intent(context, ProfileUpdate::class.java))}) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Profile",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(25.dp))

        Text(
            text = "Welcome back,",
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = userName,
            fontSize = 26.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(25.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            OutlinedTextField(
                value = text ,
                onValueChange = {text = it},
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text("Search vehicle")
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.width(8.dp))

            ElevatedButton(
                onClick = {
                    val brandQuery = text.trim()

                    if (brandQuery.isBlank()) {
                        Toast.makeText(
                            context,
                            "Please enter a brand name",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@ElevatedButton
                    }

                    vehicleViewModel.getVehicles { _, _, list ->

                        val matches = list.filter {
                            (it.type.equals("Car", ignoreCase = true) ||
                                    it.type.equals("Bike", ignoreCase = true)) &&
                                    it.brand.contains(brandQuery, ignoreCase = true)
                        }

                        if (matches.isNotEmpty()) {
                            context.startActivity(
                                Intent(context, BrandSearch::class.java)
                                    .putExtra("brand", brandQuery)
                            )
                        } else {
                            Toast.makeText(
                                context,
                                "No vehicles found for this brand.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                },
                modifier = Modifier.size(width = 100.dp, height = 50.dp)
            ) {
                Text(
                    text = "Search",
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // First Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(180.dp)
                    .clickable {onCarClick() },


                shape = RoundedCornerShape(18.dp),

                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),

                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                )
            ) {

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.car_red),
                        contentDescription = "Vehicle",
                        modifier = Modifier.size(100.dp)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "CAR",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Second Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(180.dp)
                    .clickable {onBikeClick() },

                shape = RoundedCornerShape(18.dp),

                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),

                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.bike1),
                        contentDescription = "Booking",
                        modifier = Modifier.size(100.dp)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "BIKE",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Why DriveSafe?",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()


        ) {

            Text(
                "🛡️ Verified vehicles",
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                "⚡ Instant booking in seconds",
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text("🔒 Secure online payments",
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text("🕓 24/7 support",
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewUser() {
    User()
}