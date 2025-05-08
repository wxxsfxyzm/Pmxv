package com.carlyu.pmxv.local.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    // 基础信息
    val VERSION_CODE = longPreferencesKey("version_code")
    val IS_WIZARD_COMPLETED = booleanPreferencesKey("is_wizard_completed")
    val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")

    // 用户设置
    val THEME = stringPreferencesKey("theme")
    val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
    val SWITCH_STATE_1 = booleanPreferencesKey("switch_state_1")
    val SWITCH_STATE_2 = booleanPreferencesKey("switch_state_2")
    val SWITCH_STATE_3 = booleanPreferencesKey("switch_state_3")
}