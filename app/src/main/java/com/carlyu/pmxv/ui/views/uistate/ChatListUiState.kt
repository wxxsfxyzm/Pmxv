package com.carlyu.pmxv.ui.views.uistate

import com.carlyu.pmxv.models.ChatListSingleInstance

data class ChatListUiState(
    val isLoading: Boolean = false,
    val messages: List<ChatListSingleInstance> = emptyList()
)
