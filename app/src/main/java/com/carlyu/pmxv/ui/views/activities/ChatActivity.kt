package com.carlyu.pmxv.ui.views.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.carlyu.pmxv.models.data.ThemeStyleType
import com.carlyu.pmxv.ui.theme.PmxvTheme
import com.carlyu.pmxv.ui.views.screens.ChatScreen
import com.carlyu.pmxv.ui.views.screens.Message
import com.carlyu.pmxv.viewmodels.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class ChatActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val isDarkTheme = when (settingsViewModel.uiMode.value) {
                ThemeStyleType.LIGHT -> false
                ThemeStyleType.DARK -> true
                else -> isSystemInDarkTheme()
            }
            PmxvTheme(
                darkTheme = isDarkTheme,
                dynamicColor = settingsViewModel.useDynamicColor.value
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.primary,

                    ) {
                    val messages = remember {
                        mutableStateListOf(
                            Message(id = "1", text = "Hello!", isSentByUser = false),
                            Message(id = "2", text = "Hi, how are you?", isSentByUser = true),
                            Message(id = "3", text = "I'm good, thanks!", isSentByUser = false)
                        )
                    }
                    var newMessage by remember { mutableStateOf("") }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = newMessage,
                            onValueChange = { newMessage = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Type a message") }
                        )
                        Button(
                            onClick = {
                                if (newMessage.isNotBlank()) {
                                    messages.add(
                                        Message(
                                            id = UUID.randomUUID().toString(),
                                            text = newMessage,
                                            isSentByUser = true
                                        )
                                    )
                                    newMessage = ""
                                }
                            },
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text("Send")
                        }
                    }
                    Column(modifier = Modifier.fillMaxSize()) {
                        ChatScreen(messages = messages)
                        Spacer(modifier = Modifier.height(8.dp))
                        // Add message input and send button here
                    }

                }
            }
        }


    }
}