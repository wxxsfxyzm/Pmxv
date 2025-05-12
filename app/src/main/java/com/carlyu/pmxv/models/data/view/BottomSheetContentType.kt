package com.carlyu.pmxv.models.data.view

sealed class BottomSheetContentType {
    // 通用检查更新类型
    data class CheckUpdates(
        val title: String = "Check Updates",
        val description: String = "Under Development\nFor Test Purposes Only"
    ) : BottomSheetContentType()

    // 其他场景示例
    data class Confirmation(
        val title: String,
        val message: String
    ) : BottomSheetContentType()

    // 添加更多场景...
}