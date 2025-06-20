package com.carlyu.pmxv.ui.views.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.carlyu.pmxv.models.data.view.ThemeStyleType
import com.carlyu.pmxv.ui.theme.PmxvTheme
import com.carlyu.pmxv.ui.views.screens.mainViewScreen.AboutPageScreen
import com.carlyu.pmxv.ui.views.uistate.SettingsState
import com.carlyu.pmxv.ui.views.viewmodels.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * AboutPageActivity 是应用的关于页面活动，负责显示应用的关于信息。
 * 它使用 Jetpack Compose 来构建 UI，并通过 Hilt 进行依赖注入。
 */
@AndroidEntryPoint
class AboutPageActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val uiState by settingsViewModel.uiState.collectAsState()
            // 监听设置状态变化
            val (isDarkTheme, dynamicColor) = when (uiState) {
                is SettingsState.Success -> {
                    val successState = uiState as SettingsState.Success
                    Pair(
                        when (successState.uiMode) {
                            ThemeStyleType.LIGHT -> false
                            ThemeStyleType.DARK -> true
                            ThemeStyleType.FOLLOW_SYSTEM -> isSystemInDarkTheme()
                        },
                        successState.useDynamicColor
                    )
                }

                SettingsState.Loading,
                is SettingsState.Error -> {
                    // 加载中和错误状态使用系统默认
                    Pair(isSystemInDarkTheme(), false)
                }
            }
            PmxvTheme(
                darkTheme = isDarkTheme,
                dynamicColor = dynamicColor
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AboutPageScreen()
                }
            }
        }
    }
}