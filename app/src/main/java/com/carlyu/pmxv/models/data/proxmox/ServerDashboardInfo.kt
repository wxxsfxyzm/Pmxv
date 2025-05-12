package com.carlyu.pmxv.models.data.proxmox


data class ServerDashboardInfo(
    val serverName: String = "pve", // 默认测试数据
    var uptimeSeconds: Long = 0L, // 初始运行时间（秒）
    val cpuUsage: ResourceUsage = ResourceUsage(1.2f, 4.0f, "GHz", 0.3f),
    val memoryUsage: ResourceUsage = ResourceUsage(8.5f, 32.0f, "GB", 0.26f),
    val rootFsUsage: ResourceUsage = ResourceUsage(50.2f, 200.0f, "GB", 0.25f),
    val swapUsage: ResourceUsage = ResourceUsage(0.5f, 8.0f, "GB", 0.06f),
    val vmStats: GuestStats = GuestStats(3, 5),
    val lxcStats: GuestStats = GuestStats(2, 2)
)