package com.carlyu.pmxv.models.data.proxmox

import java.util.Locale
import kotlin.math.ln
import kotlin.math.pow

/**
 * 表示资源（如 CPU, 内存, 存储, Swap）的使用情况。
 */
data class ResourceUsage(
    val used: Float,      // 已使用量 (基础单位，例如字节, 核心数)
    val total: Float,     // 总量 (基础单位)
    val unit: String,     // 用于展示的基本单位 (例如: "GB", "Cores")
    val percentage: Float // 使用百分比，范围 0.0f 到 1.0f (例如, 0.75 代表 75%)
) {
    // 方便在 UI 中显示的格式化已使用量
    val displayUsed: String
        get() = "%.1f %s".format(used, unit)

    // 方便在 UI 中显示的格式化总量
    val displayTotal: String
        get() = "%.1f %s".format(total, unit)

    // 方便在 UI 中显示的百分比字符串
    val displayPercentage: String
        get() = String.format(Locale.getDefault(), "%.0f%%", percentage * 100)

    // 辅助函数：如果你的 DTO 返回的是字节，而你想在 ResourceUsageType 中使用 GB/MB 等，
    // 可以在 Repository 的映射逻辑中使用这个函数来转换，然后在 ResourceUsageType 中直接存储转换后的值和单位。
    companion object {
        fun formatBytesToHumanReadable(bytes: Long, decimals: Int = 1): Pair<Float, String> {
            if (bytes <= 0) return Pair(0f, "B")
            val units = arrayOf("B", "KB", "MB", "GB", "TB", "PB")
            //"%.${decimals}f %s" is the intended format after conversion
            val digitGroups = (ln(bytes.toDouble()) / ln(1024.0)).toInt()
            val value = (bytes / 1024.0.pow(digitGroups.toDouble())).toFloat()
            return Pair(value, units[digitGroups])
        }

        fun calculatePercentage(used: Long?, total: Long?): Float {
            if (total == null || total == 0L || used == null) return 0.0f
            return (used.toDouble() / total.toDouble()).toFloat().coerceIn(0.0f, 1.0f)
        }
    }
}

