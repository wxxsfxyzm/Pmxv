package com.carlyu.chat.ui.views.activities

import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.carlyu.chat.models.data.ThemeStyleType
import com.carlyu.chat.ui.components.ScaffoldLayout
import com.carlyu.chat.ui.theme.ChatdemoTheme
import com.carlyu.chat.utils.SharedPreferenceUtils
import com.carlyu.chat.viewmodels.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        // 调用 PreferenceUtils.clearUnusedSharedPreferences 方法
        val unUsedKeys = listOf("")
        SharedPreferenceUtils.clearUnusedSharedPreferences(this, unUsedKeys)

        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            // Observe the finishActivity LiveData
            settingsViewModel.finishActivity.observe(this) { shouldFinish ->
                if (shouldFinish) {
                    finish()
                }
            }

            val isDarkTheme = when (settingsViewModel.uiMode.value) {
                ThemeStyleType.LIGHT -> false
                ThemeStyleType.DARK -> true
                else ->
                    isSystemInDarkTheme()
            }
            ChatdemoTheme(
                darkTheme = isDarkTheme,
                dynamicColor = settingsViewModel.useDynamicColor.value
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,

                    ) {
                    ScaffoldLayout()
                }
            }
        }
    }

    private fun isDarkMode(resources: Resources): Boolean {
        return (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }
}
