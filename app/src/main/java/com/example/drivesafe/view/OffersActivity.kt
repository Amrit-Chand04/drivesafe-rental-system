package com.example.drivesafe.view

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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drivesafe.model.OfferModel
import com.example.drivesafe.viewmodel.OfferViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OffersActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OffersScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OffersScreen() {
    var showDialog by remember { mutableStateOf(false) }

    var selectedOffer by remember { mutableStateOf<OfferModel?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val vm: OfferViewModel = viewModel()

    val offersList = vm.offers.collectAsState().value

    LaunchedEffect(Unit) {
        vm.loadOffers()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {

        Text(
            text = "Create Offer",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)
        )

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.4.dp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(35.dp))

        Text(
            text = "Offer Management",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Create and manage promotional offers\nto boost your bookings.",
            color = Color.Gray,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
                ElevatedButton(
                    onClick = {
                        showDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.45f)
                        .height(50.dp)
                        .wrapContentWidth(Alignment.End),
                    shape = RoundedCornerShape(10.dp),
                    elevation = ButtonDefaults.elevatedButtonElevation(
                        defaultElevation = 8.dp
                    ),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = Color.Transparent
                    ),
                    contentPadding = PaddingValues()
                ) {

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF00C853),
                                        Color(0xFF1E88E5)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {

                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "Create Offer",
                                color = Color.White,
                                fontSize = 20.sp
                            )
                        }
                    }
                }

                CreateOfferDialog(
                    showDialog = showDialog,
                    vm = vm,
                    onDismiss = {
                        showDialog = false
                    }
                )
            }

        Spacer(modifier = Modifier.height(44.dp))

        Text(
            text = "Active Offers",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(22.dp))

        Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(bottom = 16.dp)
                    .border(
                        width = 1.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp)
            ) {

                if(offersList.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No active offers available.",
                            fontSize = 22.sp,
                            color = Color.Gray
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
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
    if (showDeleteDialog && selectedOffer != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Offer") },
            text = { Text("Are you sure you want to delete this offer?") },
            confirmButton = {
                TextButton(onClick = {
                    vm.deleteOffer(selectedOffer!!.id) { success, msg ->
                        showDeleteDialog = false
                        if (success) {
                            vm.loadOffers()
                        }
                    }
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                }) {
                    Text("Cancel")
                }
            }
        )
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
    val toastMessage = vm.toast.collectAsState().value
    val isLoading = vm.isLoading.collectAsState().value

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            vm.clear()
        }
    }

    Dialog(onDismissRequest = onDismiss) {

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFEAF8EE)
            )
        ) {

            Column(modifier = Modifier.padding(16.dp)) {

                Text("Create Offer", fontSize = 20.sp, fontWeight = FontWeight.Bold)

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = discount,
                    onValueChange = {
                        if (it.all { ch -> ch.isDigit() }) {
                            discount = it
                        }
                    },
                    label = {
                        Text("Discount Percentage")
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
                Spacer(Modifier.height(8.dp))

                // START DATE
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { openStart = true }
                        .border(
                            width = 1.dp,
                            color = Color(0xFF888888),
                            shape = RoundedCornerShape(7.dp)
                        )
                        .background(Color(0xFFEAF8EE), RoundedCornerShape(10.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (startDate.isEmpty()) "Select Start Date" else startDate,
                            color = if (startDate.isEmpty()) Color(0xFF888888) else Color.Black,
                            modifier = Modifier.weight(1f)
                        )

                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                // END DATE
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { openEnd = true }
                        .border(
                            width = 1.dp,
                            color = Color(0xFF888888),
                            shape = RoundedCornerShape(7.dp)
                        )
                        .background(Color(0xFFEAF8EE), RoundedCornerShape(10.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (endDate.isEmpty()) "Select End Date" else endDate,
                            color = if (endDate.isEmpty()) Color(0xFF888888) else Color.Black,
                            modifier = Modifier.weight(1f)
                        )

                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {

                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }

                    Spacer(Modifier.width(8.dp))

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

                            vm.createOffer(offer) { success, message ->
                                if (success) {
                                    vm.loadOffers()
                                    onDismiss()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00C853)
                        )
                    ) {

                        if (isLoading) {

                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )

                        } else {

                            Text(
                                text = "Save",
                                color = Color.White
                            )

                        }
                    }
                }
            }
        }
    }

    // Start date picker
    if (openStart) {
        DatePickerDialog(
            onDismissRequest = { openStart = false },
            confirmButton = {
                Button(onClick = {
                    startDate = startState.selectedDateMillis?.let {
                        formatDate(it)
                    } ?: ""
                    openStart = false
                }) {
                    Text("OK")
                }
            }
        ) {
            DatePicker(state = startState)
        }
    }

    // End date picker
    if (openEnd) {
        DatePickerDialog(
            onDismissRequest = { openEnd = false },
            confirmButton = {
                Button(onClick = {
                    endDate = endState.selectedDateMillis?.let {
                        formatDate(it)
                    } ?: ""
                    openEnd = false
                }) {
                    Text("OK")
                }
            }
        ) {
            DatePicker(state = endState)
        }
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
        modifier = Modifier.fillMaxWidth()
            .clickable(onClick = onClick ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF00C853),
                            Color(0xFF1E88E5)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Column {

                Text(
                    text = offer.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(7.dp))

                Text(
                    text = offer.description,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Discount: ${offer.discount}%",
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Start: ${offer.startDate}",
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "End: ${offer.endDate}",
                    color = Color.White
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OfferPreview() {
    OffersScreen()
}