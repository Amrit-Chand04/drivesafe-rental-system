package com.example.drivesafe.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.Brush
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drivesafe.ui.theme.AppThemeState
import com.example.drivesafe.ui.theme.DriveSafeTheme
import com.example.drivesafe.viewmodel.UserViewModel

class SettingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DriveSafeTheme {
                SettingsScreen()
            }
        }
    }
}

@Composable
fun SettingsScreen() {

    val context = LocalContext.current
    val vm: UserViewModel = viewModel()
    val user by vm.user.collectAsState()

    var showAppearance by remember { mutableStateOf(false) }
    var selectedTheme by remember { mutableStateOf("System") }

    var showLogoutDialog by remember { mutableStateOf(false) }

    val isLoggedOut by vm.isLoggedOut.collectAsState()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        vm.loadCurrentUser()
    }


    LaunchedEffect(isLoggedOut) {

        if (isLoggedOut) {

            context.startActivity(
                Intent(context, LoginActivity::class.java)
            )

            (context as? ComponentActivity)?.finish()
        }
    }

    if (showLogoutDialog) {

        AlertDialog(
            onDismissRequest = {
                showLogoutDialog = false
            },

            containerColor = Color(0xFFE8F5E9),

            title = {
                Text("Logout")
            },
            text = {
                Text("Are you sure you want to logout?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        vm.logOut()
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showLogoutDialog = false
                    }
                ) {
                    Text("No")
                }
            }
        )
    }

    fun onAppearanceClick() {
        showAppearance = !showAppearance
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE8F5E9))
            .padding(horizontal = 16.dp)
    ) {

        Text(
            text = "Settings",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {

            // PROFILE CARD
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF24C16B), Color(0xFF0A6640))
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 20.dp)
                ) {
                    // Edit button top-right
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(34.dp)
                            .background(Color.White.copy(alpha = 0.25f), CircleShape)
                            .clickable { context.startActivity(Intent(context, ProfileUpdate::class.java)) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profile",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Avatar circle with initials
                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .background(Color.White.copy(alpha = 0.2f), CircleShape)
                                .border(2.dp, Color.White.copy(alpha = 0.7f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = user?.fullName
                                    ?.split(" ")
                                    ?.mapNotNull { it.firstOrNull()?.uppercaseChar() }
                                    ?.take(2)
                                    ?.joinToString("") ?: "U",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = user?.fullName ?: "User",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Box(
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.25f), RoundedCornerShape(50.dp))
                                .padding(horizontal = 14.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = user?.role?.uppercase() ?: "",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            if (user?.role == "user") {
                item {
                    SettingsItem(
                        title = "Favourite",
                        onClick = { context.startActivity(Intent(context, FavoritesActivity::class.java)) }
                    )
                }
                item {
                    SettingsItem(
                        title = "Complete KYC",
                        onClick = { context.startActivity(Intent(context, KycActivity::class.java)) }
                    )
                }
            }

            item { SettingsItem(title = "Appearance", onClick = { onAppearanceClick() }) }

            if (showAppearance) {
                item {
                    Column(modifier = Modifier.padding(start = 20.dp)) {
                        listOf("Light", "Dark", "System").forEach { theme ->
                            Text(
                                text = theme,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedTheme = theme
                                        AppThemeState.theme.value = theme
                                        showAppearance = false
                                    }
                                    .padding(8.dp)
                            )
                        }
                    }
                }
            }

            item {
                SettingsItem(
                    title = "Password Change",
                    onClick = { context.startActivity(Intent(context, ChangePasswordActivity::class.java)) }
                )
            }

            item {
                SettingsItem(
                    title = "Log Out",
                    titleColor = Color.Red,
                    onClick = { showLogoutDialog = true }
                )
            }
        }
    }
}

@Composable
fun SettingsItem(
    title: String,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clickable { onClick() },

        shape = RoundedCornerShape(20.dp),

        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),

        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = title,
                fontSize = 15.sp,
                color = titleColor
            )

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


@Preview
@Composable
fun SettingsPreview() {
    SettingsScreen()
}