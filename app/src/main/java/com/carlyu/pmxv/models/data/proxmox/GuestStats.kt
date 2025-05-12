package com.carlyu.pmxv.models.data.proxmox

/**
 * 表示客户机（如 VM, LXC）的运行/总数统计信息。
 */
data class GuestStats(
    val running: Int, // 正在运行的数量
    val total: Int    // 总数量 (不包括模板)
) {
    // 方便在 UI 中显示的格式化字符串 (例如 "5 / 10")
    val display: String
        get() = "$running / $total"
}