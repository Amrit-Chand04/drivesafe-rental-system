package com.example.drivesafe.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.drivesafe.model.VehicleFirebaseModel
import com.example.drivesafe.ui.theme.DriveSafeTheme
import com.example.drivesafe.viewmodel.FavoriteViewModel
import com.example.drivesafe.viewmodel.KycViewModel
import com.example.drivesafe.viewmodel.VehicleViewModel

class FavoritesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DriveSafeTheme {
                FavoritesScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen() {

    val context = LocalContext.current
    val vehicleViewModel: VehicleViewModel = viewModel()
    val favoriteViewModel: FavoriteViewModel = viewModel()
    val kycViewModel: KycViewModel = viewModel()

    val vehicles by vehicleViewModel.vehicles.collectAsState()
    val favoriteIds by favoriteViewModel.favoriteIds.collectAsState()
    val isLoading = vehicleViewModel.isLoading.collectAsState().value

    var showKycDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        vehicleViewModel.getVehicles { _, _, _ -> }
        favoriteViewModel.loadFavorites()
    }

    val favoriteVehicles = remember(vehicles, favoriteIds) {
        vehicles.filter { favoriteIds.contains(it.vehicleId) }
    }

    if (showKycDialog) {
        AlertDialog(
            onDismissRequest = { showKycDialog = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(text = "KYC Required", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    text = "You need to complete your KYC verification before booking a vehicle.",
                    fontSize = 15.sp,
                    color = Color.Gray
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showKycDialog = false
                        context.startActivity(Intent(context, KycActivity::class.java))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A859))
                ) {
                    Text("Complete KYC", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showKycDialog = false }) {
                    Text("Cancel", color = Color(0xFF00A859))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Favourites", fontSize = 26.sp, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = { (context as Activity).finish() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFEAF8EE)
                )
            )
        },
        containerColor = Color(0xFFEAF8EE)
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {

            when {

                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF00A859)
                    )
                }

                favoriteVehicles.isEmpty() -> {
                    Text(
                        text = "No favourites yet",
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.Gray
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        items(favoriteVehicles) { vehicle ->
                            FavoriteVehicleCard(
                                vehicle = vehicle,
                                onFavoriteClick = { favoriteViewModel.toggleFavorite(vehicle.vehicleId) },
                                onBook = {
                                    kycViewModel.checkKycStatus { isVerified ->
                                        if (isVerified) {
                                            if (vehicle.type.equals("Bike", ignoreCase = true)) {
                                                val intent = Intent(context, BikeDetails::class.java)
                                                intent.putExtra("bikeId", vehicle.vehicleId)
                                                context.startActivity(intent)
                                            } else {
                                                val intent = Intent(context, CarDetails::class.java)
                                                intent.putExtra("carId", vehicle.vehicleId)
                                                context.startActivity(intent)
                                            }
                                        } else {
                                            showKycDialog = true
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FavoriteVehicleCard(
    vehicle: VehicleFirebaseModel,
    onFavoriteClick: () -> Unit,
    onBook: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {

        Box(modifier = Modifier.fillMaxSize()) {

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                AsyncImage(
                    model = vehicle.vehicleImage,
                    contentDescription = vehicle.name,
                    modifier = Modifier
                        .width(130.dp)
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {

                    Text(text = vehicle.name, fontSize = 16.sp, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(text = "Type: ${vehicle.type}")
                    Text(text = "Number: ${vehicle.number}")
                    Text(text = "Price: ${vehicle.price}")
                    Text(
                        text = "Status: ${vehicle.status}",
                        color = Color(0xFF00A859),
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row {
                        Button(
                            onClick = onBook,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A859))
                        ) {
                            Text(text = "Book", fontSize = 12.sp)
                        }
                    }
                }
            }

            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Favourite",
                    tint = Color(0xFF00A859)
                )
            }
        }
    }
}