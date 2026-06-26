package com.example.drivesafe.model

data class MessageModel(
    var messageId: String = "",
    var senderId: String = "",
    var senderName: String = "",
    var senderRole: String = "",
    var message: String = "",
    var timestamp: Long = System.currentTimeMillis(),
)