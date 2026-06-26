package com.example.drivesafe.repo

import com.example.drivesafe.model.ChatListModel
import com.example.drivesafe.model.MessageModel
import com.google.firebase.database.*

class ChatRepoImpl : ChatRepo {

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
                        callback(false, it.message ?: "Chat list update failed")
                    }
            }
            .addOnFailureListener {
                callback(false, it.message ?: "Message failed")
            }
    }

    override fun getMessages(
        chatId: String,
        callback: (List<MessageModel>) -> Unit
    ) {
        database.child("chats")
            .child(chatId)
            .orderByChild("timestamp")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<MessageModel>()

                    for (data in snapshot.children) {
                        val message = data.getValue(MessageModel::class.java)
                        if (message != null) {
                            list.add(message)
                        }
                    }

                    callback(list)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(emptyList())
                }
            })
    }

    override fun getChatList(callback: (List<ChatListModel>) -> Unit) {
        database.child("chatList")
            .orderByChild("lastTime")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<ChatListModel>()

                    for (data in snapshot.children) {
                        val chat = data.getValue(ChatListModel::class.java)
                        if (chat != null) {
                            list.add(chat)
                        }
                    }

                    callback(list.reversed())
                }

                override fun onCancelled(error: DatabaseError) {
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
                    callback(snapshot.getValue(ChatListModel::class.java))
                }

                override fun onCancelled(error: DatabaseError) {
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
    }

    override fun markUserChatAsRead(userId: String) {

        val chatId = "chat_$userId"

        database.child("chatList")
            .child(userId)
            .child("unreadForUser")
            .setValue(0)

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
    }
}