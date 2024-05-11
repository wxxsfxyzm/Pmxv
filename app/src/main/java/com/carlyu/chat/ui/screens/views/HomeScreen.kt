package com.carlyu.chat.ui.screens.views

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.carlyu.chat.models.ChatListSingleData
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.random.Random

@Composable
@Preview
fun HomeScreen() {
    val context = LocalContext.current
    BackHandler {
        (context as? Activity)?.moveTaskToBack(true)
    }
    val messages = mutableListOf<ChatListSingleData>().apply {
        repeat(15) {
            add(
                ChatListSingleData(
                    content = "Message ${Random.nextInt(1000, 9999)}",
                    sender = "Sender ${Random.nextInt(1000, 9999)}",
                    receiver = "Receiver ${Random.nextInt(1000, 9999)}",
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }
    // 显示聊天消息的列表
    ChatMessageList(messages = messages)
}

@Composable
fun ChatMessageList(messages: List<ChatListSingleData>) {
    LazyColumn {
        items(messages) { message ->
            ChatMessageItem(message = message)
        }
    }
}

@Composable
fun ChatMessageItem(message: ChatListSingleData) {

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
        }
    )
    HorizontalDivider()
}

