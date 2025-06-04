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
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carlyu.pmxv.R
import com.carlyu.pmxv.models.data.proxmox.PVEAuthenticationMethod
import com.carlyu.pmxv.models.data.proxmox.PVEAuthenticationMethod.API_TOKEN
import com.carlyu.pmxv.models.data.proxmox.PVEAuthenticationMethod.PAM
import com.carlyu.pmxv.models.data.proxmox.PVEAuthenticationMethod.PVE
import com.carlyu.pmxv.models.entity.SettingsWizardNavigationAction
import com.carlyu.pmxv.ui.components.widgets.AuthMethodRadioButton
import com.carlyu.pmxv.ui.components.widgets.SettingSwitchRow
import com.carlyu.pmxv.ui.theme.PmxvTheme
import com.carlyu.pmxv.ui.views.uistate.SettingsWizardState
import com.carlyu.pmxv.ui.views.viewmodels.SettingsWizardViewModel
import kotlinx.coroutines.launch

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
    viewModel: SettingsWizardViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            val result = snackbarHostState.showSnackbar(
                message = message,
                actionLabel = "知道了", // Or use R.string.ok
                duration = SnackbarDuration.Long // Give user time to read
            )
            if (result == SnackbarResult.ActionPerformed || result == SnackbarResult.Dismissed) {
                viewModel.clearErrorMessage()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(
                            when (uiState.currentStep) {
                                0 -> R.string.welcome
                                1 -> R.string.login_account
                                2 -> R.string.setup_complete
                                else -> R.string.setup_wizard
                            }
                        )
                    )
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }, // Added for error messages
        bottomBar = {
            WizardBottomNavigation(
                currentStep = uiState.currentStep,
                totalSteps = 3, // 总共3步 (0, 1, 2)
                onNextClicked = { viewModel.handleNavigation(SettingsWizardNavigationAction.Next) },
                onBackClicked = { viewModel.handleNavigation(SettingsWizardNavigationAction.Back) },
                onCompleteClicked = { viewModel.completeWizard() },
                // For button enable/disable, don't set error from here
                isNextEnabled = viewModel.validateCurrentStep(allowEmptyErrorOverride = false),
                isLoading = uiState.isLoading
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            AnimatedContent(
                targetState = uiState.currentStep,
                modifier = Modifier.fillMaxSize(),
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
                    0 -> WelcomeStepScreen(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    )

                    1 -> LoginStepScreen(
                        modifier = Modifier
                            .fillMaxSize(),
                        //.padding(paddingValues), // 应用 padding
                        formState = uiState, // 传递整个 uiState 或相关部分
                        onUpdateLoginForm = { serverAddress, username, password, apiTokenId, apiTokenSecret, nodeName, accountName, disableSsl, savePassword ->
                            viewModel.updateLoginForm(
                                serverAddress,
                                username,
                                password,
                                apiTokenId,
                                apiTokenSecret,
                                nodeName,
                                accountName,
                                disableSsl,
                                savePassword
                            )
                        },
                        onAuthMethodChange = { method -> viewModel.updateSelectedAuthMethod(method) },
                        isLoading = uiState.isLoading
                    )

                    2 -> AllSetStepScreen(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}

@Composable
fun WizardBottomNavigation(
    currentStep: Int,
    totalSteps: Int,
    onNextClicked: () -> Unit,
    onBackClicked: () -> Unit,
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

@Composable
fun WelcomeStepScreen(modifier: Modifier = Modifier) {
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
            text = "",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun LoginStepScreen(
    modifier: Modifier = Modifier,
    formState: SettingsWizardState, // 传递相关的状态部分
    onUpdateLoginForm: (
        serverAddress: String?, username: String?, password: String?,
        apiTokenId: String?, apiTokenSecret: String?, nodeName: String?,
        accountName: String?, disableSsl: Boolean?, savePassword: Boolean?
    ) -> Unit,
    onAuthMethodChange: (PVEAuthenticationMethod) -> Unit,
    isLoading: Boolean
) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    // 合并 requesters，因为 serverAddress 和 accountName 等字段是共用的
    val serverAddressRequester = remember { BringIntoViewRequester() }
    val accountNameRequester = remember { BringIntoViewRequester() }


    // 每种认证类型的特定 requesters
    val pamUsernameRequester = remember { BringIntoViewRequester() }
    val pamPasswordRequester = remember { BringIntoViewRequester() }
    val apiTokenNodeRequester = remember { BringIntoViewRequester() }
    val apiTokenIdRequester =
        remember { BringIntoViewRequester() } // 修正 typo: BringIntoViewRequester
    val apiTokenSecretRequester = remember { BringIntoViewRequester() }


    Column(
        modifier = modifier // 传入的 padding 已由调用者应用
            // .fillMaxSize() // 移除, modifier 已包含
            .verticalScroll(scrollState)
            .padding(
                horizontal = 24.dp,
                vertical = 0.dp
            ), // 保留水平 padding, 如果 paddingTop 处理了垂直 padding，可调整
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = when (formState.selectedAuthMethod) {
                PAM -> "PAM Login"/*stringResource(R.string.settings_wizard_login_title_pam)*/
                API_TOKEN -> "API Token Login"/*stringResource(R.string.settings_wizard_login_title_api_token)*/
                PVE -> "Not implemented" // 需要处理
            },
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = when (formState.selectedAuthMethod) {
                PAM -> "Linux PAM"/*stringResource(R.string.settings_wizard_login_description_pam)*/
                API_TOKEN -> "User API Token"/*stringResource(R.string.settings_wizard_login_description_api_token)*/
                PVE -> "Not implemented" // 需要处理
            },
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Server Address (Common)
        OutlinedTextField(
            value = when (formState.selectedAuthMethod) {
                PAM -> formState.pamLoginForm.serverAddress
                API_TOKEN -> formState.apiLoginForm.serverAddress
                PVE -> "Not implemented" // 需要处理
            },
            onValueChange = {
                onUpdateLoginForm(
                    it,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
                )
            }, // 仅更新 serverAddress
            label = { Text(text = "Server Address"/*stringResource(R.string.setup_login_server_address)*/) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .bringIntoViewRequester(serverAddressRequester)
                .onFocusEvent { if (it.isFocused) coroutineScope.launch { serverAddressRequester.bringIntoView() } },
            enabled = !isLoading,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Uri,
                imeAction = ImeAction.Next
            )
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Account Name (Common)
        OutlinedTextField(
            value = when (formState.selectedAuthMethod) {
                PAM -> formState.pamLoginForm.accountName
                API_TOKEN -> formState.apiLoginForm.accountName
                PVE -> TODO()
            },
            onValueChange = {
                onUpdateLoginForm(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    it,
                    null,
                    null
                )
            }, // 仅更新accountName
            label = { Text(text = "Account Name" /*stringResource(R.string.setup_login_account_name)*/) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .bringIntoViewRequester(accountNameRequester)
                .onFocusEvent { if (it.isFocused) coroutineScope.launch { accountNameRequester.bringIntoView() } },
            enabled = !isLoading,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        // --- Authentication Method Selection ---
        Text(
            text = "Auth Method"/*stringResource(R.string.setup_login_auth_method)*/,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(bottom = 4.dp)
        )
        Row(
            Modifier.fillMaxWidth(0.95f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AuthMethodRadioButton( // 来自你之前的代码
                text = "API Token"/*stringResource(R.string.auth_method_api_token_text)*/,
                description = "API Token Description"/*stringResource(R.string.auth_method_api_token_desc)*/,
                selected = formState.selectedAuthMethod == API_TOKEN,
                onClick = { onAuthMethodChange(API_TOKEN) },
                enabled = !isLoading
            )
        }
        Row(
            Modifier.fillMaxWidth(0.95f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AuthMethodRadioButton( // 来自你之前的代码
                text = "PAM"/*stringResource(R.string.auth_method_password_text)*/,
                description = "PAM Description"/*stringResource(R.string.auth_method_password_desc)*/,
                selected = formState.selectedAuthMethod == PAM,
                onClick = { onAuthMethodChange(PAM) },
                enabled = !isLoading
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // --- Fields specific to selected Auth Method ---
        when (formState.selectedAuthMethod) {
            PAM -> {
                val form = formState.pamLoginForm
                // Username (PAM)
                OutlinedTextField(
                    value = form.username,
                    onValueChange = {
                        onUpdateLoginForm(
                            null,
                            it,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null
                        )
                    }, // 仅更新username
                    label = { Text(text = stringResource(R.string.setup_login_username)) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .bringIntoViewRequester(pamUsernameRequester)
                        .onFocusEvent { if (it.isFocused) coroutineScope.launch { pamUsernameRequester.bringIntoView() } },
                    enabled = !isLoading,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    placeholder = { Text("例如: root 或 youruser@pam") }
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Password (PAM)
                OutlinedTextField(
                    value = form.password,
                    onValueChange = {
                        onUpdateLoginForm(
                            null,
                            null,
                            it,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null
                        )
                    }, // 仅更新password
                    label = { Text(text = stringResource(R.string.setup_login_password)) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .bringIntoViewRequester(pamPasswordRequester)
                        .onFocusEvent { if (it.isFocused) coroutineScope.launch { pamPasswordRequester.bringIntoView() } },
                    enabled = !isLoading
                )
            }

            API_TOKEN -> {
                val form = formState.apiLoginForm
                // Node Name (API Token)
                OutlinedTextField(
                    value = form.nodeName,
                    onValueChange = {
                        onUpdateLoginForm(
                            null,
                            null,
                            null,
                            null,
                            null,
                            it,
                            null,
                            null,
                            null
                        )
                    }, // 仅更新nodeName
                    label = { Text(text = "Node Name"/*stringResource(R.string.setup_login_node_name)*/) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .bringIntoViewRequester(apiTokenNodeRequester)
                        .onFocusEvent { if (it.isFocused) coroutineScope.launch { apiTokenNodeRequester.bringIntoView() } },
                    enabled = !isLoading,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))

                // API Token ID (API Token)
                OutlinedTextField(
                    value = form.apiTokenId,
                    onValueChange = {
                        onUpdateLoginForm(
                            null,
                            null,
                            null,
                            it,
                            null,
                            null,
                            null,
                            null,
                            null
                        )
                    },// 仅更新apiTokenId
                    label = { Text(text = "API Token ID"/*stringResource(R.string.setup_login_api_token_id)*/) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .bringIntoViewRequester(apiTokenIdRequester)
                        .onFocusEvent { if (it.isFocused) coroutineScope.launch { apiTokenIdRequester.bringIntoView() } },
                    enabled = !isLoading,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    placeholder = { Text("user@realm!tokenid") }
                )
                Spacer(modifier = Modifier.height(12.dp))

                // API Token Secret (API Token)
                OutlinedTextField(
                    value = form.apiTokenSecret,
                    onValueChange = {
                        onUpdateLoginForm(
                            null,
                            null,
                            null,
                            null,
                            it,
                            null,
                            null,
                            null,
                            null
                        )
                    },// 仅更新apiTokenSecret
                    label = { Text(text = "API Token Secret"/*stringResource(R.string.setup_login_api_token_secret)*/) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .bringIntoViewRequester(apiTokenSecretRequester)
                        .onFocusEvent { if (it.isFocused) coroutineScope.launch { apiTokenSecretRequester.bringIntoView() } },
                    enabled = !isLoading
                )
            }

            PVE -> TODO()
        }

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 8.dp)
        )

        Text(
            text = "Advanced Settings"/*stringResource(R.string.setup_login_advanced_settings)*/,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(bottom = 4.dp)
        )

        SettingSwitchRow( // 来自你之前的代码
            text = "Disable SSL"/*stringResource(R.string.setup_login_disable_ssl)*/,
            checked = when (formState.selectedAuthMethod) {
                PAM -> formState.pamLoginForm.disableSslValidation
                API_TOKEN -> formState.apiLoginForm.disableSslValidation
                PVE -> false
            },
            onCheckedChange = { isChecked ->
                onUpdateLoginForm(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    isChecked,
                    null
                )
            }, // 仅更新disableSsl
            enabled = !isLoading
        )

        if (formState.selectedAuthMethod == PVEAuthenticationMethod.PAM) {
            SettingSwitchRow( // 来自你之前的代码
                text = "Save Password"/*stringResource(R.string.setup_login_save_password)*/,
                checked = formState.pamLoginForm.savePassword,
                onCheckedChange = { isChecked ->
                    onUpdateLoginForm(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        isChecked
                    )
                }, // 仅更新savePassword
                enabled = !isLoading
            )
        }

        Spacer(modifier = Modifier.height(50.dp))
        if (isLoading) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
        }
        Spacer(modifier = Modifier.height(200.dp))
    }
}

@Composable
fun AllSetStepScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = stringResource(R.string.setup_complete),
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.setup_all_set),
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.setup_welcome_message),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
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
        WelcomeStepScreen(modifier = Modifier.fillMaxSize())
    }
}

/*@Preview(showBackground = true, name = "Login Step Preview (Not Loading)")
@Composable
fun LoginStepPreviewNotLoading() {
    PmxvTheme(darkTheme = false, dynamicColor = true) {
        LoginStepScreen(
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
        LoginStepScreen(
            modifier = Modifier.fillMaxSize(),
            loginForm = SettingsWizardUiState.LoginForm("user", "pass"),
            onUsernameChange = {},
            onPasswordChange = {},
            isLoading = true
        )
    }
}*/

@Preview(showBackground = true, name = "All Set Step Preview")
@Composable
fun AllSetStepPreview() {
    PmxvTheme(darkTheme = false, dynamicColor = true) {
        AllSetStepScreen(modifier = Modifier.fillMaxSize())
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