package com.example.drivesafe.repo

import com.example.drivesafe.model.ChatListModel
import com.example.drivesafe.model.MessageModel

interface ChatRepo {

    fun sendMessage(
        chatId: String,
        userId: String,
        message: MessageModel,
        chatList: ChatListModel,
        callback: (Boolean, String) -> Unit
    )

    fun getMessages(
        chatId: String,
        callback: (List<MessageModel>) -> Unit
    )

    fun getChatList(
        callback: (List<ChatListModel>) -> Unit
    )

    fun getUserChatPreview(
        userId: String,
        callback: (ChatListModel?) -> Unit
    )

    fun markAdminChatAsRead(userId: String)

    fun markUserChatAsRead(userId: String)
}

