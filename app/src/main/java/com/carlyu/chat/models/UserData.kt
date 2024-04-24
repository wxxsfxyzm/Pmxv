package com.carlyu.chat.models

import com.carlyu.chat.models.data.DarkThemeConfig
import com.carlyu.chat.models.data.ThemeBrand

data class UserData(
    val themeBrand: ThemeBrand,
    val darkThemeConfig: DarkThemeConfig,
    val useDynamicColor: Boolean,
)