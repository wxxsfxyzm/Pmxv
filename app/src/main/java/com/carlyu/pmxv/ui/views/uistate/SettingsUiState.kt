package com.carlyu.pmxv.ui.views.uistate

import com.carlyu.pmxv.models.data.view.BottomSheetContentType
import com.carlyu.pmxv.models.data.view.ThemeStyleType

sealed class SettingsUiState {
    object Loading : SettingsUiState()
    data class Success(
        // 设置项状态
        val switchState1: Boolean,
        val switchState2: Boolean,
        val switchState3: Boolean,
        val uiMode: ThemeStyleType,
        val useDynamicColor: Boolean,

        // BottomSheet状态
        // BottomSheet 状态
        val bottomSheetVisible: Boolean = false,
        val bottomSheetContent: BottomSheetContentType? = null

    ) : SettingsUiState()

    data class Error(val message: String) : SettingsUiState()
}