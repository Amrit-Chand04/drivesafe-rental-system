package com.example.drivesafe.view

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.drivesafe.model.KycFirebaseModel
import com.example.drivesafe.ui.theme.DriveSafeTheme
import com.example.drivesafe.viewmodel.KycViewModel

class ManageKycVerification : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DriveSafeTheme {
                ManageKycVerificationScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageKycVerificationScreen() {

    val context = LocalContext.current
    val kycViewModel: KycViewModel = viewModel()

    val allKycList by kycViewModel.allKycList.collectAsState()
    val isLoading by kycViewModel.isLoadingAll.collectAsState()

    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Pending", "Approved", "Rejected")

    LaunchedEffect(Unit) {
        kycViewModel.loadAllKycRecords()
    }

    val filteredList = remember(selectedFilter, allKycList) {
        if (selectedFilter == "All") allKycList
        else allKycList.filter { kyc ->
            kyc.status.equals(selectedFilter, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Manage KYC Verification",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { (context as Activity).finish() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
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
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filters.forEach { label ->

                    val isSelected = selectedFilter == label
                    val bgColor by animateColorAsState(
                        targetValue = if (isSelected) Color(0xFF2E7D32) else Color.White,
                        animationSpec = tween(250),
                        label = "chip_bg"
                    )
                    val textColor by animateColorAsState(
                        targetValue = if (isSelected) Color.White else Color.DarkGray,
                        animationSpec = tween(250),
                        label = "chip_text"
                    )

                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = label },
                        label = {
                            Text(
                                text = label,
                                fontSize = 13.sp,
                                color = textColor,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = bgColor,
                            selectedContainerColor = bgColor
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) Color(0xFF2E7D32) else Color(0xFFCCCCCC),
                            selectedBorderColor = Color(0xFF2E7D32)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }

            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF2E7D32))
                    }
                }

                filteredList.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "No KYC records found", color = Color.Gray)
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(filteredList) { kyc ->
                            KycCard(
                                kyc = kyc,
                                onApprove = { kycViewModel.updateKycStatus(kyc.uid, "approved") },
                                onReject = { reason -> kycViewModel.updateKycStatus(kyc.uid, "rejected", reason) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun KycCard(
    kyc: KycFirebaseModel,
    onApprove: () -> Unit,
    onReject: (String) -> Unit
) {
    var fullImageUrl by remember { mutableStateOf<String?>(null) }
    var showRejectDialog by remember { mutableStateOf(false) }
    var rejectReason by remember { mutableStateOf("") }

    fullImageUrl?.let { url ->
        FullImageDialog(url = url, onDismiss = { fullImageUrl = null })
    }

    if (showRejectDialog) {
        AlertDialog(
            onDismissRequest = { showRejectDialog = false },
            title = { Text("Reason for Rejection", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = rejectReason,
                    onValueChange = { rejectReason = it },
                    placeholder = { Text("Enter reason...") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (rejectReason.isNotBlank()) {
                            onReject(rejectReason)
                            showRejectDialog = false
                            rejectReason = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                ) {
                    Text("Reject")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRejectDialog = false; rejectReason = "" }) {
                    Text("Cancel")
                }
            }
        )
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFD4EDDA)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = kyc.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = kyc.phone,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }

                KycStatusBadge(status = kyc.status)
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "KYC Documents",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF555555)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                KycImageTile(
                    label = "Driving Licence",
                    url = kyc.doc,
                    icon = Icons.Default.Badge,
                    modifier = Modifier.weight(1f),
                    onClick = { fullImageUrl = kyc.doc }
                )
                KycImageTile(
                    label = "Profile Photo",
                    url = kyc.photo,
                    icon = Icons.Default.Image,
                    modifier = Modifier.weight(1f),
                    onClick = { fullImageUrl = kyc.photo }
                )
            }

            if (kyc.status.equals("rejected", ignoreCase = true) && kyc.rejectionReason.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFEBEE), shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Reason: ",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFE53935)
                    )
                    Text(
                        text = kyc.rejectionReason,
                        fontSize = 13.sp,
                        color = Color(0xFFB71C1C)
                    )
                }
            }

            if (kyc.status.equals("pending", ignoreCase = true)) {
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { showRejectDialog = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFE53935)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFFE53935)
                        )
                    ) {
                        Text(
                            text = "✕  Reject",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }

                    Button(
                        onClick = onApprove,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2E7D32),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "✓  Approve",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun KycImageTile(
    label: String,
    url: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF2F2F2))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF2E7D32),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1A1A1A)
        )
    }
}

@Composable
fun FullImageDialog(url: String, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            AsyncImage(
                model = url,
                contentDescription = "Full Image",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
            )
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .background(Color(0x88000000), shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun KycStatusBadge(status: String) {
    val (bgColor, textColor, label) = when (status.lowercase()) {
        "approved" -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), "✓ Approved")
        "rejected" -> Triple(Color(0xFFFFEBEE), Color(0xFFE53935), "✕ Rejected")
        else -> Triple(Color(0xFFFFF3E0), Color(0xFFF57C00), "⏱ Pending")
    }

    Box(
        modifier = Modifier
            .background(bgColor, shape = RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewManageKycVerification() {
    DriveSafeTheme {
        ManageKycVerificationScreen()
    }
}