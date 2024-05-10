package com.carlyu.chat.viewmodels

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.carlyu.chat.models.data.ThemeStyleType
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    // lateinit var isUiModeDark: MutableState<String?>

    val switchState = mutableStateOf(sharedPreferences.getBoolean("switch_state", false))
    val uiMode = mutableStateOf(getThemeSetting()) // 添加这一行
    val useDynamicColor = mutableStateOf(sharedPreferences.getBoolean("dynamic_color", false))

    fun onSwitchChange(newState: Boolean) {
        switchState.value = newState
        with(sharedPreferences.edit()) {
            putBoolean("switch_state", newState)
            apply()
        }
    }

    fun toggleDynamicColor(newState: Boolean) {
        useDynamicColor.value = newState
        with(sharedPreferences.edit()) {
            putBoolean("dynamic_color", newState)
            apply()
        }

    }

    fun changeThemeStyle(theme: ThemeStyleType) {
        sharedPreferences.edit().putString(
            "theme",
            theme.toString()
        ).apply()
        uiMode.value = theme
    }

    private fun getDynamicColorSetting(): Boolean {
        return sharedPreferences.getBoolean("dynamic_color", false)
    }

    private fun getThemeSetting(): ThemeStyleType {
        val themeString =
            sharedPreferences.getString("theme", ThemeStyleType.FOLLOW_SYSTEM.toString())
        return try {
            ThemeStyleType.valueOf(themeString ?: ThemeStyleType.FOLLOW_SYSTEM.toString())
        } catch (e: IllegalArgumentException) {
            ThemeStyleType.FOLLOW_SYSTEM
        }
    }
}