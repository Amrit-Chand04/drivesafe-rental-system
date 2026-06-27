package com.example.drivesafe.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drivesafe.ui.theme.AppThemeState
import com.example.drivesafe.ui.theme.DriveSafeTheme
import com.example.drivesafe.viewmodel.AuthViewModel

class SettingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DriveSafeTheme {
                SettingsScreen(
                    userName = "User",
                    userEmail = "user@gmail.com",
                    role = "user"
                )
            }
        }
    }
}

@Composable
fun SettingsScreen(
    userName: String,
    userEmail: String,
    role: String
) {

    val context = LocalContext.current
    val vm: AuthViewModel = viewModel()

    var showAppearance by remember { mutableStateOf(false) }
    var selectedTheme by remember { mutableStateOf("System") }

    var showLogoutDialog by remember { mutableStateOf(false) }

    val isLoggedOut by vm.isLoggedOut.collectAsState()


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
            .padding(16.dp)
    ) {

        Text(
            text = "Settings",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(24.dp))

        // PROFILE CARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Row(verticalAlignment = Alignment.CenterVertically) {

                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color(0xFF24C16B),
                        modifier = Modifier.size(60.dp)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {

                        Text(
                            text = userName,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = userEmail,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                ElevatedButton(onClick = { }) {
                    Text("Edit")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if(role == "user"){
            SettingsItem(
                title = "Favourite",
                onClick = {}
            )

            SettingsItem(
                title = "Complete KYC",
                onClick = {
                    val intent = Intent(context, KycActivity::class.java)
                    context.startActivity(intent)
                }
            )
        }

        // APPEARANCE
        SettingsItem(
            title = "Appearance",
            onClick = {
                onAppearanceClick()
            }
        )

        // DROPDOWN
        if (showAppearance) {

            Column(modifier = Modifier.padding(start = 20.dp)) {

                Text(
                    "Light",
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedTheme = "Light"
                            AppThemeState.theme.value = "Light"
                            showAppearance = false
                        }
                        .padding(8.dp)
                )

                Text(
                    "Dark",
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedTheme = "Dark"
                            AppThemeState.theme.value = "Dark"
                            showAppearance = false
                        }
                        .padding(8.dp)
                )

                Text(
                    "System",
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedTheme = "System"
                            AppThemeState.theme.value = "System"
                            showAppearance = false
                        }
                        .padding(8.dp)
                )
            }
        }

        SettingsItem(
            title = "Password Change",
            onClick = {
                val intent = Intent(context, ChangePasswordActivity::class.java)
                context.startActivity(intent)
            }
        )

        SettingsItem(
            title = "Log Out",
            titleColor = Color.Red,
            onClick = {
                showLogoutDialog = true
            }
        )
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
            .padding(vertical = 8.dp)
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
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = title,
                fontSize = 18.sp,
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
    SettingsScreen(
        userName = "User",
        userEmail = "user@gmail.com",
        role = "user"
    )
}