package com.example.drivesafe.viewmodel

import androidx.lifecycle.ViewModel
import com.example.drivesafe.model.ChatListModel
import com.example.drivesafe.model.MessageModel
import com.example.drivesafe.repo.ChatRepo

class ChatViewModel(
    private val repo: ChatRepo
) : ViewModel() {

    fun sendMessage(
        chatId: String,
        userId: String,
        message: MessageModel,
        chatList: ChatListModel,
        callback: (Boolean, String) -> Unit
    ) {
        repo.sendMessage(chatId, userId, message, chatList, callback)
    }

    fun getMessages(
        chatId: String,
        callback: (List<MessageModel>) -> Unit
    ) {
        repo.getMessages(chatId, callback)
    }

    fun getChatList(callback: (List<ChatListModel>) -> Unit) {
        repo.getChatList(callback)
    }

    fun getUserChatPreview(
        userId: String,
        callback: (ChatListModel?) -> Unit
    ) {
        repo.getUserChatPreview(userId, callback)
    }

    fun markAdminChatAsRead(userId: String) {
        repo.markAdminChatAsRead(userId)
    }

    fun markUserChatAsRead(userId: String) {
        repo.markUserChatAsRead(userId)
    }
}