package com.carlyu.pmxv.remote.api

import com.carlyu.pmxv.local.room.entity.AccountEntity
import com.carlyu.pmxv.local.room.repository.AccountRepository
import com.carlyu.pmxv.models.data.proxmox.PVEAuthenticationMethod.API_TOKEN
import com.carlyu.pmxv.models.data.proxmox.PVEAuthenticationMethod.PAM
import com.carlyu.pmxv.models.data.proxmox.PVEAuthenticationMethod.PVE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val accountRepository: AccountRepository
) : Interceptor {

    // StateFlow 来自 repository，提供对最新活动帐户的同步访问
    // 此 StateFlow 的 CoroutineScope 应与注入此 Interceptor 的 Scope 一致 (例如 SingletonComponent -> ApplicationScope)
    private val activeAccountState: StateFlow<AccountEntity?> =
        accountRepository.getActiveAccountFlow()
            .stateIn(
                scope = CoroutineScope(Dispatchers.IO + kotlinx.coroutines.SupervisorJob()), // 使用一个独立的 SupervisorJob
                started = SharingStarted.Eagerly, // 总是需要一个值或 null
                // 危险！runBlocking可能引起ANR
                // initialValue = runBlocking { accountRepository.getActiveAccount() } // 同步获取初始值，或者 null
                initialValue = null // 安全的初始值
            )

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        // 使用 .value 同步访问最新的 StateFlow 值
        val activeAccount = activeAccountState.value

        activeAccount?.let { account ->
            Timber.tag("AuthInterceptor")
                .d("Active account found: ${account.name}, AuthMethod: ${account.authMethod}")
            when (account.authMethod) {
                API_TOKEN -> {
                    // getFullApiTokenHeaderValue 需要处理解密
                    account.getFullApiTokenHeaderValue()?.let { tokenHeader ->
                        requestBuilder.header("Authorization", tokenHeader)
                        Timber.tag("AuthInterceptor")
                            .v("Added API Token header for ${account.name}")
                    } ?: Timber.tag("AuthInterceptor")
                        .w("Active API Token account ${account.name} has no valid token to add.")
                }

                PAM -> {
                    // PAM 认证使用 PVEAuthCookie (由 OkHttp CookieJar 管理)
                    // 和 CSRFPreventionToken 标头 (用于 POST, PUT, DELETE)
                    // CookieJar 应该在 OkHttpClient 中配置

                    account.currentCsrfToken?.let { csrfToken ->
                        val method = originalRequest.method.uppercase()
                        if (method == "POST" || method == "PUT" || method == "DELETE") {
                            requestBuilder.header("CSRFPreventionToken", csrfToken)
                            Timber.tag("AuthInterceptor")
                                .v("Added CSRFPreventionToken header for ${account.name} (${method})")
                        }
                    } ?: Timber.tag("AuthInterceptor").d(
                        "%sCookie (PVEAuthCookie) should be handled by CookieJar.",
                        "PAM account ${account.name}: No CSRF token to add. (Method: ${originalRequest.method}) " +
                                "This is expected for GET requests or if token is not set. "
                    )
                }
                // 为 PVEAuthenticationMethodType.PVE 添加处理（如果需要）
                PVE -> TODO()
            }
        } ?: Timber.tag("AuthInterceptor").v("No active account. Proceeding without auth headers.")

        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}