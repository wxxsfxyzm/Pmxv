package com.carlyu.pmxv.ui.views.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carlyu.pmxv.models.data.view.ThemeStyleType
import com.carlyu.pmxv.ui.components.scaffold.MainViewScaffoldLayout
import com.carlyu.pmxv.ui.components.scaffold.SettingsWizardScaffoldLayout
import com.carlyu.pmxv.ui.theme.PmxvTheme
import com.carlyu.pmxv.ui.views.uistate.SettingsUiState
import com.carlyu.pmxv.ui.views.viewmodels.SettingsViewModel
import com.carlyu.pmxv.ui.views.viewmodels.SettingsWizardViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * MainActivity 是应用的主活动，负责显示应用的主要界面。
 * 它使用 Jetpack Compose 来构建 UI，并通过 Hilt 进行依赖注入。
 * 在创建时，它会加载系统设置和向导状态，并根据这些状态决定显示哪个界面。
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        var systemSettingsUiReady by mutableStateOf(false)
        var wizardStateReady by mutableStateOf(false) // 用于跟踪向导状态是否已从DataStore加载

        setContent {
            // 主应用设置 ViewModel
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val settingsUiState by settingsViewModel.uiState.collectAsStateWithLifecycle()

            // 设置向导 ViewModel
            val wizardViewModel: SettingsWizardViewModel = hiltViewModel()
            val isWizardCompleted by wizardViewModel.isWizardCompleted.collectAsStateWithLifecycle()


            // 当 isWizardCompleted 的初始值从 DataStore 加载后 (即不再是initialValue，或有了第一个值)
            // 我们认为向导状态已就绪。
            // isWizardCompleted.value Being false (initialValue of stateIn) or the actual value from datastore.
            // The key is that the flow has emitted at least its initial value.
            LaunchedEffect(Unit) { //  只需要执行一次，或者key为wizardViewModel确保其已初始化
                // 等待 wizardViewModel.isWizardCompleted 至少发出其初始值
                // 由于 `stateIn` 的 `initialValue`，它会立即有一个值。所以直接设为true是安全的
                wizardStateReady = true
            }

            // 监听设置状态变化
            val (isDarkTheme, dynamicColor) = when (settingsUiState) {
                is SettingsUiState.Success -> {
                    systemSettingsUiReady = true
                    val successState = settingsUiState as SettingsUiState.Success
                    Pair(
                        when (successState.uiMode) {
                            ThemeStyleType.LIGHT -> false
                            ThemeStyleType.DARK -> true
                            ThemeStyleType.FOLLOW_SYSTEM -> isSystemInDarkTheme()
                        },
                        successState.useDynamicColor
                    )
                }

                is SettingsUiState.Error -> {
                    systemSettingsUiReady = true // 出错也认为加载结束，可以移除 Splash Screen
                    Pair(isSystemInDarkTheme(), false) // 或者提供特定的错误状态主题
                }

                is SettingsUiState.Loading -> {
                    systemSettingsUiReady = false // 仍在加载，Splash Screen 继续显示
                    // 在 Loading 状态下，PmxvTheme 会使用下面的默认值，
                    // 但因为 Splash Screen 还在显示，用户不会看到这个中间状态。
                    Pair(isSystemInDarkTheme(), false) // 这里的返回值其实在splash显示期间不重要
                }
            }

            // SplashScreen 条件：系统主题设置加载完成 且 向导状态检查完成
            splashScreen.setKeepOnScreenCondition { !systemSettingsUiReady || !wizardStateReady }

            PmxvTheme(
                darkTheme = isDarkTheme,
                dynamicColor = dynamicColor
            ) {
                // 只有当 SplashScreen 消失后才决定显示哪个内容
                if (systemSettingsUiReady && wizardStateReady) {
                    if (isWizardCompleted) {
                        MainViewScaffoldLayout(viewModel = settingsViewModel)
                    } else {
                        SettingsWizardScaffoldLayout(viewModel = wizardViewModel)
                    }
                } else {
                    // SplashScreen 期间，这里的内容是不可见的，可以放一个加载指示器以防万一
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                // 处理主应用退出逻辑 (SettingsViewModel)
                settingsViewModel.finishActivity.observe(this) { shouldFinish ->
                    if (shouldFinish && isWizardCompleted) { // 仅当向导完成且在主应用界面时
                        finish()
                    }
                }
            }
        }
    }
}
