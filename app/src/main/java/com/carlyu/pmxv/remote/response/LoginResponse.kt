package com.carlyu.pmxv.remote.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    val data: LoginData?,  // 登录成功时有值，失败时为null
    val message: String?   // 登录失败时有值，成功时为null
)

data class LoginData(
    @SerializedName("CSRFPreventionToken")
    val csrfPreventionToken: String,
    val ticket: String,
    val username: String,
    val cap: Capabilities
)

// Capabilities及其子类保持原有结构不变（同之前的定义）
data class Capabilities(
    val vms: VMs,
    val access: Access,
    val dc: DC,
    val storage: Storage,
    val mapping: Mapping,
    val sdn: SDN,
    val nodes: Nodes
)

data class VMs(
    @SerializedName("VM.Snapshot.Rollback")
    val vmSnapshotRollback: Int,
    @SerializedName("VM.Migrate")
    val vmMigrate: Int,
    @SerializedName("VM.Config.Memory")
    val vmConfigMemory: Int,
    @SerializedName("VM.Snapshot")
    val vmSnapshot: Int,
    @SerializedName("VM.Audit")
    val vmAudit: Int,
    @SerializedName("VM.Allocate")
    val vmAllocate: Int,
    @SerializedName("VM.Monitor")
    val vmMonitor: Int,
    @SerializedName("VM.Config.Options")
    val vmConfigOptions: Int,
    @SerializedName("VM.Config.Network")
    val vmConfigNetwork: Int,
    @SerializedName("Permissions.Modify")
    val permissionsModify: Int,
    @SerializedName("VM.Config.CPU")
    val vmConfigCpu: Int,
    @SerializedName("VM.Config.Cloudinit")
    val vmConfigCloudinit: Int,
    @SerializedName("VM.Config.CDROM")
    val vmConfigCdrom: Int,
    @SerializedName("VM.Backup")
    val vmBackup: Int,
    @SerializedName("VM.Config.Disk")
    val vmConfigDisk: Int,
    @SerializedName("VM.Clone")
    val vmClone: Int,
    @SerializedName("VM.Config.HWType")
    val vmConfigHwType: Int,
    @SerializedName("VM.PowerMgmt")
    val vmPowerMgmt: Int,
    @SerializedName("VM.Console")
    val vmConsole: Int
)

data class Access(
    @SerializedName("User.Modify")
    val userModify: Int,
    @SerializedName("Group.Allocate")
    val groupAllocate: Int,
    @SerializedName("Permissions.Modify")
    val permissionsModify: Int
)

data class DC(
    @SerializedName("SDN.Use")
    val sdnUse: Int,
    @SerializedName("SDN.Allocate")
    val sdnAllocate: Int,
    @SerializedName("SDN.Audit")
    val sdnAudit: Int,
    @SerializedName("Sys.Modify")
    val sysModify: Int,
    @SerializedName("Sys.Audit")
    val sysAudit: Int
)

data class Storage(
    @SerializedName("Permissions.Modify")
    val permissionsModify: Int,
    @SerializedName("Datastore.AllocateSpace")
    val datastoreAllocateSpace: Int,
    @SerializedName("Datastore.Allocate")
    val datastoreAllocate: Int,
    @SerializedName("Datastore.AllocateTemplate")
    val datastoreAllocateTemplate: Int,
    @SerializedName("Datastore.Audit")
    val datastoreAudit: Int
)

data class Mapping(
    @SerializedName("Mapping.Modify")
    val mappingModify: Int,
    @SerializedName("Permissions.Modify")
    val permissionsModify: Int,
    @SerializedName("Mapping.Audit")
    val mappingAudit: Int,
    @SerializedName("Mapping.Use")
    val mappingUse: Int
)

data class SDN(
    @SerializedName("Permissions.Modify")
    val permissionsModify: Int,
    @SerializedName("SDN.Use")
    val sdnUse: Int,
    @SerializedName("SDN.Allocate")
    val sdnAllocate: Int,
    @SerializedName("SDN.Audit")
    val sdnAudit: Int
)

data class Nodes(
    @SerializedName("Sys.Audit")
    val sysAudit: Int,
    @SerializedName("Sys.PowerMgmt")
    val sysPowerMgmt: Int,
    @SerializedName("Sys.AccessNetwork")
    val sysAccessNetwork: Int,
    @SerializedName("Sys.Incoming")
    val sysIncoming: Int,
    @SerializedName("Sys.Modify")
    val sysModify: Int,
    @SerializedName("Sys.Console")
    val sysConsole: Int,
    @SerializedName("Sys.Syslog")
    val sysSyslog: Int,
    @SerializedName("Permissions.Modify")
    val permissionsModify: Int
)