package com.example.drivesafe.view

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drivesafe.R
import com.example.drivesafe.model.ChatListModel
import com.example.drivesafe.model.MessageModel
import com.example.drivesafe.ui.theme.DriveSafeTheme
import com.example.drivesafe.viewmodel.ChatViewModel
import com.example.drivesafe.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth

class UserChatActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            DriveSafeTheme {
                val chatViewModel: ChatViewModel = viewModel()

                UserChatBody(chatViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserChatBody(chatViewModel: ChatViewModel) {

    val context = LocalContext.current
    val currentUser = FirebaseAuth.getInstance().currentUser

    val userId = currentUser?.uid ?: "guest_user"
    val chatId = "chat_$userId"

    val userViewModel: UserViewModel = viewModel()
    val userProfile by userViewModel.user.collectAsState()
    val userName = userProfile?.fullName?.takeIf { it.isNotBlank() }
        ?: currentUser?.email
        ?: "User"

    var messageText by remember { mutableStateOf("") }
    val messages by chatViewModel.messages.collectAsState()

    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        userViewModel.loadCurrentUser()
        chatViewModel.markUserChatAsRead(userId)
        chatViewModel.loadMessages(chatId)
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { (context as Activity).finish() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.outline_arrow_back_ios_24),
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color(0xFFE1F7EA), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "A",
                                color = Color(0xFF00A859),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Admin",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFE8F5E9)
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

            val messagesByDay = remember(messages) {
                messages.groupBy { chatDayKey(it.timestamp) }
            }

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
            ) {
                messagesByDay.forEach { (_, dayMessages) ->
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = formatChatDate(dayMessages.first().timestamp),
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

                    items(dayMessages) { msg ->
                        ChatMessageBubble(
                            message = msg.message,
                            senderName = msg.senderName,
                            time = formatChatTime(msg.timestamp),
                            isMine = msg.senderRole == "user",
                            mineLabel = "You"
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE8F5E9))
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type message") },
                    shape = RoundedCornerShape(28.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        if (messageText.isNotBlank()) {

                            val currentTime = System.currentTimeMillis()

                            val messageData = MessageModel(
                                senderId = userId,
                                senderName = userName,
                                senderRole = "user",
                                message = messageText,
                                timestamp = currentTime
                            )

                            val chatListData = ChatListModel(
                                userId = userId,
                                name = userName,
                                lastMessage = messageText,
                                lastTime = currentTime,
                                unreadForAdmin = 1,
                                unreadForUser = 0
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

