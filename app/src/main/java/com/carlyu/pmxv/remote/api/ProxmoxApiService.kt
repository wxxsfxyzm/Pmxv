package com.carlyu.pmxv.remote.api

import com.carlyu.pmxv.remote.api.model.ProxmoxApiResponse
import com.carlyu.pmxv.remote.api.model.proxmox.GuestDto
import com.carlyu.pmxv.remote.api.model.proxmox.NodeStatusDto
import com.carlyu.pmxv.remote.api.model.proxmox.ProxmoxTicketResponseDto
import com.carlyu.pmxv.remote.api.model.proxmox.StorageContentDto
import com.carlyu.pmxv.remote.api.model.proxmox.StorageStatusDto
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ProxmoxApiService {

    // PAM Login (Username/Password) - No custom auth headers needed for this call itself.
    @FormUrlEncoded
    @POST("access/ticket")
    suspend fun loginWithPassword(
        @Field("username") username: String, // e.g., root@pam
        @Field("password") password: String,
        @Field("realm") realm: String = "pam", // Default to PAM
    ): Response<ProxmoxApiResponse<ProxmoxTicketResponseDto>>

    // --- 需要认证的方法 ---
    // OkHttp CookieJar 会处理来自 loginWithPassword 的 PVEAuthCookie。
    // AuthInterceptor 将处理添加 Authorization (API Token) 或 CSRFPreventionToken (PAM + POST/PUT/DELETE) + Cookie (PAM)
    // 这些方法不再需要用于 AUTH 的显式 Header 参数

    @GET("nodes/{node}/status")
    suspend fun getNodeStatus(
        @Path("node") node: String,
        // @Header("Authorization") apiTokenHeader: String? = null, // "PVEAPIToken=..."
        // @Header("CSRFPreventionToken") csrfTokenHeader: String? = null // 用于票据认证的修改请求
    ): Response<ProxmoxApiResponse<NodeStatusDto>>

    @GET("nodes/{node}/qemu")
    suspend fun getQemuVms(
        @Path("node") node: String,
//        @Header("Authorization") apiTokenHeader: String? = null,
//        @Header("CSRFPreventionToken") csrfTokenHeader: String? = null
    ): Response<ProxmoxApiResponse<List<GuestDto>>>

    @GET("nodes/{node}/lxc")
    suspend fun getLxcContainers(
        @Path("node") node: String,
//        @Header("Authorization") apiTokenHeader: String? = null,
//        @Header("CSRFPreventionToken") csrfTokenHeader: String? = null
    ): Response<ProxmoxApiResponse<List<GuestDto>>>

    @GET("nodes/{node}/storage/{storageId}/status")
    suspend fun getStorageStatus(
        @Path("node") node: String,
        @Path("storageId") storageId: String,
//        @Header("Authorization") apiTokenHeader: String? = null,
//        @Header("CSRFPreventionToken") csrfTokenHeader: String? = null
    ): Response<ProxmoxApiResponse<StorageStatusDto>>

    @GET("nodes/{node}/storage/{storageId}/content")
    suspend fun getStorageContent(
        @Path("node") node: String,
        @Path("storageId") storageId: String,
//        @Header("Authorization") apiTokenHeader: String? = null,
//        @Header("CSRFPreventionToken") csrfTokenHeader: String? = null,
        @Query("content") contentType: String? = null // 可选，用于过滤内容类型
    ): Response<ProxmoxApiResponse<List<StorageContentDto>>>

    // 例如：启动 VM (POST 请求)
    // @POST("nodes/{node}/qemu/{vmid}/status/start")
    // suspend fun startVm(
    //     @Path("node") node: String,
    //     @Path("vmid") vmid: Int,
    //     @Header("Authorization") apiTokenHeader: String? = null,
    //     @Header("CSRFPreventionToken") csrfTokenHeader: String // 使用票据认证时，POST/PUT/DELETE 需要
    //     // @Body emptyBody: RequestBody? = null // 如果 Proxmox API 需要一个空的请求体
    // ): Response<ProxmoxApiResponse<String>> // 响应可能是任务 ID

    // 添加其他 API 端点...
}