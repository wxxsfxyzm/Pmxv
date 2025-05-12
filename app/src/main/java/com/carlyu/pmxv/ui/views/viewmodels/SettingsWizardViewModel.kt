package com.carlyu.pmxv.ui.views.viewmodels

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carlyu.pmxv.local.datastore.PreferencesKeys
import com.carlyu.pmxv.local.room.entity.AccountEntity
import com.carlyu.pmxv.local.room.repository.AccountRepository
import com.carlyu.pmxv.models.data.proxmox.PVEAuthenticationMethod
import com.carlyu.pmxv.models.entity.SettingsWizardNavigationAction
import com.carlyu.pmxv.remote.api.ProxmoxApiProvider
import com.carlyu.pmxv.ui.views.uistate.SettingsWizardUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

/**
 * SettingsWizardViewModel 是设置向导的 ViewModel，负责管理向导的状态和逻辑。
 * 它使用 DataStore 来存储和读取用户的设置和向导状态。
 * 同时，它使用 AccountRepository 来保存验证通过的 Proxmox 账户信息。
 *
 * @param dataStore 用于存储用户设置的 DataStore 实例
 * @param accountRepository 用于管理 Proxmox 账户信息的仓库
 * @param proxmoxApiProvider 用于动态创建 Proxmox API 服务的提供者
 */
