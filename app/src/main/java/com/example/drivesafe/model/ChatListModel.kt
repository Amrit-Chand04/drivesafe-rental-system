package com.example.drivesafe.model

data class ChatListModel(

    var userId: String = "",

    var name: String = "",

    var lastMessage: String = "",

    var lastTime: Long = System.currentTimeMillis(),

    var unreadForAdmin: Int = 0,

    var unreadForUser: Int = 0,

    var profileImage: String = "",

    var online: Boolean = false
)