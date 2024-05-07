package com.carlyu.chat.viewmodels

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel


class SettingsViewModel(context: Context) : ViewModel() {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    val switchState = mutableStateOf(sharedPreferences.getBoolean("switch_state", false))

    fun onSwitchChange(newState: Boolean) {

        switchState.value = newState
        with(sharedPreferences.edit()) {
            putBoolean("switch_state", newState)
            apply()
        }
    }
}