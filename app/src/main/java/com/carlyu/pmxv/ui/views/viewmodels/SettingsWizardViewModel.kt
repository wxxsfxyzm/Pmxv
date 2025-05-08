package com.carlyu.pmxv.ui.views.viewmodels

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carlyu.pmxv.local.datastore.PreferencesKeys
import com.carlyu.pmxv.models.entity.SettingsWizardNavigationAction
import com.carlyu.pmxv.ui.views.uistate.SettingsWizardUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsWizardViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsWizardUiState())
    val uiState: StateFlow<SettingsWizardUiState> = _uiState.asStateFlow()

    // StateFlow 暴露向导是否已完成的状态
    val isWizardCompleted: StateFlow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.IS_WIZARD_COMPLETED] ?: false
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // 或者 Eagerly
            initialValue = false // 初始假设未完成，直到DataStore加载
        )

    internal fun handleNavigation(action: SettingsWizardNavigationAction) {
        when (action) {
            is SettingsWizardNavigationAction.Next -> {
                if (_uiState.value.currentStep == 1 && !_uiState.value.isLoading) { // 如果是登录页且未在加载
                    performLogin() // 触发登录，成功后会自动导航
                } else if (_uiState.value.currentStep < 2) { // 最大步骤索引是2 (0, 1, 2)
                    _uiState.update {
                        it.copy(
                            currentStep = it.currentStep + 1,
                            errorMessage = null
                        )
                    }
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

    internal fun updateLoginForm(username: String = "", password: String = "") {
        _uiState.update { state ->
            state.copy(
                loginForm = state.loginForm.copy(
                    username = username.ifEmpty { state.loginForm.username },
                    password = password.ifEmpty { state.loginForm.password }
                ),
                errorMessage = null // 清除因输入变化可能产生的旧错误
            )
        }
    }

    internal fun performLogin() {
        if (!validateCurrentStep()) { // 再次验证，防止直接调用
            _uiState.update { it.copy(errorMessage = "用户名或密码格式不正确。") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                delay(1000) // 模拟登录
                dataStore.edit { prefs ->
                    prefs[PreferencesKeys.IS_LOGGED_IN] = true
                    // 假设登录成功也是向导的一个重要完成步骤
                    // prefs[PreferencesKeys.IS_WIZARD_COMPLETED] = true
                }
                _uiState.update { it.copy(isLoading = false, currentStep = it.currentStep + 1) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "登录失败: ${e.message}"
                    )
                }
            }
        }
    }

    // 确保 validateCurrentStep 返回 Boolean
    internal fun validateCurrentStep(): Boolean {
        return when (_uiState.value.currentStep) {
            1 -> { // 登录页验证
                val form = _uiState.value.loginForm
                val isValid = form.username.isNotBlank() && form.password.length >= 6
                //if (!isValid) {
                // 可选：如果想在验证失败时立即显示错误，可以在这里更新errorMessage
                // _uiState.update { it.copy(errorMessage = "用户名和密码为必填项，密码至少6位。") }
                //}
                isValid
            }

            else -> true // 其他步骤默认有效
        }
    }

    // 方法：标记向导已完成 (当用户点击最后一步的“完成”按钮时调用)
    internal fun completeWizard() {
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[PreferencesKeys.IS_WIZARD_COMPLETED] = true
            }
            // 在这里不需要更新 _uiState.currentStep 或导航，
            // 因为 MainActivity 会响应 isWizardCompleted 的变化来切换界面。
        }
    }

    internal fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}