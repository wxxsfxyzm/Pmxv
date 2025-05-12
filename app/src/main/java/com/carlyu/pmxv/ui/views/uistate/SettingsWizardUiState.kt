package com.carlyu.pmxv.ui.views.uistate

import com.carlyu.pmxv.models.data.proxmox.PVEAuthenticationMethod

data class SettingsWizardUiState(
    val currentStep: Int = 0,
    val apiLoginForm: ProxmoxApiLoginForm = ProxmoxApiLoginForm(),
    val pamLoginForm: ProxmoxPamLoginForm = ProxmoxPamLoginForm(),
    val selectedAuthMethod: PVEAuthenticationMethod = PVEAuthenticationMethod.API_TOKEN, // 默认选中 Pam
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    // PAM 登录表单
    data class ProxmoxPamLoginForm(
        val serverAddress: String = "https://", // Example: https://your-proxmox-ip:8006
        val username: String = "root", // Default to root, user can change
        val password: String = "",
        val accountName: String = "", // User-defined name for this connection
        val disableSslValidation: Boolean = false, // New setting
        val savePassword: Boolean = false          // 安全警告: 直接保存密码非常不安全
    )

    // API Token 登录表单
    data class ProxmoxApiLoginForm(
        // Proxmox 登录需要的字段
        val serverAddress: String = "https://", // 例如 "https://your-pve-ip:8006"
        val nodeName: String = "pve", // Proxmox 节点名
        val apiTokenId: String = "", // API Token ID, 例如 "user@pam!mytoken"
        val apiTokenSecret: String = "", // API Token Secret (UUID)
        val accountName: String = "My Proxmox", // 用户给这个连接起的名字
        val disableSslValidation: Boolean = false
    )
}