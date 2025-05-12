package com.carlyu.pmxv.remote.api

import com.carlyu.pmxv.local.annotation.InsecureClient
import com.carlyu.pmxv.local.annotation.StandardClient
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProxmoxApiProvider @Inject constructor(
    @StandardClient private val standardClient: OkHttpClient,
    @InsecureClient private val insecureClient: OkHttpClient,
    private val json: Json // 从 NetworkModule 获取 (需要调整 NetworkModule)
) {

    /**
     * 创建 ProxmoxApiService 实例
     * 为给定的基本 URL 创建 ProxmoxApiService 实例
     * 可选择禁用 SSL 证书验证
     * 基本 URL 不应包括"/api2/json/"。
     *
     * @param baseUrl 基础 URL
     * @param trustAllCertificates 是否信任所有证书
     * @return ProxmoxApiService 实例
     */
    fun createService(baseUrl: String, trustAllCertificates: Boolean): ProxmoxApiService {
        val client = if (trustAllCertificates) {
            Timber.w("Creating ProxmoxApiService with INSECURE SSL client for $baseUrl.")
            insecureClient
        } else {
            Timber.d("Creating ProxmoxApiService with standard SSL client for $baseUrl.")
            standardClient
        }

        val validatedBaseUrl = baseUrl.trimEnd('/') + "/api2/json/"
        Timber.d("Effective Retrofit Base URL: $validatedBaseUrl")

        return Retrofit.Builder()
            .client(client)
            .baseUrl(validatedBaseUrl)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(ProxmoxApiService::class.java)
    }
}