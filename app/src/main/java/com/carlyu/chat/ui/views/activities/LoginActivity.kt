package com.carlyu.chat.ui.views.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import com.carlyu.chat.ui.theme.ChatdemoTheme
import com.carlyu.chat.ui.views.screens.LoginScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChatdemoTheme(isSystemInDarkTheme(), true) {
                LoginScreen(
                    onLoginSuccess = {
                        // 保存登录状态
                        with(sharedPreferences.edit()) {
                            putBoolean("is_logged_in", true)
                            apply()
                        }
                        // 启动 MainActivity
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }
}