package com.carlyu.chat.models

data class ChatListSingleData(
    val content: String,
    val sender: String,
    val receiver: String,
    val timestamp: Long
)
