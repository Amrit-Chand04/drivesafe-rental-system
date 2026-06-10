package com.example.drivesafe.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drivesafe.R
import com.example.drivesafe.model.VehicleModel
import com.example.drivesafe.ui.theme.DriveSafeTheme
import com.example.drivesafe.viewmodel.VehicleViewModel

class AdminManageVehicle : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            DriveSafeTheme {
                VehicleBody()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleBody() {

    var selectedIndex by remember { mutableStateOf(0) }
    var selectedCategory by remember { mutableStateOf("All") }

    val context = LocalContext.current
    val vehicleViewModel: VehicleViewModel = viewModel()

    var vehicles by remember {
        mutableStateOf<List<VehicleModel>>(emptyList())
    }

    var showDialog by remember { mutableStateOf(false) }
    var showUpdateDialog by remember { mutableStateOf(false) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var deleteVehicleId by remember { mutableStateOf("") }

    var vehicleId by remember { mutableStateOf("") }
    var updateName by remember { mutableStateOf("") }
    var updateType by remember { mutableStateOf("") }
    var updateNumber by remember { mutableStateOf("") }
    var updateStatus by remember { mutableStateOf("") }
    var updateImageName by remember { mutableStateOf("") }
    var updatePrice by remember { mutableStateOf("") }
    var updateLocation by remember { mutableStateOf("") }
    var updateDescription by remember { mutableStateOf("") }
    var updateCapacity by remember { mutableStateOf("") }
    var updateEngine by remember { mutableStateOf("") }
    var updateSpeed by remember { mutableStateOf("") }
    var updateBattery by remember { mutableStateOf("") }
    var updateParking by remember { mutableStateOf("") }
    var updateSafety by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        vehicleViewModel.getVehicles { success, message, list ->
            if (success) {
                vehicles = list
            } else {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    val filteredVehicles = if (selectedCategory == "All") {
        vehicles
    } else {
        vehicles.filter {
            it.type.equals(selectedCategory, ignoreCase = true)
        }
    }

    if (showUpdateDialog) {
        AlertDialog(
            onDismissRequest = { showUpdateDialog = false },
            title = { Text("Update Vehicle") },
            text = {
                Column(
                    modifier = Modifier
                        .heightIn(max = 500.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    OutlinedTextField(
                        value = updateName,
                        onValueChange = { updateName = it },
                        label = { Text("Vehicle Name") },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = updateType,
                        onValueChange = { updateType = it },
                        label = { Text("Type: Car or Bike") },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = updateNumber,
                        onValueChange = { updateNumber = it },
                        label = { Text("Vehicle Number") },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = updateStatus,
                        onValueChange = { updateStatus = it },
                        label = { Text("Status") },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = updateImageName,
                        onValueChange = { updateImageName = it },
                        label = { Text("Image Name: car or bike") },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = updatePrice,
                        onValueChange = { updatePrice = it },
                        label = { Text("Price") },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = updateLocation,
                        onValueChange = { updateLocation = it },
                        label = { Text("Location") },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = updateDescription,
                        onValueChange = { updateDescription = it },
                        label = { Text("Description") },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = updateCapacity,
                        onValueChange = { updateCapacity = it },
                        label = { Text("Capacity") },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = updateEngine,
                        onValueChange = { updateEngine = it },
                        label = { Text("Engine") },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = updateSpeed,
                        onValueChange = { updateSpeed = it },
                        label = { Text("Speed") },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = updateBattery,
                        onValueChange = { updateBattery = it },
                        label = { Text("Battery") },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = updateParking,
                        onValueChange = { updateParking = it },
                        label = { Text("Parking") },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = updateSafety,
                        onValueChange = { updateSafety = it },
                        label = { Text("Safety") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val vehicle = VehicleModel(
                            vehicleId = vehicleId,
                            name = updateName,
                            type = updateType,
                            number = updateNumber,
                            status = updateStatus,
                            imageName = updateImageName,
                            price = "Rs. ${updatePrice.filter { it.isDigit() }}/Day",
                            location = updateLocation,
                            description = updateDescription,
                            capacity = updateCapacity,
                            engine = updateEngine,
                            speed = updateSpeed,
                            battery = updateBattery,
                            parking = updateParking,
                            safety = updateSafety
                        )

                        vehicleViewModel.updateVehicle(vehicleId, vehicle) { success, message ->
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            if (success) {
                                showUpdateDialog = false
                            }
                        }
                    }
                ) {
                    Text("Update")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUpdateDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showDeleteDialog) {

        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
            },
            title = {
                Text("Delete Vehicle")
            },
            text = {
                Text(
                    "Are you sure you want to delete this vehicle?"
                )
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        vehicleViewModel.deleteVehicle(deleteVehicleId) { success, message ->

                            if (success) {
                                Toast.makeText(
                                    context,
                                    "Vehicle deleted successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            showDeleteDialog = false
                        }
                    }
                ) {
                    Text("OK")
                }
            },

            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Vehicles",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(id = R.drawable.outline_arrow_back_ios_24),
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(id = R.drawable.rounded_add_alert_24),
                            contentDescription = "Notification",
                            tint = Color.Gray
                        )
                    }

                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_supervised_user_circle_24),
                            contentDescription = "Profile",
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
            NavigationBar(containerColor = Color.White) {

                NavigationBarItem(
                    selected = selectedIndex == 0,
                    onClick = { selectedIndex = 0 },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_home_24),
                            contentDescription = "Home"
                        )
                    },
                    label = { Text("Home") }
                )

                NavigationBarItem(
                    selected = selectedIndex == 1,
                    onClick = { selectedIndex = 1 },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_inbox_24),
                            contentDescription = "Inbox"
                        )
                    },
                    label = { Text("Inbox") }
                )

                NavigationBarItem(
                    selected = selectedIndex == 2,
                    onClick = { selectedIndex = 2 },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_supervised_user_circle_24),
                            contentDescription = "Profile"
                        )
                    },
                    label = { Text("Profile") }
                )
            }
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF3F8F5))
                .padding(paddingValues)
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {

            item {
                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        placeholder = {
                            Text(
                                text = "Search vehicles...",
                                fontSize = 13.sp
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(55.dp),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )

                    Button(
                        onClick = {},
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White
                        ),
                        border = BorderStroke(1.dp, Color.LightGray),
                        modifier = Modifier.height(55.dp)
                    ) {

                        Image(
                            painter = painterResource(id = R.drawable.filter),
                            contentDescription = "Filter",
                            modifier = Modifier.size(18.dp),
                            contentScale = ContentScale.Fit
                        )

                        Spacer(modifier = Modifier.width(5.dp))

                        Text(
                            text = "Filter",
                            color = Color(0xFF00A859),
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        CategoryButton(
                            text = "All Vehicles",
                            selected = selectedCategory == "All",
                            onClick = { selectedCategory = "All" }
                        )
                    }

                    item {
                        CategoryButton(
                            text = "Cars",
                            selected = selectedCategory == "Car",
                            onClick = { selectedCategory = "Car" }
                        )
                    }

                    item {
                        CategoryButton(
                            text = "Bikes",
                            selected = selectedCategory == "Bike",
                            onClick = { selectedCategory = "Bike" }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {

                    Button(
                        onClick = { showDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00A859)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.height(42.dp)
                    ) {

                        Text(
                            text = "+ Add Vehicle",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            items(filteredVehicles) { vehicle ->

                val image = if (
                    vehicle.imageName.lowercase() == "bike" ||
                    vehicle.type.equals("Bike", ignoreCase = true)
                ) {
                    R.drawable.bike
                } else {
                    R.drawable.car
                }

                VehicleCard(
                    image = image,
                    name = vehicle.name,
                    type = vehicle.type,
                    number = vehicle.number,
                    status = vehicle.status,
                    onEdit = {
                        vehicleId = vehicle.vehicleId
                        updateName = vehicle.name
                        updateType = vehicle.type
                        updateNumber = vehicle.number
                        updateStatus = vehicle.status
                        updateImageName = vehicle.imageName

                        updatePrice = vehicle.price
                        updateLocation = vehicle.location
                        updateDescription = vehicle.description
                        updateCapacity = vehicle.capacity
                        updateEngine = vehicle.engine
                        updateSpeed = vehicle.speed
                        updateBattery = vehicle.battery
                        updateParking = vehicle.parking
                        updateSafety = vehicle.safety
                        showUpdateDialog = true
                    },
                    onDelete = {

                        deleteVehicleId = vehicle.vehicleId
                        showDeleteDialog = true
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }

    if (showDialog) {
        AddVehicleDialog(
            onDismiss = {
                showDialog = false
            },
            onAdd = { vehicle ->
                vehicleViewModel.addVehicle(vehicle) { success, message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

                    if (success) {
                        showDialog = false
                    }
                }
            }
        )
    }
}

@Composable

fun AddVehicleDialog(
    onDismiss: () -> Unit,
    onAdd: (VehicleModel) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Available") }
    var imageName by remember { mutableStateOf("car") }
    var price by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var capacity by remember { mutableStateOf("") }
    var engine by remember { mutableStateOf("") }
    var speed by remember { mutableStateOf("") }
    var battery by remember { mutableStateOf("") }
    var parking by remember { mutableStateOf("") }
    var safety by remember { mutableStateOf("") }

    val context = LocalContext.current
    var typeExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Add Vehicle")
        },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 500.dp)
                    .verticalScroll(rememberScrollState())
            ) {

                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Vehicle Name") }, singleLine = true)
                Spacer(modifier = Modifier.height(8.dp))

                Box {
                    OutlinedTextField(
                        value = type,
                        onValueChange = {},
                        label = { Text("Type") },
                        readOnly = true,
                        singleLine = true,
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    typeExpanded = true
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown"
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    DropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = {
                            typeExpanded = false
                        }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Car") },
                            onClick = {
                                type = "Car"
                                imageName = "car"
                                typeExpanded = false
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Bike") },
                            onClick = {
                                type = "Bike"
                                imageName = "bike"
                                typeExpanded = false
                            }
                        )
                    }
                }

                OutlinedTextField(value = number, onValueChange = { number = it }, label = { Text("Vehicle Number") }, singleLine = true)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(value = status, onValueChange = { status = it }, label = { Text("Status") }, singleLine = true)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(value = imageName, onValueChange = { imageName = it }, label = { Text("Image Name: car or bike") }, singleLine = true)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Price") }, singleLine = true)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location") }, singleLine = true)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, singleLine = true)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(value = capacity, onValueChange = { capacity = it }, label = { Text("Capacity") }, singleLine = true)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(value = engine, onValueChange = { engine = it }, label = { Text("Engine") }, singleLine = true)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(value = speed, onValueChange = { speed = it }, label = { Text("Speed") }, singleLine = true)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(value = battery, onValueChange = { battery = it }, label = { Text("Battery") }, singleLine = true)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(value = parking, onValueChange = { parking = it }, label = { Text("Parking") }, singleLine = true)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(value = safety, onValueChange = { safety = it }, label = { Text("Safety") }, singleLine = true)
            }
        },
        confirmButton = {
            Button(
                onClick = {

                    if (

                        name.isBlank() ||
                        type.isBlank() ||
                        number.isBlank() ||
                        status.isBlank() ||
                        imageName.isBlank() ||
                        price.isBlank() ||
                        location.isBlank() ||
                        description.isBlank() ||
                        capacity.isBlank() ||
                        engine.isBlank() ||
                        speed.isBlank() ||
                        battery.isBlank() ||
                        parking.isBlank() ||
                        safety.isBlank()
                    ) {
                        Toast.makeText(
                            context,
                            "Please fill all fields",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    val vehicle = VehicleModel(
                        name = name,
                        type = type,
                        number = number,
                        status = status,
                        imageName = imageName,
                        price = "Rs. $price/Day",
                        location = location,
                        description = description,
                        capacity = capacity,
                        engine = engine,
                        speed = speed,
                        battery = battery,
                        parking = parking,
                        safety = safety
                    )
                    onAdd(vehicle)
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun CategoryButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor =
                if (selected)
                    Color(0xFF00A859)
                else
                    Color.White
        ),
        shape = RoundedCornerShape(25.dp),
        border =
            if (!selected)
                BorderStroke(1.dp, Color.LightGray)
            else
                null,
        contentPadding = PaddingValues(
            horizontal = 14.dp,
            vertical = 5.dp
        ),
        modifier = Modifier.height(38.dp)
    ) {

        Text(
            text = text,
            fontSize = 13.sp,
            color =
                if (selected)
                    Color.White
                else
                    Color.Black
        )
    }
}

@Composable
fun VehicleCard(
    image: Int,
    name: String,
    type: String,
    number: String,
    status: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(id = image),
                contentDescription = name,
                modifier = Modifier
                    .width(70.dp)
                    .height(50.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF30323A),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = type,
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = number,
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = status,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00A859)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Button(
                        onClick = onEdit,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00A859)
                        )
                    ) {
                        Text("Edit")
                    }

                    Button(
                        onClick = onDelete,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red
                        )
                    ) {
                        Text("Delete")
                    }
                }
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
fun VehiclePreview() {
    DriveSafeTheme {
        VehicleBody()
    }
}