package com.example.drivesafe.repo

import android.util.Log
import com.example.drivesafe.model.ChatListModel
import com.example.drivesafe.model.MessageModel
import com.google.firebase.database.*

class ChatRepoImpl : ChatRepo {

    private val tag = "ChatRepoImpl"

    private val database = FirebaseDatabase.getInstance().reference

    override fun sendMessage(
        chatId: String,
        userId: String,
        message: MessageModel,
        chatList: ChatListModel,
        callback: (Boolean, String) -> Unit
    ) {
        val messageId = database.child("chats")
            .child(chatId)
            .push()
            .key ?: ""

        val finalMessage = message.copy(messageId = messageId)

        database.child("chats")
            .child(chatId)
            .child(messageId)
            .setValue(finalMessage)
            .addOnSuccessListener {

                database.child("chatList")
                    .child(userId)
                    .setValue(chatList)
                    .addOnSuccessListener {
                        callback(true, "Message sent")
                    }
                    .addOnFailureListener {
                        Log.e(tag, "chatList update failed for $userId: ${it.message}")
                        callback(false, it.message ?: "Chat list update failed")
                    }
            }
            .addOnFailureListener {
                Log.e(tag, "sendMessage($chatId) failed: ${it.message}")
                callback(false, it.message ?: "Message failed")
            }
    }

    override fun getMessages(
        chatId: String,
        callback: (List<MessageModel>) -> Unit
    ) {
        database.child("chats")
            .child(chatId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<MessageModel>()

                    for (data in snapshot.children) {
                        try {
                            val message = data.getValue(MessageModel::class.java)
                            if (message != null) {
                                list.add(message)
                            }
                        } catch (e: Exception) {
                            Log.e(tag, "Skipping malformed message ${data.key}: ${e.message}")
                        }
                    }

                    callback(list.sortedBy { it.timestamp })
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(tag, "getMessages($chatId) cancelled: ${error.message}")
                    callback(emptyList())
                }
            })
    }

    override fun getChatList(callback: (List<ChatListModel>) -> Unit) {
        database.child("chatList")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<ChatListModel>()

                    for (data in snapshot.children) {
                        try {
                            val chat = data.getValue(ChatListModel::class.java)
                            if (chat != null) {
                                list.add(chat)
                            }
                        } catch (e: Exception) {
                            Log.e(tag, "Skipping malformed chat ${data.key}: ${e.message}")
                        }
                    }

                    callback(list.sortedByDescending { it.lastTime })
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(tag, "getChatList cancelled: ${error.message}")
                    callback(emptyList())
                }
            })
    }

    override fun getUserChatPreview(
        userId: String,
        callback: (ChatListModel?) -> Unit
    ) {
        database.child("chatList")
            .child(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        callback(snapshot.getValue(ChatListModel::class.java))
                    } catch (e: Exception) {
                        Log.e(tag, "Malformed chat preview for $userId: ${e.message}")
                        callback(null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(tag, "getUserChatPreview($userId) cancelled: ${error.message}")
                    callback(null)
                }
            })
    }

    override fun markAdminChatAsRead(userId: String) {

        val chatId = "chat_$userId"

        database.child("chatList")
            .child(userId)
            .child("unreadForAdmin")
            .setValue(0)
            .addOnFailureListener {
                Log.e(tag, "markAdminChatAsRead($userId) failed: ${it.message}")
            }

        database.child("chats")
            .child(chatId)
            .get()
            .addOnSuccessListener { snapshot ->

                for (data in snapshot.children) {
                    val senderRole = data.child("senderRole").value.toString()

                    if (senderRole == "user") {
                        data.ref.child("isSeen").setValue(true)
                    }
                }
            }
            .addOnFailureListener {
                Log.e(tag, "markAdminChatAsRead($userId) read fetch failed: ${it.message}")
            }
    }

    override fun markUserChatAsRead(userId: String) {

        val chatId = "chat_$userId"

        database.child("chatList")
            .child(userId)
            .child("unreadForUser")
            .setValue(0)
            .addOnFailureListener {
                Log.e(tag, "markUserChatAsRead($userId) failed: ${it.message}")
            }

        database.child("chats")
            .child(chatId)
            .get()
            .addOnSuccessListener { snapshot ->

                for (data in snapshot.children) {
                    val senderRole = data.child("senderRole").value.toString()

                    if (senderRole == "admin") {
                        data.ref.child("isSeen").setValue(true)
                    }
                }
            }
            .addOnFailureListener {
                Log.e(tag, "markUserChatAsRead($userId) read fetch failed: ${it.message}")
            }
    }
}