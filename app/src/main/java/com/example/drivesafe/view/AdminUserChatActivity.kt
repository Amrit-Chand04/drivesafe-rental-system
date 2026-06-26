package com.example.drivesafe.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.drivesafe.model.ChatListModel
import com.example.drivesafe.model.MessageModel
import com.example.drivesafe.repo.ChatRepoImpl
import com.example.drivesafe.ui.theme.DriveSafeTheme
import com.example.drivesafe.viewmodel.ChatViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdminUserChatActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val userId = intent.getStringExtra("userId") ?: ""
        val userName = intent.getStringExtra("userName") ?: "User"

        setContent {
            DriveSafeTheme {
                val chatViewModel = remember {
                    ChatViewModel(ChatRepoImpl())
                }

                AdminUserChatBody(
                    userId = userId,
                    userName = userName,
                    chatViewModel = chatViewModel
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUserChatBody(
    userId: String,
    userName: String,
    chatViewModel: ChatViewModel
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val chatId = "chat_$userId"

    var messageText by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(listOf<MessageModel>()) }

    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        chatViewModel.markAdminChatAsRead(userId)

        chatViewModel.getMessages(chatId) { list ->
            messages = list
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color(0xFFE1F7EA), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userName.firstOrNull()?.uppercase() ?: "U",
                                color = Color(0xFF00A859),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = userName,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = "Online",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFFBF4))
                .padding(padding)
        ) {

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
            ) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Today",
                            modifier = Modifier
                                .background(
                                    Color(0xFFEDEDED),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 14.dp, vertical = 6.dp),
                            fontSize = 12.sp,
                            color = Color.DarkGray
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }

                items(messages) { msg ->
                    AdminMessageBubble(
                        message = msg.message,
                        senderName = msg.senderName,
                        time = formatAdminUserChatTime(msg.timestamp),
                        isMine = msg.senderRole == "admin"
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type reply") },
                    shape = RoundedCornerShape(28.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        if (messageText.isNotBlank()) {

                            val currentTime = System.currentTimeMillis()

                            val messageData = MessageModel(
                                senderId = "admin",
                                senderName = "Admin",
                                senderRole = "admin",
                                message = messageText,
                                timestamp = currentTime
                            )

                            val chatListData = ChatListModel(
                                userId = userId,
                                name = userName,
                                lastMessage = messageText,
                                lastTime = currentTime,
                                unreadForAdmin = 0,
                                unreadForUser = 1
                            )

                            chatViewModel.sendMessage(
                                chatId = chatId,
                                userId = userId,
                                message = messageData,
                                chatList = chatListData
                            ) { success, msg ->
                                if (success) {
                                    messageText = ""
                                } else {
                                    Toast.makeText(
                                        context,
                                        msg,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    },
                    shape = CircleShape,
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.size(54.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00A859)
                    )
                ) {
                    Text("➤", fontSize = 22.sp)
                }
            }
        }
    }
}

@Composable
fun AdminMessageBubble(
    message: String,
    senderName: String,
    time: String,
    isMine: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
    ) {

        if (!isMine) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(Color(0xFFE1F7EA), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = senderName.firstOrNull()?.uppercase() ?: "U",
                    color = Color(0xFF00A859),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(8.dp))
        }

        Card(
            modifier = Modifier.widthIn(max = 260.dp),
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (isMine) 18.dp else 4.dp,
                bottomEnd = if (isMine) 4.dp else 18.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (isMine) Color(0xFFE0FFD8) else Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = if (isMine) "Admin" else senderName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00A859)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = message,
                    fontSize = 16.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = time,
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

fun formatAdminUserChatTime(timestamp: Long): String {
    return try {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        sdf.format(Date(timestamp))
    } catch (e: Exception) {
        ""
    }
}