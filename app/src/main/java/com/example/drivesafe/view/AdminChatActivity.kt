package com.example.drivesafe.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.drivesafe.R
import com.example.drivesafe.model.ChatListModel
import com.example.drivesafe.repo.ChatRepoImpl
import com.example.drivesafe.ui.theme.DriveSafeTheme
import com.example.drivesafe.viewmodel.ChatViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdminChatActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            DriveSafeTheme {
                val chatViewModel = remember {
                    ChatViewModel(ChatRepoImpl())
                }

                AdminChatBody(chatViewModel = chatViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminChatBody(chatViewModel: ChatViewModel) {

    var selectedIndex by remember { mutableStateOf(1) }
    val context = LocalContext.current

    var chats by remember {
        mutableStateOf(listOf<ChatListModel>())
    }

    LaunchedEffect(Unit) {
        chatViewModel.getChatList { list ->
            chats = list
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Chats",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(
                                id = R.drawable.outline_arrow_back_ios_24
                            ),
                            contentDescription = "Back",
                            tint = Color.Black
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
                            painter = painterResource(
                                id = R.drawable.baseline_supervised_user_circle_24
                            ),
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
                Spacer(modifier = Modifier.height(20.dp))

                HorizontalDivider(
                    thickness = 1.dp,
                    color = Color.LightGray
                )

                Spacer(modifier = Modifier.height(25.dp))
            }

            if (chats.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No messages yet",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            items(chats) { chat ->
                AdminChatCard(
                    name = chat.name,
                    message = chat.lastMessage,
                    time = formatAdminChatTime(chat.lastTime),
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

@Composable
fun AdminChatCard(
    name: String,
    message: String,
    time: String,
    unread: Int,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFEAF7F0), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name.firstOrNull()?.uppercase() ?: "U",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00A859)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = message,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {

                Text(
                    text = time,
                    fontSize = 11.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (unread > 0) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .background(Color(0xFF00A859), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = unread.toString(),
                            fontSize = 11.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

fun formatAdminChatTime(timestamp: Long): String {
    return try {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        sdf.format(Date(timestamp))
    } catch (e: Exception) {
        ""
    }
}

