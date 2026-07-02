package com.example.drivesafe.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drivesafe.model.ChatListModel
import com.example.drivesafe.ui.theme.DriveSafeTheme
import com.example.drivesafe.viewmodel.ChatViewModel
import com.google.firebase.auth.FirebaseAuth

class UserAdminChatActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            DriveSafeTheme {
                val chatViewModel: ChatViewModel = viewModel()
                var selectedIndex by remember { mutableStateOf(1) }
                val context = LocalContext.current

                UserDashboardScaffold(
                    selectedIndex = selectedIndex,
                    onItemSelected = { index ->
                        if (index == 1) {
                            selectedIndex = 1
                        } else {
                            (context as Activity).finish()
                        }
                    }
                ) { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        UserAdminChatBody(chatViewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun UserAdminChatBody(chatViewModel: ChatViewModel) {

    val context = LocalContext.current
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid ?: "guest_user"

    val preview by chatViewModel.userChatPreview.collectAsState()
    val isLoading by chatViewModel.isUserChatPreviewLoading.collectAsState()
    val adminChat = preview ?: ChatListModel(
        userId = userId,
        name = "Admin",
        lastMessage = "Chat with admin",
        lastTime = System.currentTimeMillis(),
        unreadForUser = 0
    )

    LaunchedEffect(Unit) {
        chatViewModel.loadUserChatPreview(userId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
    ) {

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Inbox",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color(0xFF24C16B)
                )
            }
        } else {
            ChatListCard(
                name = "Admin",
                message = adminChat.lastMessage,
                time = formatChatTime(adminChat.lastTime),
                unread = adminChat.unreadForUser,
                onClick = {
                    chatViewModel.markUserChatAsRead(userId)

                    val intent = Intent(context, UserChatActivity::class.java)
                    context.startActivity(intent)
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserChatPreview() {
    DriveSafeTheme {
        ChatListCard(
            name = "Admin",
            message = "Chat with admin",
            time = "Now",
            unread = 1,
            onClick = {}
        )
    }
}
