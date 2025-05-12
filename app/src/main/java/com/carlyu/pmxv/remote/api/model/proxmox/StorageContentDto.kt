package com.carlyu.pmxv.remote.api.model.proxmox

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO representing an item within a Proxmox storage's content.
 * Used for endpoints like /nodes/{node}/storage/{storage_id}/content
 */
@Serializable
data class StorageContentDto(
    val volid: String? = null, // Volume ID, e.g., "local:iso/ubuntu.iso" or "datastore:backup/vzdump-qemu-100..."
    @SerialName("content") val contentType: String? = null, // "iso", "backup", "vztmpl", "images", "rootdir"
    @SerialName("format") val format: String? = null, // e.g., "iso", "raw", "qcow2", "vma", "pbs-ct"
    val size: Long? = null, // Size in bytes
    @SerialName("vmid") val associatedVmid: String? = null, // VMID if content is associated with a guest (e.g. backup)
    @SerialName("notes") val notes: String? = null, // User notes for the content
    @SerialName("verification") val verificationStatus: VerificationStatusDto? = null, // For Proxmox Backup Server backups
    @SerialName("ctime") val creationTime: Long? = null, // Creation timestamp (epoch seconds)
    // Add other fields as needed, e.g., "encrypted", "protected", etc.
)

@Serializable
data class VerificationStatusDto(
    val state: String? = null, // e.g., "verified", "failed"
    val upid: String? = null // UPID of the verification task
)