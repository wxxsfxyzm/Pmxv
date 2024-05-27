package com.carlyu.chat.ui.views.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import com.carlyu.chat.ui.theme.ChatdemoTheme
import com.carlyu.chat.ui.views.screens.RegisterScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RegisterActivity : ComponentActivity() {
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            ChatdemoTheme(
                dynamicColor = true,
                darkTheme = isSystemInDarkTheme()
            ) {
                RegisterScreen(
                    onRegisterSuccess = {
                        // Handle register
                    },
                    onCancel = {
                        // Handle login
                    }
                )
            }
        }
    }

    private fun onRegister() {
        // 保存注册状态
        with(sharedPreferences.edit()) {
            putBoolean("is_logged_in", true)
            apply()
        }
        // 启动 LoginActivity
        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}