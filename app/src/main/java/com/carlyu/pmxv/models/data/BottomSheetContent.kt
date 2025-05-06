package com.carlyu.pmxv.models.data

sealed class BottomSheetContent {
    // 通用检查更新类型
    data class CheckUpdates(
        val title: String = "Check Updates",
        val description: String = "Under Development\nFor Test Purposes Only"
    ) : BottomSheetContent()

    // 其他场景示例
    data class Confirmation(
        val title: String,
        val message: String
    ) : BottomSheetContent()

    // 添加更多场景...
}