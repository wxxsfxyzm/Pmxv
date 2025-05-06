package com.carlyu.pmxv.ui.views.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.lifecycleScope
import com.carlyu.pmxv.local.datastore.PreferencesKeys
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : ComponentActivity() {

    @Inject
    lateinit var dataStore: DataStore<Preferences> // 直接注入

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        installSplashScreen()
        // 启动协程处理异步逻辑
        lifecycleScope.launch {
            checkLoginStatus()
        }
    }

    private suspend fun checkLoginStatus() {
        try {
            val preferences = dataStore.data.first()
            val isLoggedIn = preferences[PreferencesKeys.IS_LOGGED_IN] == true
            Log.d("SplashActivity", "isLoggedIn: $isLoggedIn")

            val intent = if (true) {
                Intent(this@SplashActivity, MainActivity::class.java)
            } else {
                Intent(this@SplashActivity, LoginActivity::class.java)
            }

            startActivity(intent)
            finish()
        } catch (e: Exception) {
            Log.e("SplashActivity", "Error checking login status", e)
            // 处理错误情况，例如默认跳转至登录页
            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            finish()
        }
    }
}