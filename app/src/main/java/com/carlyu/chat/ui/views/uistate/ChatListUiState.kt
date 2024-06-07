package com.carlyu.chat.ui.views.uistate

import com.carlyu.chat.models.ChatListSingleInstance

data class ChatListUiState(
    val isLoading: Boolean = false,
    val messages: List<ChatListSingleInstance> = emptyList()
)
