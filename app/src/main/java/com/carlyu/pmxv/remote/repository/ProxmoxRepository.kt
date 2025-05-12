package com.carlyu.pmxv.remote.repository

// ... (imports: ApiResult, ProxmoxClientException, DTOs, etc.)

import com.carlyu.pmxv.models.data.proxmox.GuestStats
import com.carlyu.pmxv.models.data.proxmox.ResourceUsage
import com.carlyu.pmxv.models.data.proxmox.ServerDashboardInfo
import com.carlyu.pmxv.remote.api.ProxmoxApiService
import com.carlyu.pmxv.remote.api.model.ApiResult
import com.carlyu.pmxv.remote.api.model.ProxmoxApiResponse
import com.carlyu.pmxv.remote.api.model.proxmox.NodeStatusDto
import com.carlyu.pmxv.remote.api.model.proxmox.ProxmoxTicketResponseDto
import com.carlyu.pmxv.remote.exception.ProxmoxClientException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProxmoxRepository @Inject constructor() {

    private suspend fun <DTO_DATA_TYPE : Any> safeApiCallForProxmoxData(
        requestDescription: String = "API call", // 用于日志记录
        apiCall: suspend () -> Response<ProxmoxApiResponse<DTO_DATA_TYPE>>
    ): ApiResult<DTO_DATA_TYPE> {
        return try {
            val response = apiCall()
            val requestUrl = response.raw().request.url.toString()

            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.data != null) {
                    ApiResult.Success(apiResponse.data)
                } else {
                    val errorMessage =
                        "$requestDescription Success ($requestUrl) but 'data' field is null or response body is null."
                    Timber.e(errorMessage)
                    ApiResult.Failure(
                        ProxmoxClientException.SerializationException(
                            errorMessage,
                            null
                        )
                    )
                }
            } else {
                handleApiError(response, requestUrl, requestDescription)
            }
        } catch (e: HttpException) {
            val requestUrlStr = e.response()?.raw()?.request?.url.toString()
            Timber.e(e, "$requestDescription HTTP Exception for $requestUrlStr")
            val errorBody = try {
                e.response()?.errorBody()?.string()
            } catch (ioe: IOException) {
                "Unreadable error body"
            }
            ApiResult.Failure(
                ProxmoxClientException.ApiException(e.code(), errorBody, e.message(), e)
            )
        } catch (e: IOException) {
            Timber.e(e, "$requestDescription Network Exception (IOException)")
            ApiResult.Failure(ProxmoxClientException.NetworkException(e))
        } catch (e: Exception) {
            Timber.e(e, "$requestDescription Unexpected Exception: ${e.javaClass.simpleName}")
            ApiResult.Failure(ProxmoxClientException.UnexpectedException(e.message, e))
        }
    }

    private fun <T> handleApiError(
        response: Response<T>,
        requestUrl: String,
        requestDescription: String = "API"
    ): ApiResult.Failure {
        val errorBody = try {
            response.errorBody()?.string()
        } catch (ioe: IOException) {
            "Unreadable error body"
        }
        Timber.e(
            "$requestDescription HTTP Error: Code=${response.code()}, Message=${response.message()}, Body='${
                errorBody?.take(
                    300
                )
            }' for $requestUrl"
        )

        if (response.code() == 401 || response.code() == 403) {
            val messageDetail = if (response.code() == 401) "凭证无效或令牌错误。" else "权限被拒绝。"
            // 可以进一步解析 errorBody 来获取更具体的 PVE 错误原因
            return ApiResult.Failure(
                ProxmoxClientException.AuthenticationException(
                    messageDetail,
                    null,
                    response.code()
                )
            )
        }
        return ApiResult.Failure(
            ProxmoxClientException.ApiException(
                code = response.code(),
                errorBody = errorBody,
                message = response.message()
            )
        )
    }

    suspend fun loginWithPam(
        apiService: ProxmoxApiService,
        username: String,
        password: String
    ): ApiResult<ProxmoxTicketResponseDto> = withContext(Dispatchers.IO) {
        safeApiCallForProxmoxData(requestDescription = "PAM Login") {
            apiService.loginWithPassword(username = username, password = password)
        }
    }

    suspend fun validateApiToken(
        apiService: ProxmoxApiService,
        nodeName: String
    ): ApiResult<NodeStatusDto> = withContext(Dispatchers.IO) {
        safeApiCallForProxmoxData(requestDescription = "API Token Validation (Node Status)") {
            apiService.getNodeStatus(node = nodeName)
        }
    }

    suspend fun getServerDashboardInfo(
        apiService: ProxmoxApiService,
        nodeName: String,
        rootStorageId: String = "local" // 这应该来自帐户或设置
    ): ApiResult<ServerDashboardInfo> = coroutineScope { // 使用 coroutineScope 来并行
        // 拦截器处理添加 auth 标头。ApiService 方法现在很简洁。
        val nodeStatusDeferred = async(Dispatchers.IO) {
            safeApiCallForProxmoxData(requestDescription = "Node Status") {
                apiService.getNodeStatus(nodeName)
            }
        }
        val qemuVmsDeferred = async(Dispatchers.IO) {
            safeApiCallForProxmoxData(requestDescription = "QEMU List") {
                apiService.getQemuVms(nodeName)
            }
        }
        val lxcContainersDeferred = async(Dispatchers.IO) {
            safeApiCallForProxmoxData(requestDescription = "LXC List") {
                apiService.getLxcContainers(nodeName)
            }
        }
        val rootStorageStatusDeferred = async(Dispatchers.IO) {
            safeApiCallForProxmoxData(requestDescription = "Root Storage ($rootStorageId)") {
                apiService.getStorageStatus(
                    nodeName,
                    rootStorageId
                )
            }
        }

        val nodeStatusResult = nodeStatusDeferred.await()
        val qemuVmsResult = qemuVmsDeferred.await()
        val lxcContainersResult = lxcContainersDeferred.await()
        val rootStorageResult = rootStorageStatusDeferred.await()

        if (nodeStatusResult is ApiResult.Failure) {
            return@coroutineScope ApiResult.Failure(nodeStatusResult.exception) // 传播关键故障
        }
        val nodeStatus = (nodeStatusResult as ApiResult.Success).data

        val qemuVms = (qemuVmsResult as? ApiResult.Success)?.data ?: emptyList()
        if (qemuVmsResult is ApiResult.Failure)
            Timber.w("获取 QEMU VMs 部分失败: ${qemuVmsResult.exception}")

        val lxcContainers = (lxcContainersResult as? ApiResult.Success)?.data ?: emptyList()
        if (lxcContainersResult is ApiResult.Failure)
            Timber.w("获取 LXC Containers 部分失败: ${lxcContainersResult.exception.getUserMessage()}")

        val rootStorage = (rootStorageResult as? ApiResult.Success)?.data
        if (rootStorageResult is ApiResult.Failure)
            Timber.w("获取根存储 ($rootStorageId) 部分失败: ${rootStorageResult.exception.getUserMessage()}")

        // --- 将 DTOs 映射到业务模型 (ServerDashboardInfoType) ---
        val serverName = nodeName
        val uptimeSeconds = nodeStatus.uptime ?: 0L

        val cpuUsedPercent = nodeStatus.cpu ?: 0.0f
        val totalCpuCores = nodeStatus.cpuinfo?.cpus?.toFloat() ?: 1f
        val cpuUsage = ResourceUsage(
            used = totalCpuCores * cpuUsedPercent,
            total = totalCpuCores,
            unit = "Cores",
            percentage = cpuUsedPercent
        )

        val memTotalBytes = nodeStatus.memory?.total?.toFloat() ?: 0f
        val memUsedBytes = nodeStatus.memory?.used?.toFloat() ?: 0f
        val memoryUsage = ResourceUsage(
            used = memUsedBytes / (1024f * 1024 * 1024), // -> GB
            total = memTotalBytes / (1024f * 1024 * 1024), // -> GB
            unit = "GB",
            percentage = if (memTotalBytes > 0) memUsedBytes / memTotalBytes else 0f
        )

        val rootFsTotalBytes = rootStorage?.total?.toFloat() ?: 0f
        val rootFsUsedBytes = rootStorage?.used?.toFloat() ?: 0f // PVE API 返回 used, total, avail
        val rootFsUsage = ResourceUsage(
            used = rootFsUsedBytes / (1024f * 1024 * 1024), // -> GB
            total = rootFsTotalBytes / (1024f * 1024 * 1024), // -> GB
            unit = "GB",
            percentage = if (rootFsTotalBytes > 0) rootFsUsedBytes / rootFsTotalBytes else 0f
        )

        val swapTotalBytes = nodeStatus.swap?.total?.toFloat() ?: 0f
        val swapUsedBytes = nodeStatus.swap?.used?.toFloat() ?: 0f
        val swapUsage = ResourceUsage(
            used = swapUsedBytes / (1024f * 1024 * 1024), // -> GB
            total = swapTotalBytes / (1024f * 1024 * 1024), // -> GB
            unit = "GB",
            percentage = if (swapTotalBytes > 0) swapUsedBytes / swapTotalBytes else 0f
        )

        val vmStats = GuestStats(
            running = qemuVms.count { it.status == "running" && (it.template == null || it.template == 0 || it.template != 1) }, // Proxmox 'template' 通常是 0 或 1
            total = qemuVms.count { it.template == null || it.template == 0 || it.template != 1 }
        )

        val lxcStats = GuestStats(
            running = lxcContainers.count { it.status == "running" && (it.template == null || it.template == 0 || it.template != 1) },
            total = lxcContainers.count { it.template == null || it.template == 0 || it.template != 1 }
        )

        val dashboardInfo = ServerDashboardInfo(
            serverName = serverName,
            uptimeSeconds = uptimeSeconds,
            cpuUsage = cpuUsage,
            memoryUsage = memoryUsage,
            rootFsUsage = rootFsUsage,
            swapUsage = swapUsage,
            vmStats = vmStats,
            lxcStats = lxcStats
        )
        ApiResult.Success(dashboardInfo)
    }
}