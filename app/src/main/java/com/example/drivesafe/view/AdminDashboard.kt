package com.example.drivesafe.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.drivesafe.R

class AdminDashboard : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AdminApp()
        }
    }
}

data class DashboardItem(
    val title: String,
    val icon: Int
)

@Composable
fun AdminApp() {

    var selectedIndex by remember { mutableStateOf(0) }

    var currentScreen by remember { mutableStateOf("dashboard") }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFE8F5E9),
        contentWindowInsets =
            if (selectedIndex == 2) {
                WindowInsets(0)
            } else {
                ScaffoldDefaults.contentWindowInsets
            },

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
                    "Inbox" to Icons.Default.Notifications,
                    "Offers" to Icons.Default.Add,
                    "Settings" to Icons.Default.Settings
                )

                items.forEachIndexed { index, item ->

                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },

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

    ) { padding ->

        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            when (selectedIndex) {
                0 -> AdminBody(
                    currentScreen = currentScreen,
                    onScreenChange = { currentScreen = it }
                )
                1 -> InboxScreen1()
                2 -> OffersScreen()
                3 -> SettingsScreen()
            }
        }
    }
}


@Composable
fun AdminBody(
    currentScreen: String,
    onScreenChange: (String) -> Unit
) {

    val context = LocalContext.current

    var showProfileScreen by remember {
        mutableStateOf(false)
    }

    val dashboardItems = listOf(
        DashboardItem("Manage\nUsers", R.drawable.outline_emoji_people_24),
        DashboardItem("Vehicles", R.drawable.outline_directions_car_24),
        DashboardItem("Bookings", R.drawable.outline_calendar_month_24),
        DashboardItem("KYC\nVerification", R.drawable.outline_verified_user_24)
    )

    when (currentScreen) {

        "vehicles" -> {
            VehicleBody()
            return
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
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

                IconButton(onClick = {}) {
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

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Welcome back,",
            fontSize = 22.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Admin",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(50.dp))

        dashboardItems.chunked(2).forEach { rowItems ->

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                rowItems.forEach { item ->

                    AdminCard(
                        title = item.title,
                        icon = item.icon,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            when (item.title) {

                                "Manage\nUsers" -> {
                                    println("Users clicked")

                                    val intent = Intent(context, UserManage::class.java)
                                    context.startActivity(intent)
                                }

                                "Vehicles" -> {
                                    val intent = Intent(context, AdminManageVehicle::class.java)
                                    context.startActivity(intent)
                                }

                                "Bookings" -> {
                                    println("Bookings clicked")
                                }

                                "KYC\nVerification" -> {
                                    val intent = Intent(context, ManageKycVerification::class.java)
                                    context.startActivity(intent)
                                }
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}

@Composable
fun AdminCard(
    title: String,
    icon: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {

    var pressed by remember {
        mutableStateOf(false)
    }

    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        label = ""
    )

    Card(
        modifier = modifier
            .aspectRatio(1f)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }

            .clickable {
                pressed = true
                onClick()
                pressed = false
            },

        shape = RoundedCornerShape(24.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),

            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEAF8EF)),

                contentAlignment = Alignment.Center
            ) {

                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    tint = Color(0xFF24C16B),
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}



@Composable
fun InboxScreen1() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Inbox Screen")
    }
}



@Preview(showBackground = true)
@Composable
fun PreviewAdminDashboard() {
    AdminApp()
}