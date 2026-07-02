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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drivesafe.R
import com.example.drivesafe.model.KycModel
import com.example.drivesafe.viewmodel.KycViewModel

class KycActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KycScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KycScreen() {

    val context = LocalContext.current
    val vm: KycViewModel = viewModel()
    val toastMessage = vm.toast.collectAsState().value
    val isLoading = vm.isLoading.collectAsState().value
    val isLoadingRecord by vm.isLoadingRecord.collectAsState()
    val myKycRecord by vm.myKycRecord.collectAsState()

    LaunchedEffect(Unit) {
        vm.loadMyKycRecord()
    }

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            vm.clear()
        }
    }

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var doc by remember { mutableStateOf<Uri?>(null) }
    var photo by remember { mutableStateOf<Uri?>(null) }

    val docLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> doc = uri }

    val photoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> photo = uri }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "KYC Verification",
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

        when {
            isLoadingRecord -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF23B14D))
                }
            }

            myKycRecord != null -> {
                KycStatusView(
                    record = myKycRecord!!,
                    paddingValues = paddingValues
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp)
                        .imePadding()
                ) {
                    item {
                        Column {

                            Spacer(modifier = Modifier.height(18.dp))

                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFDFF5E3))
                                    .align(Alignment.CenterHorizontally),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color(0xFF23B14D),
                                    modifier = Modifier.size(40.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "Verify Your Identity",
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(modifier = Modifier.padding(18.dp)) {

                                    KycField(
                                        value = name,
                                        onValueChange = { name = it },
                                        label = "Full Name",
                                        icon = { Icon(Icons.Default.Person, null) }
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    KycField(
                                        value = phone,
                                        onValueChange = { phone = it },
                                        label = "Phone Number",
                                        icon = { Icon(Icons.Default.Call, null) }
                                    )

                                    Spacer(modifier = Modifier.height(22.dp))

                                    Text("Driving Licence", fontWeight = FontWeight.Bold)

                                    Spacer(modifier = Modifier.height(12.dp))

                                    UploadBox(
                                        title = "Upload Driving Licence",
                                        selected = doc != null
                                    ) { docLauncher.launch("*/*") }

                                    Spacer(modifier = Modifier.height(18.dp))

                                    Text("Your Photo", fontWeight = FontWeight.Bold)

                                    Spacer(modifier = Modifier.height(12.dp))

                                    UploadBox(
                                        title = "Upload Your Photo",
                                        selected = photo != null
                                    ) { photoLauncher.launch("image/*") }
                                }
                            }

                            Spacer(modifier = Modifier.height(22.dp))

                            ElevatedButton(
                                enabled = !isLoading,
                                onClick = {
                                    vm.submitKyc(
                                        KycModel(name = name, phone = phone, doc = doc, photo = photo)
                                    ) { success, _ ->
                                        if (success) {
                                            name = ""; phone = ""; doc = null; photo = null
                                            vm.loadMyKycRecord()
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(58.dp),
                                shape = RoundedCornerShape(40.dp),
                                colors = ButtonDefaults.elevatedButtonColors(
                                    containerColor = Color(0xFF23B14D),
                                    contentColor = Color.White
                                ),
                                elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 8.dp)
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text(text = "Submit KYC", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun KycStatusView(
    record: com.example.drivesafe.model.KycFirebaseModel,
    paddingValues: androidx.compose.foundation.layout.PaddingValues
) {
    val statusColor: Color
    val statusIcon: androidx.compose.ui.graphics.vector.ImageVector
    val statusLabel: String
    val statusMessage: String

    when (record.status) {
        "approved" -> {
            statusColor = Color(0xFF23B14D)
            statusIcon = Icons.Default.CheckCircle
            statusLabel = "Approved"
            statusMessage = "Your KYC has been verified successfully. You can now book vehicles."
        }
        "rejected" -> {
            statusColor = Color(0xFFE53935)
            statusIcon = Icons.Default.Warning
            statusLabel = "Rejected"
            statusMessage = "Your KYC was rejected. Please contact support for more details."
        }
        else -> {
            statusColor = Color(0xFFFFA000)
            statusIcon = Icons.Default.Warning
            statusLabel = "Pending Review"
            statusMessage = "Your KYC is under review. We will notify you once it's verified."
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(statusColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = statusIcon,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(44.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "KYC Status",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = statusLabel,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = statusMessage,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Name: ${record.name}", fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Phone: ${record.phone}", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun KycField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: @Composable () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        leadingIcon = icon,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(14.dp)
    )
}

@Composable
fun UploadBox(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color(0xFFF8FFF9)
        ),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = SolidColor(Color(0xFF6CCF7D))
        )
    ) {

        Icon(
            painter = painterResource(id = R.drawable.outline_upload_24),
            contentDescription = null
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {

            Text(
                text = title,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = if (selected)
                    "Selected"
                else
                    "Not selected",
                fontSize = 12.sp,
                color = if (selected)
                    Color(0xFF23B14D)
                else
                    Color.Red
            )
        }
    }
}

@Preview
@Composable
fun PreviewKyc() {
    KycScreen()
}