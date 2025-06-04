package com.carlyu.pmxv.ui.views.viewmodels

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carlyu.pmxv.R
import com.carlyu.pmxv.local.datastore.PreferencesKeys
import com.carlyu.pmxv.models.data.view.BottomSheetContentType
import com.carlyu.pmxv.models.data.view.ThemeStyleType
import com.carlyu.pmxv.ui.views.uistate.SettingsState
import com.carlyu.pmxv.utils.ToastUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * SettingsViewModel 是一个 ViewModel 类，用于管理设置界面的状态和逻辑。
 * 它使用 Jetpack Compose 和 Hilt 进行依赖注入。
 * ViewModel 负责处理用户的设置操作，并与 DataStore 进行交互以保存和加载设置。
 *
 * @param context 应用程序上下文
 * @param dataStore 用于存储和检索用户设置的 DataStore 实例
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    // UI State
    private val _uiState = MutableStateFlow<SettingsState>(SettingsState.Loading)
    val uiState: StateFlow<SettingsState> = _uiState.asStateFlow()

    val finishActivity = MutableLiveData<Boolean>()

    init {
        viewModelScope.launch {
            try {
                val preferences = dataStore.data.first()
                _uiState.value = SettingsState.Success(
                    switchState1 = preferences[PreferencesKeys.SWITCH_STATE_1] == true,
                    switchState2 = preferences[PreferencesKeys.SWITCH_STATE_2] == true,
                    switchState3 = preferences[PreferencesKeys.SWITCH_STATE_3] == true,
                    uiMode = parseTheme(preferences[PreferencesKeys.THEME]),
                    useDynamicColor = preferences[PreferencesKeys.DYNAMIC_COLOR] == true,
                    bottomSheetVisible = false,
                    bottomSheetContent = null
                )
            } catch (e: Exception) {
                _uiState.value = SettingsState.Error("初始化失败: ${e.message}")
            }
        }
        observePreferences()
    }

    fun onLogoutClicked() {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.IS_LOGGED_IN] = false
                // Caution: This will reset the wizard state
                preferences[PreferencesKeys.IS_WIZARD_COMPLETED] = false
            }
            context.startActivity(
                Intent(context, TODO("Implement Login Activity"))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
            finishActivity.postValue(true)
        }
    }

    fun onSwitchChange1(newState: Boolean) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.SWITCH_STATE_1] = newState
            }
            ToastUtils.showToast(context, "Switch 1 state is $newState")
        }
    }

    fun onSwitchChange2(newState: Boolean) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.SWITCH_STATE_2] = newState
            }
            ToastUtils.showToast(context, "Switch 2 state is $newState")
        }
    }

    fun onSwitchChange3(newState: Boolean) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.SWITCH_STATE_3] = newState
            }
            ToastUtils.showToast(context, "Switch 3 state is $newState")
        }
    }


    fun toggleDynamicColor(newState: Boolean) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.DYNAMIC_COLOR] = newState
            }
        }
    }

    fun changeThemeStyle(theme: ThemeStyleType) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.THEME] = theme.toString()
            }
            if (theme == ThemeStyleType.LIGHT) {
                context.setTheme(R.style.Theme_Pmxv)
            }
        }
    }

    fun showBottomSheet(content: BottomSheetContentType) {
        viewModelScope.launch {
            // 更新 UI 状态
            _uiState.update { currentState ->
                if (currentState is SettingsState.Success) {
                    currentState.copy(
                        bottomSheetVisible = true,
                        bottomSheetContent = content
                    )
                } else {
                    currentState
                }
            }
        }
    }

    fun dismissBottomSheet() {
        viewModelScope.launch {
            _uiState.update { currentState ->
                if (currentState is SettingsState.Success) {
                    currentState.copy(
                        bottomSheetVisible = false,
                        bottomSheetContent = null
                    )
                } else {
                    currentState
                }
            }
        }
    }

    private fun observePreferences() {
        viewModelScope.launch {
            dataStore.data
                .catch { e ->
                    _uiState.value = SettingsState.Error("数据加载失败: ${e.message}")
                }
                .collect { preferences ->
                    val currentState = _uiState.value as? SettingsState.Success

                    _uiState.value = SettingsState.Success(
                        switchState1 = preferences[PreferencesKeys.SWITCH_STATE_1] == true,
                        switchState2 = preferences[PreferencesKeys.SWITCH_STATE_2] == true,
                        switchState3 = preferences[PreferencesKeys.SWITCH_STATE_3] == true,
                        uiMode = parseTheme(preferences[PreferencesKeys.THEME]),
                        useDynamicColor = preferences[PreferencesKeys.DYNAMIC_COLOR] == true,
                        // 保持当前 BottomSheet 状态
                        bottomSheetVisible = currentState?.bottomSheetVisible == true,
                        bottomSheetContent = currentState?.bottomSheetContent
                    )
                }
        }
    }

    private fun parseTheme(value: String?): ThemeStyleType {
        return try {
            ThemeStyleType.valueOf(value ?: ThemeStyleType.FOLLOW_SYSTEM.toString())
        } catch (e: IllegalArgumentException) {
            Log.e("parseTheme:IllegalArgumentException", e.printStackTrace().toString())
            // 处理无效主题字符串
            ThemeStyleType.FOLLOW_SYSTEM
        } catch (e: NullPointerException) {
            Log.e("parseTheme:NullPointerException", e.printStackTrace().toString())
            // 处理空值情况
            ThemeStyleType.FOLLOW_SYSTEM
        }
    }
}