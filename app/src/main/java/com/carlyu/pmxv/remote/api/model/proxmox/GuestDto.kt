package com.carlyu.pmxv.remote.api.model.proxmox

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO for VM (QEMU) and Container (LXC) listings.
 * 代表单个客户机 (VM 或容器)。
 */
@Serializable
data class GuestDto(
    val vmid: Int? = null,
    val name: String? = null,
    val status: String? = null, // "running", "stopped", "paused", etc.
    val type: String? = null, // "qemu" or "lxc"
    val uptime: Long? = null,
    val cpus: Float? = null, // 分配的 CPU 数量 (虚拟核心)
    val maxmem: Long? = null, // 最大内存 (字节)
    val mem: Long? = null, // 当前内存使用 (字节) - API 中更常见于客户机状态详情
    val maxdisk: Long? = null, // 最大磁盘大小 (字节) (根磁盘/卷的大小)
    @SerialName("disk") val diskUsageBytes: Long? = null, // 当前磁盘使用 (字节) - API 中更常见于客户机状态详情
    val template: Int? = null, // 1 如果是模板, 0 否则
    val node: String? = null, // 客户机运行所在的节点 (有时包含)

    // QEMU Specific fields (LXC 中可能为 null)
    val pid: Int? = null,
    val ha: HaInfoDto? = null,
    @SerialName("lock") val lockReason: String? = null,
    val tags: String? = null, // 标签，以分号分隔

    // LXC Specific fields (QEMU 中可能为 null)
    // 注意: /nodes/{node}/lxc/VMID/status/current 这样的端点会有更详细的 LXC 实时状态
    val diskread: Long? = null, // LXC aio/dio disk read bytes
    val diskwrite: Long? = null, // LXC aio/dio disk write bytes
    val netin: Long? = null, // LXC network in bytes
    val netout: Long? = null, // LXC network out bytes

    // 配置文件中的字段 (config 端点)
    val onboot: Int? = null // QEMU/LXC: 1 if autostart on boot, 0 otherwise
    // 根据需要添加其他字段 (例如防火墙, 描述, vm/ctid 别名)
)

/**
 * DTO for HA information (nested in GuestDto).
 */
@Serializable
data class HaInfoDto(
    val managed: Int? = null, // 1 如果由 HA 管理, 0 否则
    val state: String? = null, // HA 状态, 例如 "started", "stopped"
    val group: String? = null // HA 组名
)