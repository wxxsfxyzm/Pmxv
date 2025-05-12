package com.carlyu.pmxv.remote.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 通用包装器，用于成功的 Proxmox API 响应。
 * 大多数 GET 请求在此 `data` 字段内返回实际数据。
 *
 * @param data 特定 API 端点返回的实际数据。
 */
@Serializable
data class ProxmoxApiResponse<T>(
    @SerialName("data") val data: T? = null // 数据在某些空响应或部分错误上可能为 null
    // 注意: Proxmox 的错误响应 (非 2xx) 通常由 Retrofit/OkHttp 处理，
    // 它们的 body 可能是自定义的 JSON，不一定遵循此包装结构。
    // 如果需要，可以添加顶层的 "errors" 或 "warnings" 字段，
    // 但通常更关注 data 字段或 HTTP 错误码。
)