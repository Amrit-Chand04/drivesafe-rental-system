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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drivesafe.ui.theme.DriveSafeTheme
import com.example.drivesafe.viewmodel.ChatViewModel

class AdminChatActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            DriveSafeTheme {
                val chatViewModel: ChatViewModel = viewModel()
                var selectedIndex by remember { mutableStateOf(1) }
                val context = LocalContext.current

                AdminDashboardScaffold(
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
                        AdminChatBody(chatViewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminChatBody(chatViewModel: ChatViewModel) {

    val context = LocalContext.current

    val chats by chatViewModel.chatList.collectAsState()
    val isLoading by chatViewModel.isChatListLoading.collectAsState()

    LaunchedEffect(Unit) {
        chatViewModel.loadChatList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
    ) {

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Inbox",
            fontSize = 28.sp,
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
                CircularProgressIndicator(color = Color(0xFF24C16B))
            }
        } else if (chats.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No conversations yet",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(chats) { chat ->
                    ChatListCard(
                        name = chat.name,
                        message = chat.lastMessage,
                        time = formatChatTime(chat.lastTime),
                        unread = chat.unreadForAdmin,
                        onClick = {
                            chatViewModel.markAdminChatAsRead(chat.userId)

                            val intent = Intent(context, AdminUserChatActivity::class.java)
                            intent.putExtra("userId", chat.userId)
                            intent.putExtra("userName", chat.name)

                            context.startActivity(intent)
                        }
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }
    }
}