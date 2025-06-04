package com.carlyu.pmxv.remote.api.model.proxmox

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO for the *data* field within the response from the /access/ticket endpoint.
 * 包含会话票据和 CSRF 令牌，用于后续的 API 调用。
 * @param csrfPreventionToken CSRF 预防令牌，用于 POST/PUT/DELETE 请求的 CSRF 保护。
 * @param ticket 会话票据，用于身份验证。
 * @param username 认证后的用户名，通常是 "root@pam"。
 * @param clusterName 集群名称，通常在较新的 Proxmox VE 版本中可用。
 */
@Serializable
data class ProxmoxTicketResponseDto(
    @SerialName("ticket") val ticket: String?, // 会话 cookie 值 (PVEAuthCookie)
    @SerialName("CSRFPreventionToken") val csrfPreventionToken: String?,
    @SerialName("username") val username: String?, // 认证后的用户名 (例如 "root@pam")
    //@SerialName("clustername") val clusterName: String? = null // 集群名称 (较新的 PVE版本中常见)
    // 如果票据响应的 'data' 对象中还有其他字段，请在此处添加
)