@HiltViewModel
class SettingsWizardViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val accountRepository: AccountRepository,
    private val proxmoxApiProvider: ProxmoxApiProvider,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsWizardUiState())
    val uiState: StateFlow<SettingsWizardUiState> = _uiState.asStateFlow()

    // StateFlow 暴露向导是否已完成的状态
    val isWizardCompleted: StateFlow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.IS_WIZARD_COMPLETED] == true
        }
        .stateIn(
            scope = viewModelScope,
            //started = SharingStarted.WhileSubscribed(5000), // 或者 Eagerly
            started = SharingStarted.Eagerly, // 用Eagerly UI响应速度更快?
            initialValue = false // 初始假设未完成，直到DataStore加载
        )

    init {
        // 可以在此加载全局的 trustSelfSignedCerts 设置来更新初始 UI State，
        // 但具体的 SSL 验证逻辑应该在你创建 ProxmoxApiService 时处理。
        // UI State 默认值已经包含了 disableSslValidation = false。
        viewModelScope.launch {
            val initialTrust =
                dataStore.data.map { it[PreferencesKeys.TRUST_SELF_SIGNED_CERTS] == true }.first()
            _uiState.update {
                it.copy(
                    pamLoginForm = it.pamLoginForm.copy(disableSslValidation = initialTrust),
                    apiLoginForm = it.apiLoginForm.copy(disableSslValidation = initialTrust)
                )
            }
        }
    }


    internal fun handleNavigation(action: SettingsWizardNavigationAction) {
        when (action) {
            is SettingsWizardNavigationAction.Next -> {
                if (_uiState.value.currentStep == 1 && !_uiState.value.isLoading) {
                    when (_uiState.value.selectedAuthMethod) {
                        PVEAuthenticationMethod.PAM -> attemptPamLoginAndSaveAccount()
                        PVEAuthenticationMethod.API_TOKEN -> attemptApiTokenLoginAndSaveAccount()
                        PVEAuthenticationMethod.PVE -> {
                            _uiState.update { it.copy(errorMessage = "PVE 认证方式暂未实现。") }
                        }
                    }
                } else if (_uiState.value.currentStep < 2) { // Max steps are 0, 1, 2
                    if (validateCurrentStep(allowEmptyErrorOverride = false)) { // Validate before moving for other steps if any
                        _uiState.update {
                            it.copy(
                                currentStep = it.currentStep + 1,
                                errorMessage = null
                            )
                        }
                    }
                    // If validation fails, validateCurrentStep might set an error message
                }
            }

            is SettingsWizardNavigationAction.Back -> {
                if (_uiState.value.currentStep > 0) {
                    _uiState.update {
                        it.copy(
                            currentStep = it.currentStep - 1,
                            errorMessage = null
                        )
                    }
                }
            }
        }
    }

    // ✅ 聚合更新表单字段的方法
    internal fun updateLoginForm(
        serverAddress: String? = null,
        username: String? = null,
        password: String? = null, // 仅用于 PAM
        apiTokenId: String? = null, // 仅用于 API_TOKEN
        apiTokenSecret: String? = null, // 仅用于 API_TOKEN
        nodeName: String? = null, // 仅用于 API_TOKEN 验证和 Account entity
        accountName: String? = null, // 用于两种
        disableSsl: Boolean? = null, // 用于两种
        savePassword: Boolean? = null // 仅用于 PAM
    ) {
        _uiState.update { state ->
            val newState = state.copy(errorMessage = null) // Clear error on input change (optional)
            when (newState.selectedAuthMethod) {
                PVEAuthenticationMethod.PAM ->
                    newState.copy(
                        pamLoginForm = newState.pamLoginForm.copy(
                            serverAddress = serverAddress ?: newState.pamLoginForm.serverAddress,
                            username = username ?: newState.pamLoginForm.username,
                            password = password ?: newState.pamLoginForm.password,
                            accountName = accountName ?: newState.pamLoginForm.accountName,
                            disableSslValidation = disableSsl
                                ?: newState.pamLoginForm.disableSslValidation,
                            savePassword = savePassword ?: newState.pamLoginForm.savePassword
                            // nodeName, apiTokenId, apiTokenSecret are ignored for PAM form
                        )
                    )

                PVEAuthenticationMethod.API_TOKEN ->
                    newState.copy(
                        apiLoginForm = newState.apiLoginForm.copy(
                            serverAddress = serverAddress ?: newState.apiLoginForm.serverAddress,
                            nodeName = nodeName ?: newState.apiLoginForm.nodeName,
                            apiTokenId = apiTokenId ?: newState.apiLoginForm.apiTokenId,
                            apiTokenSecret = apiTokenSecret ?: newState.apiLoginForm.apiTokenSecret,
                            accountName = accountName ?: newState.apiLoginForm.accountName,
                            disableSslValidation = disableSsl
                                ?: newState.apiLoginForm.disableSslValidation
                            // username, password, savePassword are ignored for API Token form
                        )
                    )

                PVEAuthenticationMethod.PVE -> {
                    /* ... (future PVE updates) ... */
                    // when branch must be exhaustive, so we need to handle this case
                    newState
                }
            }
        }
    }

    // ✅ 更新认证方式的方法
    internal fun updateSelectedAuthMethod(method: PVEAuthenticationMethod) {
        _uiState.update { it.copy(selectedAuthMethod = method, errorMessage = null) }
    }

    // ✅ PAM (用户名/密码) 登录逻辑 (基于之前实现的，确保 ProxmoxApiProvider 支持 disableSsl)
    private fun attemptPamLoginAndSaveAccount() {
        if (!validateCurrentStep()) {
            return
        }
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        val formData = _uiState.value.pamLoginForm
        viewModelScope.launch {
            try {
                // 使用 ProxmoxApiProvider 创建 Service，传入 disableSslValidation 状态
                // ProxmoxApiProvider.createService 需要修改以处理 trustAllCerts 参数
                val apiService = proxmoxApiProvider.createService(
                    formData.serverAddress,
                    formData.disableSslValidation
                ) // Pass SSL setting

                Timber.d("Attempting PAM login for user: ${formData.username} on server: ${formData.serverAddress}")
                // 直接将 UI 中输入的用户名和密码传递给 service
                // realm="pam" 会由 ProxmoxApiService 定义中的 @Field("realm") realm: String = "pam" 自动添加
                val response = apiService.loginWithPassword(
                    username = formData.username,
                    password = formData.password
                ) // ProxmoxApiService 中需添加此方法

                if (response.isSuccessful && response.body()?.data != null) {
                    val ticketData = response.body()!!.data!!
                    Timber.i("PAM login successful for account: ${formData.accountName}")

                    // ticketData.username 应该返回完整的 "username@realm" (例如 "root@pam")
                    // 这个完整的用户名是存储和后续使用的关键
                    val authenticatedUsername = ticketData.username
                    if (authenticatedUsername.isNullOrBlank()) {
                        Timber.e("Authenticated username from ticket is null or blank!")
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "登录成功但未能获取认证用户名。"
                            )
                        }
                        return@launch
                    }

                    // 创建 Account Entity
                    val newAccount = AccountEntity(
                        name = formData.accountName,
                        serverUrl = formData.serverAddress.trimEnd('/'),
                        authMethod = PVEAuthenticationMethod.PAM, // 使用你的枚举
                        uniqueAuthIdentifier = authenticatedUsername, // 例如, "root@pam"
                        username = authenticatedUsername, // 存储完整的 "username@realm"
                        // passwordEncrypted, ticket, csrfToken, trustSelfSignedCerts, nodeName, isActive 等等
                        passwordEncrypted = if (formData.savePassword) encryptPassword(formData.password) else null,
                        // ticket 和 csrfToken 通常不直接存储在 AccountEntity 中长期保存，
                        // PVEAuthCookie (ticket) 由 CookieJar 管理, CSRFToken 用于当前会话
                        // 但可以考虑将 CSRFPreventionToken 存在 AccountEntity 的 @Transient currentCsrfToken 字段
                        // (或者，如果需要跨应用重启的会话，则需要安全地存储它们并管理其生命周期)
                        trustSelfSignedCerts = formData.disableSslValidation,
                        nodeName = "default", // 或从ticketData.clusterName 或后续API调用获取
                        currentCsrfToken = ticketData.csrfPreventionToken,
                        // isActive 会由 AccountRepository 的 addAccount 方法处理
                    )
                    val accountId = accountRepository.addAccount(newAccount)
                    Timber.i("Account '${formData.accountName}' (ID: $accountId) saved.")

                    //markWizardCompleted() // 标记向导完成

                } else {
                    val errorBody = response.errorBody()?.string() ?: response.message()
                    Timber.e("PAM login failed: ${response.code()} - $errorBody")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "登录失败: ${response.code()} - ${errorBody.take(150)}"
                        )
                    }
                }
            } catch (e: HttpException) {
                Timber.e(e, "Login HTTP error")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "网络错误 (${e.code()}): ${e.message().take(100)}"
                    )
                }
            } catch (e: IOException) {
                Timber.e(e, "Login IO error (e.g., no network, host not found)")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "连接错误: ${e.message?.take(100)}"
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Login generic error")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "发生意外错误: ${e.message?.take(100)}"
                    )
                }
            }
        }
    }

    // ✅ API Token 登录逻辑 (基于最初实现的，确保 ProxmoxApiProvider 支持 disableSsl)
    private fun attemptApiTokenLoginAndSaveAccount() {
        if (!validateCurrentStep()) { // validateCurrentStep will set error message
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        val formData = _uiState.value.apiLoginForm
        viewModelScope.launch {
            try {
                // 使用 ProxmoxApiProvider 创建 Service，传入 disableSslValidation 状态
                val apiService = proxmoxApiProvider.createService(
                    formData.serverAddress,
                    formData.disableSslValidation
                ) // Pass SSL setting
                val fullToken = "PVEAPIToken=${formData.apiTokenId}=${formData.apiTokenSecret}"

                // Verify credentials by trying to fetch node status
                Timber.d("Attempting to verify API Token for node: ${formData.nodeName} on server: ${formData.serverAddress}")
                val response = apiService.getNodeStatus(
                    node = formData.nodeName
                ) // ProxmoxApiService 需要 getNodeStatus 方法，并且能接受 Authorization 头

                if (response.isSuccessful && response.body()?.data != null) {
                    Timber.i("API Token validated successfully for account: ${formData.accountName}")
                    val newAccount = AccountEntity(
                        name = formData.accountName,
                        serverUrl = formData.serverAddress.trimEnd('/'),
                        nodeName = formData.nodeName,
                        authMethod = PVEAuthenticationMethod.API_TOKEN,
                        apiTokenId = formData.apiTokenId,
                        apiTokenSecretEncrypted = formData.apiTokenSecret,
                        trustSelfSignedCerts = formData.disableSslValidation,
                        isActive = true // AccountRepository 的 addAccount 方法会处理唯一的 active flag
                    )
                    val accountId = accountRepository.addAccount(newAccount)
                    Timber.i("Account '${formData.accountName}' (ID: $accountId) saved.")

                    //markWizardCompleted() // 标记向导完成

                } else {
                    val errorBody = response.errorBody()?.string() ?: response.message()
                    Timber.e("API Token validation failed: ${response.code()} - $errorBody")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "验证失败: ${response.code()} - ${errorBody.take(150)}"
                        )
                    }
                }
            } catch (e: HttpException) {
                Timber.e(e, "Login HTTP error")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "网络错误 (${e.code()}): ${e.message().take(100)}"
                    )
                }
            } catch (e: IOException) {
                Timber.e(e, "Login IO error (e.g., no network, host not found)")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "连接错误: ${e.message?.take(100)}"
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Login generic error")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "发生意外错误: ${e.message?.take(100)}"
                    )
                }
            }
        }
    }

    // ✅ Aggregate validation logic
    internal fun validateCurrentStep(allowEmptyErrorOverride: Boolean = true): Boolean {
        return when (_uiState.value.currentStep) {
            1 -> { // Login step validation
                val isValid = when (_uiState.value.selectedAuthMethod) {
                    PVEAuthenticationMethod.PAM -> {
                        val form = _uiState.value.pamLoginForm
                        form.serverAddress.isNotBlank() && form.username.isNotBlank() && form.password.isNotBlank() && form.accountName.isNotBlank() &&
                                (form.serverAddress.startsWith(
                                    "http://",
                                    ignoreCase = true
                                ) || form.serverAddress.startsWith("https://", ignoreCase = true))
                        // Add more URL validation if needed
                    }

                    PVEAuthenticationMethod.API_TOKEN -> {
                        val form = _uiState.value.apiLoginForm
                        form.serverAddress.isNotBlank() && form.nodeName.isNotBlank() && form.apiTokenId.isNotBlank() && form.apiTokenSecret.isNotBlank() && form.accountName.isNotBlank() &&
                                (form.serverAddress.startsWith(
                                    "http://",
                                    ignoreCase = true
                                ) || form.serverAddress.startsWith("https://", ignoreCase = true))
                        // Add more URL validation if needed
                    }

                    PVEAuthenticationMethod.PVE -> {
                        // PVE authentication is not implemented yet
                        false
                    }
                }

                if (!isValid && allowEmptyErrorOverride) {
                    _uiState.update { it.copy(errorMessage = "请填写所有必填字段并确保服务器地址格式正确。") }
                } else if (isValid && _uiState.value.errorMessage == "请填写所有必填字段并确保服务器地址格式正确。") {
                    _uiState.update { it.copy(errorMessage = null) }
                }
                isValid
            }

            else -> true // Other steps are considered valid by default
        }
    }

    // 方法：标记向导已完成 (例如，如果用户跳过了登录或从其他路径完成)
    internal fun completeWizard() {
        viewModelScope.launch {
            // Only complete if on the last step (index 2)
            if (_uiState.value.currentStep == 2) {
                dataStore.edit { prefs ->
                    prefs[PreferencesKeys.IS_WIZARD_COMPLETED] = true
                }
                Timber.i("Settings wizard marked as completed via 'Finish' button.")
            }
            // _uiState 更新不是必须的，因为 Activity 会监听 isWizardCompleted
            Timber.i("Wizard marked as completed manually.")
        }
    }

    internal fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    // TODO: Implement actual password encryption using Android Keystore
    private fun encryptPassword(password: String): String {
        Timber.w("Using placeholder password encryption!")
        // THIS IS INSECURE - REPLACE WITH REAL ENCRYPTION
        return "ENCRYPTED:${password}" // Placeholder
    }

    // TODO: Implement actual password decryption using Android Keystore
    private fun decryptPassword(encryptedPassword: String): String {
        Timber.w("Using placeholder password decryption!")
        // THIS IS INSECURE - REPLACE WITH REAL DECRYPTION
        return encryptedPassword.removePrefix("ENCRYPTED:") // Placeholder
    }
}