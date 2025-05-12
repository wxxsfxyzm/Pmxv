package com.carlyu.pmxv.remote.api.model.proxmox

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO for Proxmox Storage Status endpoint (/nodes/{node}/storage/{storageId}/status).
 * 提供节点上配置的特定存储的使用详情。
 * 注意: 这个结构与 NodeStatusDto 中的 rootfs (DiskInfoDto) 类似。
 */
@Serializable
data class StorageStatusDto(
    val storage: String? = null, // 存储 ID
    @SerialName("type") val storageType: String? = null, // 存储类型, 例如 "dir", "lvmthin", "nfs"
    val total: Long? = null, // 总容量 (字节)
    val used: Long? = null, // 已用空间 (字节)
    val avail: Long? = null, // 可用空间 (字节)
    val enabled: Int? = null, // 1 如果启用, 0 否则
    val active: Int? = null, // 1 如果活动, 0 否则
    @SerialName("content") val contentTypes: String? = null, // 允许的内容类型，逗号分隔，例如 "images,iso,backup"
    val shared: Int? = null, // 1 如果是共享存储, 0 否则
    val status: String? = null // 状态字符串, 例如 "active"
    // 根据 API 响应添加其他所需字段
)