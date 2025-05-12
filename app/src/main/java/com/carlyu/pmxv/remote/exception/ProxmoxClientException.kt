package com.carlyu.pmxv.remote.exception

import java.io.IOException

// Base class for Proxmox client-specific exceptions
sealed class ProxmoxClientException(message: String?, cause: Throwable?) :
    Exception(message, cause) {
    /**
     * 提供一个用户友好的错误消息。
     * 子类可以重写此方法以提供更具体的细节。
     */
    open fun getUserMessage(): String {
        return this.message ?: "发生了一个错误，但没有具体的错误信息。" // 基类默认实现
    }

    // Represents a network-related error (e.g., no internet, host not found, timeout)
    class NetworkException(cause: IOException) :
        ProxmoxClientException("Network error occurred: ${cause.message}", cause)

    // Represents an HTTP error response from the Proxmox API
    class ApiException(
        val code: Int,
        val errorBody: String?,
        message: String?, // Original HTTP message
        cause: Throwable? = null
    ) : ProxmoxClientException(message ?: "API returned an error code $code", cause) {
        // Helper to get a user-friendly message, perhaps parsing errorBody
        override fun getUserMessage(): String {
            // Implement logic to parse errorBody (which is often JSON in PVE) if needed
            // For simplicity, return code and start of body or original message
            val specificError = errorBody?.let { body ->
                // Try to parse common Proxmox error structures if any.
                // For now, just return a snippet.
                body.take(150)
            } ?: "No additional error details."
            return "API Error $code: $specificError"
        }
    }

    // Represents an unexpected response format or parsing error
    class SerializationException(message: String?, cause: Throwable?) :
        ProxmoxClientException(message ?: "Failed to parse API response.", cause)

    // Placeholder for PveExceptionAuthentication if needed (can be a subtype of ApiException for 401/403)
    class AuthenticationException(
        message: String?,
        cause: Throwable? = null,
        val httpCode: Int? = null
    ) :
        ProxmoxClientException(message ?: "Authentication failed.", cause)

    // Any other unexpected client-side error during the process
    class UnexpectedException(message: String?, cause: Throwable?) :
        ProxmoxClientException(message ?: "An unexpected client error occurred.", cause)
}