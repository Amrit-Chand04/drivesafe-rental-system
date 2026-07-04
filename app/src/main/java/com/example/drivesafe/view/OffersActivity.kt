package com.example.drivesafe.view

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drivesafe.model.OfferModel
import com.example.drivesafe.ui.theme.DriveSafeTheme
import com.example.drivesafe.viewmodel.OfferViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OffersActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DriveSafeTheme {
                OffersScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OffersScreen() {

    val context = LocalContext.current
    val vm: OfferViewModel = viewModel()

    val offersList by vm.offers.collectAsState()
    val isLoading by vm.isLoading.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var selectedOffer by remember { mutableStateOf<OfferModel?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        vm.loadOffers()
    }

    if (showDeleteDialog && selectedOffer != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp),
            title = { Text("Delete Offer", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete this offer?", color = Color.Gray) },
            confirmButton = {
                Button(
                    onClick = {
                        vm.deleteOffer(selectedOffer!!.id) { success, _ ->
                            showDeleteDialog = false
                            if (success) vm.loadOffers()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier.testTag("confirmDeleteButton")
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false },
                    modifier = Modifier.testTag("cancelDeleteButton")
                ) {
                    Text("Cancel", color = Color(0xFF00A859))
                }
            }
        )
    }

    CreateOfferDialog(
        showDialog = showDialog,
        vm = vm,
        onDismiss = { showDialog = false }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Offer Management",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFEAF8EE)
                )
            )
        },
        containerColor = Color(0xFFEAF8EE)
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFEAF8EE))
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {


            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {


                Button(
                    onClick = { showDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A859)),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("createOfferButton")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Create Offer", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = "Active Offers",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(bottom = 16.dp)
                    .border(
                        width = 2.dp,
                        color = Color.LightGray,
                        shape = RoundedCornerShape(28.dp)
                    )
            ) {
                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF00A859))
                        }
                    }
                    offersList.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No active offers available.",
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(offersList) { offer ->
                                OfferCardItem(
                                    offer = offer,
                                    onClick = {
                                        selectedOffer = offer
                                        showDeleteDialog = true
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateOfferDialog(
    showDialog: Boolean,
    vm: OfferViewModel,
    onDismiss: () -> Unit
) {

    if (!showDialog) return

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var discount by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var openStart by remember { mutableStateOf(false) }
    var openEnd by remember { mutableStateOf(false) }

    val startState = rememberDatePickerState()
    val endState = rememberDatePickerState()

    val context = LocalContext.current
    val toastMessage by vm.toast.collectAsState()
    val isLoading by vm.isLoading.collectAsState()

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            vm.clear()
        }
    }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color(0xFF00A859),
        focusedLabelColor = Color(0xFF00A859),
        cursorColor = Color(0xFF00A859)
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                Text(
                    text = "Create Offer",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("titleField"),
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("descriptionField"),
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = discount,
                    onValueChange = { if (it.all { ch -> ch.isDigit() }) discount = it },
                    label = { Text("Discount Percentage") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("discountField"),
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors
                )

                Spacer(modifier = Modifier.height(10.dp))

                // START DATE
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { openStart = true }
                        .border(1.dp, Color(0xFF00A859), RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (startDate.isEmpty()) "Select Start Date" else startDate,
                            color = if (startDate.isEmpty()) Color.Gray else Color.Black,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = Color(0xFF00A859)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // END DATE
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { openEnd = true }
                        .border(1.dp, Color(0xFF00A859), RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (endDate.isEmpty()) "Select End Date" else endDate,
                            color = if (endDate.isEmpty()) Color.Gray else Color.Black,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = Color(0xFF00A859)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("cancelButton")
                    ) {
                        Text("Cancel", color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        enabled = !isLoading,
                        onClick = {
                            val offer = OfferModel(
                                title = title,
                                description = description,
                                discount = discount.toIntOrNull() ?: 0,
                                startDate = startDate,
                                endDate = endDate
                            )
                            vm.createOffer(offer) { success, _ ->
                                if (success) {
                                    vm.loadOffers()
                                    onDismiss()
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A859)),
                        modifier = Modifier.testTag("saveButton")
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                        } else {
                            Text("Save", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (openStart) {
        DatePickerDialog(
            onDismissRequest = { openStart = false },
            confirmButton = {
                Button(
                    onClick = {
                        startDate = startState.selectedDateMillis?.let { formatDate(it) } ?: ""
                        openStart = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A859))
                ) { Text("OK") }
            }
        ) { DatePicker(state = startState) }
    }

    if (openEnd) {
        DatePickerDialog(
            onDismissRequest = { openEnd = false },
            confirmButton = {
                Button(
                    onClick = {
                        endDate = endState.selectedDateMillis?.let { formatDate(it) } ?: ""
                        openEnd = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A859))
                ) { Text("OK") }
            }
        ) { DatePicker(state = endState) }
    }
}

fun formatDate(millis: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(Date(millis))
}

@Composable
fun OfferCardItem(
    offer: OfferModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("offerCard")
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF24C16B), Color(0xFF0A6640))
                    )
                )
                .padding(16.dp)
        ) {
            Column {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = offer.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )

                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.25f), RoundedCornerShape(50.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${offer.discount}% OFF",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = offer.description, color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp)

                Spacer(modifier = Modifier.height(10.dp))

                HorizontalDivider(color = Color.White.copy(alpha = 0.3f), thickness = 1.dp)

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Start: ${offer.startDate}",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 13.sp
                    )
                    Text(
                        text = "End: ${offer.endDate}",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OfferPreview() {
    DriveSafeTheme {
        OffersScreen()
    }
}