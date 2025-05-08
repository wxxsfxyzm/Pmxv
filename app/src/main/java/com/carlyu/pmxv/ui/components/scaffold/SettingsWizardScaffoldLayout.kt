package com.carlyu.pmxv.ui.components.scaffold

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carlyu.pmxv.models.entity.SettingsWizardNavigationAction
import com.carlyu.pmxv.ui.theme.PmxvTheme
import com.carlyu.pmxv.ui.views.uistate.SettingsWizardUiState
import com.carlyu.pmxv.ui.views.viewmodels.SettingsWizardViewModel

/**
 * SettingsWizardScaffoldLayout 是一个用于设置向导的布局组件。
 * 它使用 Jetpack Compose 来构建 UI，并通过 ViewModel 来管理状态。
 * 该组件包含顶部应用栏、底部导航栏和不同的步骤内容。
 *
 * @param viewModel 用于管理设置向导状态的 ViewModel
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun SettingsWizardScaffoldLayout(
    viewModel: SettingsWizardViewModel // 在实际Activity中，你会通过 Hilt 或 viewModel() 获取
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when (uiState.currentStep) {
                            0 -> "欢迎"
                            1 -> "登录账户"
                            2 -> "设置完成"
                            else -> "设置向导"
                        }
                    )
                }
            )
        },
        bottomBar = {
            WizardBottomNavigation(
                currentStep = uiState.currentStep,
                totalSteps = 3, // 总共3步 (0, 1, 2)
                onNextClicked = { viewModel.handleNavigation(SettingsWizardNavigationAction.Next) },
                onBackClicked = { viewModel.handleNavigation(SettingsWizardNavigationAction.Back) },
                //onLoginClicked = { viewModel.performLogin() },
                onCompleteClicked = { viewModel.completeWizard() },
                isNextEnabled = viewModel.validateCurrentStep(), // 使用ViewModel中的验证逻辑
                isLoading = uiState.isLoading
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (uiState.errorMessage != null) {
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .padding(bottom = 70.dp), // 避免被底部导航遮挡
                    action = {
                        TextButton(onClick = { viewModel.clearErrorMessage() }) { // 假设有这个方法清除错误
                            Text("知道了")
                        }
                    }
                ) {
                    Text(text = uiState.errorMessage!!)
                }
            }

            AnimatedContent(
                targetState = uiState.currentStep,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> fullWidth },
                            animationSpec = tween(300)
                        ) + fadeIn(animationSpec = tween(300)) togetherWith
                                slideOutHorizontally(
                                    targetOffsetX = { fullWidth -> -fullWidth },
                                    animationSpec = tween(300)
                                ) + fadeOut(animationSpec = tween(300))
                    } else {
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> -fullWidth },
                            animationSpec = tween(300)
                        ) + fadeIn(animationSpec = tween(300)) togetherWith
                                slideOutHorizontally(
                                    targetOffsetX = { fullWidth -> fullWidth },
                                    animationSpec = tween(300)
                                ) + fadeOut(animationSpec = tween(300))
                    }.using(
                        SizeTransform(clip = false)
                    )
                },
                label = "WizardStepTransition"
            ) { step ->
                when (step) {
                    0 -> WelcomeStepContent(modifier = Modifier.fillMaxSize())
                    1 -> LoginStepContent(
                        modifier = Modifier.fillMaxSize(),
                        loginForm = uiState.loginForm,
                        onUsernameChange = { viewModel.updateLoginForm(username = it) },
                        onPasswordChange = { viewModel.updateLoginForm(password = it) },
                        isLoading = uiState.isLoading
                    )

                    2 -> AllSetStepContent(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}

@Composable
fun WelcomeStepContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "欢迎使用!",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "此向导将引导您完成应用程序的初始设置。请点击“下一步”继续。",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun LoginStepContent(
    modifier: Modifier = Modifier,
    loginForm: SettingsWizardUiState.LoginForm,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    isLoading: Boolean
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "登录您的账户",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        OutlinedTextField(
            value = loginForm.username,
            onValueChange = onUsernameChange,
            label = { Text("用户名") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(0.9f),
            enabled = !isLoading
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = loginForm.password,
            onValueChange = onPasswordChange,
            label = { Text("密码 (至少6位)") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(0.9f),
            enabled = !isLoading
        )
        if (isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }
    }
}

@Composable
fun AllSetStepContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = "完成图标",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "一切就绪!",
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "您的应用程序已成功设置。点击“完成”开始使用。",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun WizardBottomNavigation(
    currentStep: Int,
    totalSteps: Int,
    onNextClicked: () -> Unit,
    onBackClicked: () -> Unit,
    //onLoginClicked: () -> Unit,
    onCompleteClicked: () -> Unit,
    isNextEnabled: Boolean,
    isLoading: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (currentStep > 0) {
            Button(
                onClick = onBackClicked,
                enabled = !isLoading
            ) {
                Text("上一步")
            }
        } else {
            Spacer(Modifier.weight(1f)) // 占位符，使 "下一步" 按钮在第一页时能正确对齐到右边
        }

        // 如果是登录步骤 (currentStep == 1)
        when {
            currentStep == 1 -> {
                Button(
                    onClick = onNextClicked,
                    enabled = isNextEnabled && !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = LocalContentColor.current, // 使用按钮文字颜色
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("登录中...")
                    } else {
                        Text("登录并继续")
                    }
                }
            }

            currentStep < totalSteps - 1 -> { // 中间步骤的 "下一步"
                Button(
                    onClick = onNextClicked,
                    enabled = isNextEnabled && !isLoading // 确保isNextEnabled在这里也起作用
                ) {
                    Text("下一步")
                }
            }

            currentStep == totalSteps - 1 -> { // 最后一步 "完成"
                Button(
                    onClick = onCompleteClicked,
                    enabled = !isLoading
                ) {
                    Text("完成")
                }
            }
        }
    }
}

// 为了在ViewModel中清除错误信息，你可能需要在ViewModel中添加一个类似这样的方法：
// fun clearErrorMessage() {
// _uiState.update { it.copy(errorMessage = null) }
// }

// 预览 (可选，但推荐)
@Preview(showBackground = true, name = "Welcome Step Preview")
@Composable
fun WelcomeStepPreview() {
    PmxvTheme(darkTheme = false, dynamicColor = true) {
        WelcomeStepContent(modifier = Modifier.fillMaxSize())
    }
}

@Preview(showBackground = true, name = "Login Step Preview (Not Loading)")
@Composable
fun LoginStepPreviewNotLoading() {
    PmxvTheme(darkTheme = false, dynamicColor = true) {
        LoginStepContent(
            modifier = Modifier.fillMaxSize(),
            loginForm = SettingsWizardUiState.LoginForm("user", "pass"),
            onUsernameChange = {},
            onPasswordChange = {},
            isLoading = false
        )
    }
}

@Preview(showBackground = true, name = "Login Step Preview (Loading)")
@Composable
fun LoginStepPreviewLoading() {
    PmxvTheme(darkTheme = false, dynamicColor = true) {
        LoginStepContent(
            modifier = Modifier.fillMaxSize(),
            loginForm = SettingsWizardUiState.LoginForm("user", "pass"),
            onUsernameChange = {},
            onPasswordChange = {},
            isLoading = true
        )
    }
}

@Preview(showBackground = true, name = "All Set Step Preview")
@Composable
fun AllSetStepPreview() {
    PmxvTheme(darkTheme = false, dynamicColor = true) {
        AllSetStepContent(modifier = Modifier.fillMaxSize())
    }
}

@Preview(showBackground = true, name = "Bottom Nav - First Step")
@Composable
fun BottomNavFirstStepPreview() {
    PmxvTheme(darkTheme = false, dynamicColor = true) {
        WizardBottomNavigation(0, 3, {}, {}, {}, true, false)
    }
}

@Preview(showBackground = true, name = "Bottom Nav - Login Step")
@Composable
fun BottomNavLoginStepPreview() {
    PmxvTheme(darkTheme = false, dynamicColor = true) {
        WizardBottomNavigation(1, 3, {}, {}, {}, true, false)
    }
}

@Preview(showBackground = true, name = "Bottom Nav - Login Step Loading")
@Composable
fun BottomNavLoginStepLoadingPreview() {
    PmxvTheme(darkTheme = false, dynamicColor = true) {
        WizardBottomNavigation(1, 3, {}, {}, {}, false, true)
    }
}

@Preview(showBackground = true, name = "Bottom Nav - Last Step")
@Composable
fun BottomNavLastStepPreview() {
    PmxvTheme(darkTheme = false, dynamicColor = true) {
        WizardBottomNavigation(2, 3, {}, {}, {}, true, false)
    }
}