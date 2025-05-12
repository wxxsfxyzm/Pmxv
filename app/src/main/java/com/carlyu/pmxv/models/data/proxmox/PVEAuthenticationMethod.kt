package com.carlyu.pmxv.models.data.proxmox

enum class PVEAuthenticationMethod {
    PAM, // Linux PAM standard authentication
    PVE, // Proxmox VE Authentication Server (not implemented yet)
    API_TOKEN // Keep if you might revert or offer both
}