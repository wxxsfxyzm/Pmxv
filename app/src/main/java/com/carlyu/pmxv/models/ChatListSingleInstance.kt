package com.carlyu.pmxv.models

data class ChatListSingleInstance(
    val content: String,
    val sender: String,
    val receiver: String,
    val timestamp: Long
)
