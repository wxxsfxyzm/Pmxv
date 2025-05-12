package com.carlyu.pmxv.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.carlyu.pmxv.models.data.proxmox.PVEAuthenticationMethod // Your enum for auth methods

@Entity(
    tableName = "accounts",
    indices = [Index(value = ["server_url", "unique_auth_identifier"], unique = true)]
)
data class AccountEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0, // 主键通常可以是 val

    @ColumnInfo(name = "name")
    var name: String = "",

    @ColumnInfo(name = "server_url")
    var serverUrl: String = "", // 改为 var

    @ColumnInfo(
        name = "auth_method",
        typeAffinity = ColumnInfo.TEXT
    )
    var authMethod: PVEAuthenticationMethod = PVEAuthenticationMethod.PAM, // 改为 var

    @ColumnInfo(name = "unique_auth_identifier")
    var uniqueAuthIdentifier: String = "", // 改为 var

    // PAM/Username-Password Specific
    @ColumnInfo(name = "username")
    var username: String? = null, // 改为 var

    @ColumnInfo(name = "password_encrypted")
    var passwordEncrypted: String? = null, // 改为 var

    // API Token Specific
    @ColumnInfo(name = "api_token_id")
    var apiTokenId: String? = null, // 改为 var

    @ColumnInfo(name = "api_token_secret_encrypted")
    var apiTokenSecretEncrypted: String? = null, // 改为 var

    @Transient
    var currentCsrfToken: String? = null, // 已经是 var，保持

    // Connection Settings
    @ColumnInfo(name = "trust_self_signed_certs", defaultValue = "0")
    var trustSelfSignedCerts: Boolean = false, // 已经是 var，保持

    @ColumnInfo(name = "node_name")
    var nodeName: String? = null, // 改为 var

    // Status
    @ColumnInfo(name = "is_active", defaultValue = "0")
    var isActive: Boolean = false, // 已经是 var，保持

    @ColumnInfo(name = "server_version")
    var serverVersion: String? = null, // 改为 var

    @ColumnInfo(name = "last_login_timestamp", defaultValue = "0")
    var lastLoginTimestamp: Long = 0L // 改为 var
) {
    /**
     * Helper to get the full API token string (header value).
     * Returns null if not an API_TOKEN auth method or if tokens are missing.
     */
    fun getFullApiTokenHeaderValue(): String? {
        return if (authMethod == PVEAuthenticationMethod.API_TOKEN && !apiTokenId.isNullOrBlank() && !apiTokenSecretEncrypted.isNullOrBlank()) {
            // !!! DECRYPTION NEEDED for apiTokenSecretEncrypted !!!
            // For now, assuming apiTokenSecretEncrypted holds the raw secret for demonstration
            "PVEAPIToken=$apiTokenId=$apiTokenSecretEncrypted" // Replace with decrypted secret
        } else {
            null
        }
    }
}