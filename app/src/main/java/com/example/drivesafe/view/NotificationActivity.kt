package com.example.drivesafe.view

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drivesafe.R
import com.example.drivesafe.viewmodel.OfferViewModel

class NotificationActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            NotificationBody()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationBody() {

    val context = LocalContext.current
    val vm: OfferViewModel = viewModel()

    val offers by vm.offers.collectAsState()
    val isLoading by vm.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        vm.loadOffers()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Notification",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { (context as Activity).finish() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
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
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {

            Spacer(modifier = Modifier.height(13.dp))

            HorizontalDivider(
                thickness = 1.dp,
                color = Color.LightGray
            )

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(color = Color(0xFF00A859))
                    }

                    offers.isEmpty() -> {
                        Text(
                            text = "No notifications yet",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(offers) { offer ->

                                NotificationCard(
                                    title = "New Offer: ${offer.title}",
                                    message = "${offer.description} (${offer.discount}% OFF)",
                                    time = offer.startDate
                                )

                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationCard(
    title: String,
    message: String,
    time: String
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
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
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {

            Box(
                modifier = Modifier
                    .size(45.dp)
                    .background(
                        Color(0xFFEAF7F0),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    painter = painterResource(
                        id = R.drawable.baseline_notifications_none_24
                    ),
                    contentDescription = null,
                    tint = Color(0xFF00A859)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = time,
                        color = Color.Gray,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = message,
                    color = Color.Gray,
                    fontSize = 12.sp
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
fun NotificationPreview() {
    NotificationBody()
}