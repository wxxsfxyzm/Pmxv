package com.carlyu.pmxv.ui.views.activities

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
import com.carlyu.pmxv.models.data.ThemeStyleType
import com.carlyu.pmxv.ui.components.ScaffoldLayout
import com.carlyu.pmxv.ui.theme.PmxvTheme
import com.carlyu.pmxv.utils.SharedPreferenceUtils
import com.carlyu.pmxv.viewmodels.SettingsViewModel
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
            PmxvTheme(
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

}
