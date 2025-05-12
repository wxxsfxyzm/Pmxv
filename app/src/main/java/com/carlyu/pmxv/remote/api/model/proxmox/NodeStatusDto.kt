package com.carlyu.pmxv.remote.api.model.proxmox

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO for the Proxmox Node Status endpoint (/nodes/{node}/status).
 * 包含节点的整体资源使用情况和信息。
 */
@Serializable
data class NodeStatusDto(
    val id: String? = null, // 例如 "node/pve"
    val node: String? = null, // 节点名称, 例如 "pve"
    @SerialName("level") val logLevel: String? = null, // 例如 "info"
    val uptime: Long? = null, // 正常运行时间 (秒)
    val cpu: Float? = null, // 当前 CPU 使用率 (0.0 到 1.0)
    val loadavg: List<Float>? = null, // 负载平均值 (1, 5, 15 分钟)
    @SerialName("wait") val iowait: Float? = null, // I/O 等待 CPU 时间
    @SerialName("ksm") val kernelSamepageMerging: KsmDto? = null, // KSM 状态详情
    val memory: MemInfoDto? = null,
    val swap: MemInfoDto? = null,
    val rootfs: DiskInfoDto? = null, // 根文件系统使用情况 (可能为 null 或不存在)
    @SerialName("cpuinfo") val cpuinfo: CpuInfoDto? = null,
    @SerialName("pveversion") val pveVersion: String? = null, // Proxmox VE 版本
    @SerialName("kversion") val kernelVersion: String? = null, // Linux 内核版本
    val current_kernel: KernelInfoDto? = null // 当前运行的内核信息
    // 根据 /nodes/{node}/status 的 API 响应添加其他所需字段
)

/**
 * DTO for Kernel Samepage Merging information.
 */
@Serializable
data class KsmDto(
    @SerialName("shared") val sharedPages: Long? = null
    // KSM 相关的其他字段
)

/**
 * DTO for Memory/Swap information (nested in NodeStatusDto).
 */
@Serializable
data class MemInfoDto(
    val total: Long? = null, // 总内存/交换空间 (字节)
    val used: Long? = null, // 已用内存/交换空间 (字节)
    val free: Long? = null // 空闲内存/交换空间 (字节)
)

/**
 * DTO for Disk usage information (nested, e.g., rootfs in NodeStatusDto or for StorageStatusDto).
 */
@Serializable
data class DiskInfoDto(
    val total: Long? = null, // 总容量 (字节)
    val used: Long? = null, // 已用空间 (字节)
    val free: Long? = null, // 空闲空间 (字节)
    val avail: Long? = null, // 可用空间 (字节) (可能与 free 不同)
    // Proxmox API for storage might also include 'type', 'content', 'percent'
    @SerialName("type") val storageType: String? = null,
    @SerialName("mountpoint") val mountPoint: String? = null
)

/**
 * DTO for CPU information (nested in NodeStatusDto).
 */
@Serializable
data class CpuInfoDto(
    val cpus: Int? = null, // CPU 核心/线程总数
    val model: String? = null, // CPU 型号名称
    @SerialName("mhz") val mhz: String? = null, // CPU 速度 (MHz) - API 返回的是字符串
    @SerialName("user_hz") val userHz: Int? = null, // 定时器频率
    val cores: Int? = null, // 物理核心数
    val sockets: Int? = null, // CPU 插槽数
    @SerialName("flags") val flags: String? = null, // CPU 标志
    @SerialName("hvm") val hvmSupport: String? = null // HVM 支持状态
)

/**
 * DTO for current Kernel information.
 */
@Serializable
data class KernelInfoDto(
    @SerialName("version") val version: String? = null, // e.g., "6.5.11-7-pve"
    @SerialName("release") val release: String? = null // e.g., "6.5.11-7-pve"
)