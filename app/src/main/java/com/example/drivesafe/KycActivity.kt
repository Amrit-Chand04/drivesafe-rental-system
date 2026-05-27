package com.example.drivesafe
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.drivesafe.ui.theme.DriveSafeTheme

class KycActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KycScreen()

        }
    }
}


@Composable
fun KycScreen() {

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var extraContact by remember { mutableStateOf("") }

    var doc by remember { mutableStateOf<Uri?>(null) }
    var photo by remember { mutableStateOf<Uri?>(null) }

    val docLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> doc = uri }

    val photoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> photo = uri }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEAF8EE))
            .padding(18.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(onClick = { }) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = Color.Black
                )
            }

            Text(
                text = "KYC Verification",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF23B14D)
            )
        }

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
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {

            Column(
                modifier = Modifier.padding(18.dp)
            ) {

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

                Spacer(modifier = Modifier.height(12.dp))

                KycField(
                    value = extraContact,
                    onValueChange = { extraContact = it },
                    label = "Additional Contact (Optional)",
                    icon = { Icon(Icons.Default.MailOutline, null) }
                )

                Spacer(modifier = Modifier.height(22.dp))

                Text(
                    "Driving Licence",
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                UploadBox(
                    title = "Upload Driving Licence",
                    selected = doc != null
                ) {
                    docLauncher.launch("*/*")
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    "Profile Photo",
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                UploadBox(
                    title = "Upload Profile Photo",
                    selected = photo != null
                ) {
                    photoLauncher.launch("image/*")
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        ElevatedButton(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(40.dp),
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = Color(0xFF23B14D),
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.elevatedButtonElevation(
                defaultElevation = 8.dp
            )
        ) {
            Text(
                text = "Submit KYC",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
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
                    "✔ Selected"
                else
                    "❌ Not selected",
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