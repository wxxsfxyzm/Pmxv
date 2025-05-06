package com.carlyu.pmxv.ui.views.viewmodels

import android.content.Context
import android.content.Intent
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carlyu.pmxv.R
import com.carlyu.pmxv.local.datastore.PreferencesKeys
import com.carlyu.pmxv.models.data.BottomSheetContent
import com.carlyu.pmxv.models.data.ThemeStyleType
import com.carlyu.pmxv.ui.views.activities.LoginActivity
import com.carlyu.pmxv.ui.views.uistate.SettingsUiState
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


@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    // UI State
    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    /*    // BottomSheet
        val bottomSheetState = mutableStateOf(false)
        val bottomSheetContent = mutableStateOf<BottomSheetContent?>(null)*/

    val finishActivity = MutableLiveData<Boolean>()

    /*    // switchValue
        val switchState1 = mutableStateOf(sharedPreferences.getBoolean("switch_state_1", false))
        val switchState2 = mutableStateOf(sharedPreferences.getBoolean("switch_state_2", false))
        val switchState3 = mutableStateOf(sharedPreferences.getBoolean("switch_state_3", false))

        // UI Control Variables
        val uiMode = mutableStateOf(getThemeSetting()) // 添加这一行
        val useDynamicColor = mutableStateOf(sharedPreferences.getBoolean("dynamic_color", false))*/

    init {
        viewModelScope.launch {
            try {
                val preferences = dataStore.data.first()
                _uiState.value = SettingsUiState.Success(
                    switchState1 = preferences[PreferencesKeys.SWITCH_STATE_1] == true,
                    switchState2 = preferences[PreferencesKeys.SWITCH_STATE_2] == true,
                    switchState3 = preferences[PreferencesKeys.SWITCH_STATE_3] == true,
                    uiMode = parseTheme(preferences[PreferencesKeys.THEME]),
                    useDynamicColor = preferences[PreferencesKeys.DYNAMIC_COLOR] == true,
                    bottomSheetVisible = false,
                    bottomSheetContent = null
                )
            } catch (e: Exception) {
                _uiState.value = SettingsUiState.Error("初始化失败: ${e.message}")
            }
        }
        observePreferences()
    }

    fun onLogoutClicked() {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.IS_LOGGED_IN] = false
            }
            context.startActivity(
                Intent(context, LoginActivity::class.java)
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

    fun showBottomSheet(content: BottomSheetContent) {
        viewModelScope.launch {
            // 更新 UI 状态
            _uiState.update { currentState ->
                if (currentState is SettingsUiState.Success) {
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
                if (currentState is SettingsUiState.Success) {
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
                    _uiState.value = SettingsUiState.Error("数据加载失败: ${e.message}")
                }
                .collect { preferences ->
                    val currentState = _uiState.value as? SettingsUiState.Success

                    _uiState.value = SettingsUiState.Success(
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
            e.printStackTrace()
            // 处理无效主题字符串
            ThemeStyleType.FOLLOW_SYSTEM
        } catch (e: NullPointerException) {
            // 处理空值情况
            ThemeStyleType.FOLLOW_SYSTEM
        }
    }
}