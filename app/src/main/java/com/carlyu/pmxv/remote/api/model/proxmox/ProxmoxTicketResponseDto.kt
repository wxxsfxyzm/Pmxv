package com.carlyu.pmxv.remote.api.model.proxmox

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO for the *data* field within the response from the /access/ticket endpoint.
 * 包含会话票据和 CSRF 令牌，用于后续的 API 调用。
 */
@Serializable
data class ProxmoxTicketResponseDto(
    @SerialName("CSRFPreventionToken") val csrfPreventionToken: String? = null,
    @SerialName("ticket") val ticket: String? = null, // 会话 cookie 值 (PVEAuthCookie)
    @SerialName("username") val username: String? = null, // 认证后的用户名 (例如 "root@pam")
    @SerialName("clustername") val clusterName: String? = null // 集群名称 (较新的 PVE版本中常见)
    // 如果票据响应的 'data' 对象中还有其他字段，请在此处添加
)