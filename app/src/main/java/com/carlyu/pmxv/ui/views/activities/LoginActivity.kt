package com.carlyu.pmxv.ui.views.activities


import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import com.carlyu.pmxv.local.datastore.PreferencesKeys
import com.carlyu.pmxv.ui.theme.PmxvTheme
import com.carlyu.pmxv.ui.views.screens.LoginScreen
import com.carlyu.pmxv.ui.views.viewmodels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {
    @Inject
    lateinit var dataStore: DataStore<Preferences> // 替换为 DataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PmxvTheme(isSystemInDarkTheme(), true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val loginViewModel: LoginViewModel = hiltViewModel()
                    LoginScreen(
                        loginViewModel = loginViewModel,
                        onLoginSuccess = {
                            // 使用协程作用域处理异步操作
                            lifecycleScope.launch {
                                onLogin()
                            }
                        }
                    )
                }
            }
        }
    }

    private suspend fun onLogin() { // 改为 suspend 函数
        // 使用 DataStore 保存登录状态
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_LOGGED_IN] = true
        }

        // 启动 MainActivity
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}