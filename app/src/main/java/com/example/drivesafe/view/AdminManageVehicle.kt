package com.example.drivesafe.view

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.drivesafe.R
import com.example.drivesafe.model.VehicleFirebaseModel
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
fun VehicleBody(){

    var selectedCategory by remember { mutableStateOf("All") }

    val context = LocalContext.current
    val vehicleViewModel: VehicleViewModel = viewModel()

    val vehicles by vehicleViewModel.vehicles.collectAsState()

    var showDialog by remember { mutableStateOf(false) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedVehicleId by remember { mutableStateOf("") }

    var showEditDialog by remember { mutableStateOf(false) }
    var selectedVehicle by remember { mutableStateOf<VehicleFirebaseModel?>(null) }


    val isLoading = vehicleViewModel.isLoading.collectAsState().value

    LaunchedEffect(Unit) {
        vehicleViewModel.getVehicles { success, message, list ->

            if (!success) {
                Toast.makeText(
                    context,
                    message,
                    Toast.LENGTH_SHORT
                ).show()
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

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Manage Vehicle",
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

        if (showDialog) {
            AddVehicleDialog(
                isLoading = isLoading,
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

        if (showEditDialog && selectedVehicle != null) {

            AddVehicleDialog(
                vehicle = selectedVehicle,
                isLoading = isLoading,
                onDismiss = {
                    showEditDialog = false
                },
                onUpdate = { updatedVehicle ->

                    vehicleViewModel.updateVehicle(updatedVehicle) { success, message ->

                        Toast.makeText(
                            context,
                            message,
                            Toast.LENGTH_SHORT
                        ).show()

                        if (success) {
                            showEditDialog = false
                        }
                    }

                },
                onAdd = {}
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
                    Text("Are you sure you want to delete this vehicle?")
                },

                confirmButton = {

                    Button(
                        onClick = {

                            vehicleViewModel.deleteVehicle(
                                selectedVehicleId
                            ) { success, message ->

                                Toast.makeText(
                                    context,
                                    message,
                                    Toast.LENGTH_SHORT
                                ).show()

                            }

                            showDeleteDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red
                        )
                    ) {
                        Text(
                            "Delete",
                            color = Color.White
                        )
                    }
                },

                dismissButton = {

                    Button(
                        onClick = {
                            showDeleteDialog = false
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE8F5E9))
                .padding(paddingValues)
                .padding(16.dp)
                .padding(horizontal = 14.dp)
                .verticalScroll(rememberScrollState())
        ) {

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

            Spacer(modifier = Modifier.height(26.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .border(
                        width = 2.dp,
                        color = Color.LightGray,
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(
                            color = Color(0xFF00A859)
                        )
                    }

                    filteredVehicles.isEmpty() -> {
                        Text(
                            text = "No vehicles found",
                            color = Color.Gray
                        )
                    }

                    else -> {

                        LazyColumn(
                            modifier = Modifier.padding(6.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            items(filteredVehicles) { vehicle ->
                                VehicleCard(
                                    vehicle = vehicle,
                                    onEdit = {
                                        selectedVehicle = vehicle
                                        showEditDialog = true
                                    },
                                    onDelete = {
                                        selectedVehicleId = vehicle.vehicleId
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
// my name is amrit chand thakuri and this is the part of

// the testing and debugging process
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVehicleDialog(
    vehicle: VehicleFirebaseModel? = null,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onAdd: (VehicleModel) -> Unit,
    onUpdate: (VehicleFirebaseModel) -> Unit = {}
) {

    val context = LocalContext.current

    var vehicleName by remember {
        mutableStateOf(vehicle?.name ?: "")
    }

    var vehicleType by remember {
        mutableStateOf(vehicle?.type ?: "Car")
    }

    var vehicleNumber by remember {
        mutableStateOf(vehicle?.number ?: "")
    }

    var vehicleBrand by remember {
        mutableStateOf(vehicle?.brand ?: "")
    }

    var status by remember {
        mutableStateOf(vehicle?.status ?: "Available")
    }

    var statusExpanded by remember {
        mutableStateOf(false)
    }

    var pricePerDay by remember {
        mutableStateOf(
            vehicle?.price
                ?.replace("Rs. ", "")
                ?.replace("/Day", "")
                ?: ""
        )
    }

    var description by remember {
        mutableStateOf(vehicle?.description ?: "")
    }

    var capacity by remember {
        mutableStateOf(vehicle?.capacity ?: "")
    }

    var engine by remember {
        mutableStateOf(vehicle?.engine ?: "")
    }

    var speed by remember {
        mutableStateOf(vehicle?.speed ?: "")
    }

    var fuelType by remember {
        mutableStateOf(vehicle?.fuelType ?: "EV")
    }

    var fuelTypeExpanded by remember {
        mutableStateOf(false)
    }

    var vehicleTypeExpanded by remember {
        mutableStateOf(false)
    }

    var vehicleImage by remember {
        mutableStateOf<Uri?>(null)
    }

    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        vehicleImage = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (vehicle == null)
                    "Add Vehicle"
                else
                    "Update Vehicle"
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 500.dp)
                    .verticalScroll(rememberScrollState())
            ) {

                // Vehicle Name
                OutlinedTextField(
                    value = vehicleName,
                    onValueChange = { vehicleName = it },
                    label = { Text("Vehicle Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Vehicle Type Dropdown
                ExposedDropdownMenuBox(
                    expanded = vehicleTypeExpanded,
                    onExpandedChange = {
                        vehicleTypeExpanded = !vehicleTypeExpanded
                    }
                ) {
                    OutlinedTextField(
                        value = vehicleType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Vehicle Type") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = vehicleTypeExpanded
                            )
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = vehicleTypeExpanded,
                        onDismissRequest = {
                            vehicleTypeExpanded = false
                        }
                    ) {
                        listOf("Car", "Bike").forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    vehicleType = item
                                    vehicleTypeExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Vehicle Number
                OutlinedTextField(
                    value = vehicleNumber,
                    onValueChange = { vehicleNumber = it },
                    label = { Text("Vehicle Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Vehicle Brand
                OutlinedTextField(
                    value = vehicleBrand,
                    onValueChange = { vehicleBrand = it },
                    label = { Text("Brand") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Status
                if (vehicle == null) {

                    OutlinedTextField(
                        value = "Available",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Status") },
                        modifier = Modifier.fillMaxWidth()
                    )

                } else {

                    ExposedDropdownMenuBox(
                        expanded = statusExpanded,
                        onExpandedChange = {
                            statusExpanded = !statusExpanded
                        }
                    ) {

                        OutlinedTextField(
                            value = status,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Status") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = statusExpanded
                                )
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = statusExpanded,
                            onDismissRequest = {
                                statusExpanded = false
                            }
                        ) {

                            listOf(
                                "Available",
                                "Maintenance"
                            ).forEach { item ->

                                DropdownMenuItem(
                                    text = { Text(item) },
                                    onClick = {
                                        status = item
                                        statusExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }


                Spacer(modifier = Modifier.height(8.dp))

                // Price Per Day
                OutlinedTextField(
                    value = pricePerDay,
                    onValueChange = { pricePerDay = it },
                    label = { Text("Price Per Day") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Capacity
                OutlinedTextField(
                    value = capacity,
                    onValueChange = { capacity = it },
                    label = { Text("Capacity") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Engine
                OutlinedTextField(
                    value = engine,
                    onValueChange = { engine = it },
                    label = { Text("Engine") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                //Fuel Type
                ExposedDropdownMenuBox(
                    expanded = fuelTypeExpanded,
                    onExpandedChange = {
                        fuelTypeExpanded = !fuelTypeExpanded
                    }
                ) {
                    OutlinedTextField(
                        value = fuelType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Fuel Type") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = fuelTypeExpanded
                            )
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = fuelTypeExpanded,
                        onDismissRequest = {
                            fuelTypeExpanded = false
                        }
                    ) {
                        listOf(
                            "EV",
                            "Petrol",
                            "Diesel",
                        ).forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    fuelType = item
                                    fuelTypeExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Speed
                OutlinedTextField(
                    value = speed,
                    onValueChange = { speed = it },
                    label = { Text("Speed") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Vehicle Image

                UploadBox(
                    title = "Upload Vehicle Image",
                    selected = vehicleImage != null
                ) {
                    photoLauncher.launch("image/*")
                }
            }
        },

        confirmButton = {
            Button(
                enabled = !isLoading,
                onClick = {

                    if (
                        vehicleName.isBlank() ||
                        vehicleType.isBlank() ||
                        vehicleNumber.isBlank() ||
                        vehicleBrand.isBlank() ||
                        pricePerDay.isBlank() ||
                        description.isBlank() ||
                        capacity.isBlank() ||
                        engine.isBlank() ||
                        fuelType.isBlank() ||
                        speed.isBlank() ||
                        (vehicle == null && vehicleImage == null)
                    ) {
                        Toast.makeText(
                            context,
                            "Please fill all fields",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    if (vehicle == null) {

                        val newVehicle = VehicleModel(
                            name = vehicleName,
                            type = vehicleType,
                            fuelType = fuelType,
                            number = vehicleNumber,
                            brand = vehicleBrand,
                            status = status,
                            vehicleImage = vehicleImage,
                            price = "Rs. $pricePerDay/Day",
                            description = description,
                            capacity = capacity,
                            engine = engine,
                            speed = speed
                        )

                        onAdd(newVehicle)

                    } else {

                        val newImage =
                            if (vehicleImage != null) {
                                vehicleImage.toString()
                            } else {
                                vehicle.vehicleImage
                            }


                        val isChanged =

                            vehicle.name != vehicleName ||
                                    vehicle.type != vehicleType ||
                                    vehicle.fuelType != fuelType ||
                                    vehicle.number != vehicleNumber ||
                                    vehicle.brand != vehicleBrand ||
                                    vehicle.status != status ||
                                    vehicle.price != "Rs. $pricePerDay/Day" ||
                                    vehicle.description != description ||
                                    vehicle.capacity != capacity ||
                                    vehicle.engine != engine ||
                                    vehicle.speed != speed ||
                                    vehicle.vehicleImage != newImage


                        if (!isChanged) {

                            Toast.makeText(
                                context,
                                "No changes made",
                                Toast.LENGTH_SHORT
                            ).show()

                            return@Button
                        }


                        val updatedVehicle = vehicle.copy(
                            name = vehicleName,
                            type = vehicleType,
                            fuelType = fuelType,
                            number = vehicleNumber,
                            brand = vehicleBrand,
                            status = status,
                            price = "Rs. $pricePerDay/Day",
                            description = description,
                            capacity = capacity,
                            engine = engine,
                            speed = speed,
                            vehicleImage = if (vehicleImage != null) {
                                vehicleImage.toString()
                            } else {
                                vehicle.vehicleImage
                            }
                        )
                        onUpdate(updatedVehicle)
                    }
                }
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        if (vehicle == null)
                            "Add Vehicle"
                        else
                            "Update Vehicle"
                    )
                }
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
    vehicle: VehicleFirebaseModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    //debugging

    println("VEHICLE IMAGE URL = ${vehicle.vehicleImage}")

    var imageLoading by remember { mutableStateOf(true) }

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

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {


            // IMAGE FROM CLOUDINARY URL
            Box(
                modifier = Modifier
                    .width(130.dp)
                    .height(180.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = vehicle.vehicleImage,
                    contentDescription = vehicle.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    onSuccess = { imageLoading = false },
                    onError = { imageLoading = false }
                )

                if (imageLoading) {
                    CircularProgressIndicator()
                }
            }



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

                Text(
                    text = "Price: ${vehicle.price}"
                )

                Text(
                    text = "Status: ${vehicle.status}",
                    color = Color(0xFF00A859),
                    fontWeight = FontWeight.Bold
                )


                Spacer(modifier = Modifier.height(10.dp))


                Row {

                    Button(
                        onClick = onEdit,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00A859)
                        )
                    ){
                        Text(
                            text = "Edit",
                            fontSize = 10.sp
                        )
                    }


                    Spacer(modifier = Modifier.width(8.dp))


                    Button(
                        onClick = onDelete,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red
                        )
                    ){
                        Text(
                            text = "Delete",
                            fontSize = 10.sp
                        )
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