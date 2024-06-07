package com.carlyu.chat.ui.views.screens

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.carlyu.chat.models.ChatListSingleInstance
import com.carlyu.chat.ui.views.activities.ChatActivity
import com.carlyu.chat.ui.views.uistate.ChatListUiState
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.random.Random

@Composable
@Preview
fun HomeScreen() {
    val uiState = remember { mutableStateOf(ChatListUiState(isLoading = true)) }

    // 显示聊天消息的列表
    ChatMessageList(uiState.value)

    LaunchedEffect(key1 = Unit) {
        val messages = loadMessagesFromDatabase()
        uiState.value = ChatListUiState(messages = messages, isLoading = false)
    }
}

@Composable
fun ChatMessageList(uiState: ChatListUiState) {
    val context = LocalContext.current
    if (uiState.isLoading)
        CircularProgressIndicator()
    else {
        LazyColumn {
            items(uiState.messages) { message ->
                ChatMessageItem(message = message, onClick = {
                    context.startActivity(Intent(context, ChatActivity::class.java))
                })
            }
        }
    }
}

@Composable
fun ChatMessageItem(
    message: ChatListSingleInstance,
    onClick: () -> Unit = { }
) {

    // 在这里显示你的聊天消息
    // 例如，你可以使用Text组件来显示消息的内容
    ListItem(
        headlineContent = { Text(text = message.sender) },
        supportingContent = { Text(text = message.content) },
        trailingContent = {
            Text(
                text = SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    Locale.getDefault()
                ).format(message.timestamp)
            )
        },
        leadingContent = {
            Icon(
                Icons.Filled.Favorite,
                contentDescription = "Localized description",
            )
        },
        modifier = Modifier.clickable { onClick() },
    )
    HorizontalDivider()
}

fun loadMessagesFromDatabase(): List<ChatListSingleInstance> {
    return mutableListOf<ChatListSingleInstance>().apply {
        repeat(15) {
            add(
                ChatListSingleInstance(
                    content = "Message ${Random.nextInt(1000, 9999)}",
                    sender = "Sender ${Random.nextInt(1000, 9999)}",
                    receiver = "Receiver ${Random.nextInt(1000, 9999)}",
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }
}