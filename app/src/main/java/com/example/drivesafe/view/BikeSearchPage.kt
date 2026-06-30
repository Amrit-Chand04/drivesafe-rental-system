package com.example.drivesafe.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.drivesafe.ui.theme.DriveSafeTheme
import com.example.drivesafe.viewmodel.FavoriteViewModel
import com.example.drivesafe.viewmodel.KycViewModel
import com.example.drivesafe.viewmodel.VehicleViewModel
import com.example.drivesafe.R
import com.example.drivesafe.model.VehicleFirebaseModel


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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BikeSearchBody() {

    val context = LocalContext.current
    val vehicleViewModel: VehicleViewModel = viewModel()
    val kycViewModel: KycViewModel = viewModel()
    val favoriteViewModel: FavoriteViewModel = viewModel()

    val bikes by vehicleViewModel.vehicles.collectAsState()
    val favoriteIds by favoriteViewModel.favoriteIds.collectAsState()

    var showFilterDialog by remember { mutableStateOf(false) }
    var showKycDialog by remember { mutableStateOf(false) }

    var selectedPriceFilter by remember {
        mutableStateOf("All")
    }

    val isLoading = vehicleViewModel.isLoading.collectAsState().value

    LaunchedEffect(Unit) {
        vehicleViewModel.getVehicles { _, _, _ -> }
        favoriteViewModel.loadFavorites()
    }

    val filteredBikes = remember(selectedPriceFilter, bikes) {

        val bikeList = bikes.filter {
            it.type.equals("Bike", ignoreCase = true)
        }

        fun effectivePrice(it: com.example.drivesafe.model.VehicleFirebaseModel): Int =
            if (it.offerPercentage > 0 && it.discountedPrice > 0) it.discountedPrice
            else it.price.filter(Char::isDigit).toIntOrNull() ?: 0

        when(selectedPriceFilter){

            "Low" -> bikeList.filter { effectivePrice(it) < 3000 }

            "Medium" -> bikeList.filter { effectivePrice(it) in 3000..7000 }

            "High" -> bikeList.filter { effectivePrice(it) > 7000 }

            else -> bikeList
        }
    }

    if (showKycDialog) {
        AlertDialog(
            onDismissRequest = { showKycDialog = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    text = "KYC Required",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
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

    if (showFilterDialog) {
        AlertDialog(
            onDismissRequest = {
                showFilterDialog = false
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp),

            title = {
                Text(
                    text = "Filter by Price",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            },

            text = {

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    val filters = listOf(
                        "All" to "All",
                        "Low" to "Below Rs. 3000",
                        "Medium" to "Rs. 3000 - Rs. 7000",
                        "High" to "Above Rs. 7000"
                    )

                    filters.forEach { (key, label) ->

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = if (selectedPriceFilter == key)
                                        Color(0xFFE8F5E9)
                                    else
                                        Color.Transparent,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    selectedPriceFilter = key
                                    showFilterDialog = false
                                }
                                .padding(
                                    horizontal = 14.dp,
                                    vertical = 12.dp
                                ),

                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = label,
                                modifier = Modifier.fillMaxWidth(),
                                fontSize = 15.sp,
                                fontWeight = if (selectedPriceFilter == key)
                                    FontWeight.Bold
                                else
                                    FontWeight.Normal,
                                color = if (selectedPriceFilter == key)
                                    Color(0xFF00A859)
                                else
                                    Color.Black
                            )
                        }
                    }
                }
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        showFilterDialog = false
                    }
                ) {
                    Text(
                        "Close",
                        color = Color(0xFF00A859),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Bike Search",
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
                .background(Color(0xFFE8F5E9))
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
        ) {

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                thickness = 1.dp,
                color = Color.LightGray
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(60.dp)
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


                    Text(
                        text = selectedPriceFilter,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00A859),
                        maxLines = 1
                    )
                }
            }


            Text(
                text = "Recommend For You",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .border(
                        width = 2.dp,
                        color = Color.LightGray,
                        shape = RoundedCornerShape(28.dp)
                    ),
            ) {

                when {

                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFF00A859)
                            )
                        }
                    }

                    filteredBikes.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No bikes found",
                                color = Color.Gray
                            )
                        }
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            items(filteredBikes) { bike ->
                                BikeCard(
                                    vehicle = bike,
                                    isFavorite = favoriteIds.contains(bike.vehicleId),
                                    onFavoriteClick = { favoriteViewModel.toggleFavorite(bike.vehicleId) },
                                    onBook = {
                                        kycViewModel.checkKycStatus { isVerified ->
                                            if (isVerified) {
                                                val intent = Intent(context, BikeDetails::class.java)
                                                intent.putExtra("bikeId", bike.vehicleId)
                                                context.startActivity(intent)
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
}

@Composable
fun BikeCard(
    vehicle: VehicleFirebaseModel,
    isFavorite: Boolean = false,
    onFavoriteClick: () -> Unit = {},
    onBook: () -> Unit
) {
    //debugging

    println("VEHICLE IMAGE URL = ${vehicle.vehicleImage}")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {

        Box(modifier = Modifier.fillMaxSize()) {

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // IMAGE FROM CLOUDINARY URL
                AsyncImage(
                    model = vehicle.vehicleImage,
                    contentDescription = vehicle.name,
                    modifier = Modifier
                        .width(130.dp)
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {


                    Text(
                        text = vehicle.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )


                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )


                    Text(
                        text = "Type: ${vehicle.type}"
                    )

                    Text(
                        text = "Number: ${vehicle.number}"
                    )

                    Text(
                        text = "Brand: ${vehicle.brand}"
                    )

                    if (vehicle.offerPercentage > 0 && vehicle.discountedPrice > 0) {
                        val original = vehicle.price.filter(Char::isDigit).toIntOrNull() ?: 0
                        Text(
                            text = "Rs. $original",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            textDecoration = TextDecoration.LineThrough
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Rs. ${vehicle.discountedPrice}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00A859)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFFF6B00), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "${vehicle.offerPercentage}% OFF",
                                    fontSize = 10.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else {
                        Text(text = "Price: ${vehicle.price}")
                    }

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
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00A859)
                            )
                        ){
                            Text(
                                text = "Book",
                                fontSize = 12.sp
                            )
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
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) Color(0xFF00A859) else Color.Black
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
fun CarPreview() {
    DriveSafeTheme {
        BikeSearchBody()
    }
}