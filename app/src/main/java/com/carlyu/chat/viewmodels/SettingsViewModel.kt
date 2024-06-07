package com.carlyu.chat.viewmodels

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.carlyu.chat.R
import com.carlyu.chat.models.data.BottomSheetContent
import com.carlyu.chat.models.data.ThemeStyleType
import com.carlyu.chat.ui.views.activities.LoginActivity
import com.carlyu.chat.utils.ToastUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    // BottomSheet
    val bottomSheetState = mutableStateOf(false)
    val bottomSheetContent = mutableStateOf<BottomSheetContent?>(null)

    // logout setting
    // val logoutDialogState = mutableStateOf(false)
    val finishActivity = MutableLiveData<Boolean>()

    // switchValue
    val switchState1 = mutableStateOf(sharedPreferences.getBoolean("switch_state_1", false))
    val switchState2 = mutableStateOf(sharedPreferences.getBoolean("switch_state_2", false))
    val switchState3 = mutableStateOf(sharedPreferences.getBoolean("switch_state_3", false))

    // UI Control Variables
    val uiMode = mutableStateOf(getThemeSetting()) // 添加这一行
    val useDynamicColor = mutableStateOf(sharedPreferences.getBoolean("dynamic_color", false))

    fun onLogoutClicked() {
        with(sharedPreferences.edit()) {
            putBoolean("is_logged_in", false)
            apply()
        }
        context.startActivity(
            Intent(
                context,
                LoginActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
        finishActivity.value = true
    }

    fun onSwitchChange1(newState: Boolean) {
        switchState1.value = newState
        with(sharedPreferences.edit()) {
            putBoolean("switch_state_1", newState)
            apply()
        }
        ToastUtils.showToast(context, "Switch 1 state is $newState")
    }

    fun onSwitchChange2(newState: Boolean) {
        switchState2.value = newState
        with(sharedPreferences.edit()) {
            putBoolean("switch_state_2", newState)
            apply()
        }
        Toast.makeText(context, "Switch 2 state is $newState", Toast.LENGTH_SHORT).show()
    }

    fun onSwitchChange3(newState: Boolean) {
        switchState3.value = newState
        with(sharedPreferences.edit()) {
            putBoolean("switch_state_3", newState)
            apply()
        }
        Toast.makeText(context, "Switch 3 state is $newState", Toast.LENGTH_SHORT).show()
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
        if (theme == ThemeStyleType.LIGHT) {
            context.setTheme(R.style.Theme_Chatdemo)
        }
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