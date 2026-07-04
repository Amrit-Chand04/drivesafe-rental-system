package com.example.drivesafe.viewmodel

import androidx.lifecycle.ViewModel
import com.example.drivesafe.model.ChatListModel
import com.example.drivesafe.model.MessageModel
import com.example.drivesafe.repo.ChatRepo
import com.example.drivesafe.repo.ChatRepoImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ChatViewModel(private val repo: ChatRepo = ChatRepoImpl()) : ViewModel() {

    private val _messages = MutableStateFlow<List<MessageModel>>(emptyList())
    val messages: StateFlow<List<MessageModel>> = _messages.asStateFlow()

    private val _chatList = MutableStateFlow<List<ChatListModel>>(emptyList())
    val chatList: StateFlow<List<ChatListModel>> = _chatList.asStateFlow()

    private val _isChatListLoading = MutableStateFlow(false)
    val isChatListLoading: StateFlow<Boolean> = _isChatListLoading.asStateFlow()

    private val _userChatPreview = MutableStateFlow<ChatListModel?>(null)
    val userChatPreview: StateFlow<ChatListModel?> = _userChatPreview.asStateFlow()

    private val _isUserChatPreviewLoading = MutableStateFlow(false)
    val isUserChatPreviewLoading: StateFlow<Boolean> = _isUserChatPreviewLoading.asStateFlow()

    private var messagesChatId: String? = null
    private var chatListLoaded = false
    private var userChatPreviewUserId: String? = null

    fun loadMessages(chatId: String) {
        if (messagesChatId == chatId) return
        messagesChatId = chatId

        repo.getMessages(chatId) { list ->
            _messages.value = list
        }
    }

    fun loadChatList() {
        if (chatListLoaded) return
        chatListLoaded = true
        _isChatListLoading.value = true

        repo.getChatList { list ->
            _chatList.value = list
            _isChatListLoading.value = false
        }
    }

    fun loadUserChatPreview(userId: String) {
        if (userChatPreviewUserId == userId) return
        userChatPreviewUserId = userId
        _isUserChatPreviewLoading.value = true

        repo.getUserChatPreview(userId) { preview ->
            _userChatPreview.value = preview
            _isUserChatPreviewLoading.value = false
        }
    }

    fun sendMessage(
        chatId: String,
        userId: String,
        message: MessageModel,
        chatList: ChatListModel,
        callback: (Boolean, String) -> Unit
    ) {
        repo.sendMessage(chatId, userId, message, chatList, callback)
    }

    fun markAdminChatAsRead(userId: String) {
        repo.markAdminChatAsRead(userId)
    }

    fun markUserChatAsRead(userId: String) {
        repo.markUserChatAsRead(userId)
    }
}
