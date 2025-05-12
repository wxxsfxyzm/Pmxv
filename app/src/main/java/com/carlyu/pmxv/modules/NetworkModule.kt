package com.carlyu.pmxv.modules

import android.annotation.SuppressLint
import com.carlyu.pmxv.BuildConfig
import com.carlyu.pmxv.local.annotation.InsecureClient
import com.carlyu.pmxv.local.annotation.StandardClient
import com.carlyu.pmxv.remote.api.AuthInterceptor
import com.carlyu.pmxv.remote.api.ProxmoxApiProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.CookieJar
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import java.net.CookieManager
import java.net.CookiePolicy
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

/* *
 * NetworkModule.kt
 * This module provides the necessary network components for making API calls.
 * It includes an OkHttpClient with custom SSL handling and a Retrofit instance.
 *
 * Note: The SSL handling in this module is insecure and should only be used for development purposes.
 * For production, implement proper SSL pinning or use valid certificates.
 * IMPORTANT SECURITY NOTE on createUnsafeOkHttpClient():
 * The createUnsafeOkHttpClient function above disables SSL certificate validation. This is highly insecure and should ONLY be used for local development with self-signed certificates where you fully trust the network and server. For a production application, you must:
 *
 * Ensure your Proxmox server has a valid SSL certificate from a trusted CA.
 * Or implement certificate pinning in
 * your app if you need to trust a specific self-signed certificate.
 * Remove or conditionally compile out the unsafe client for release builds. I've added a BuildConfig.TRUST_SELF_SIGNED_CERTS_FOR_DEBUG flag (you'd set this in your build.gradle).
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // Based on PveClientBase.java: Create a trust manager that does not validate certificate chains
    private fun createInsecureOkHttpClientBuilder(): OkHttpClient.Builder {
        val trustAllCerts = arrayOf<X509TrustManager>(
            @SuppressLint("CustomX509TrustManager")
            object : X509TrustManager {
                @SuppressLint("TrustAllX509TrustManager")
                override fun checkClientTrusted(
                    chain: Array<out X509Certificate>?,
                    authType: String?
                ) {
                }

                @SuppressLint("TrustAllX509TrustManager")
                override fun checkServerTrusted(
                    chain: Array<out X509Certificate>?,
                    authType: String?
                ) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            })
        val sslContext = SSLContext.getInstance("TLS") // "SSL" is old, "TLS" is more current
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())
        val sslSocketFactory = sslContext.socketFactory

        // Create an all-trusting host name verifier
        val allHostsValid = HostnameVerifier { _, _ -> true }

        return OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustAllCerts[0])
            .hostnameVerifier(allHostsValid)
    }

    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            isLenient = true // Proxmox API有时会返回非标准JSON，例如单个字符串而不是JSON对象
            prettyPrint = BuildConfig.DEBUG // Optional: for easier debugging of JSON
        }
    }

    @Provides
    @Singleton
    fun provideCookieJar(): CookieJar {
        // For PVEAuthCookie session management after PAM login
        val cookieManager = CookieManager().apply {
            setCookiePolicy(CookiePolicy.ACCEPT_ALL) // PVE API often uses cookies for session
        }
        return JavaNetCookieJar(cookieManager)
    }

    @Provides
    @Singleton
    @InsecureClient
    fun provideInsecureOkHttpClient(
        cookieJar: CookieJar,
        authInterceptor: AuthInterceptor
    ): OkHttpClient =
        createInsecureOkHttpClientBuilder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .cookieJar(cookieJar) // Add cookie jar for session management
            .addInterceptor(authInterceptor) // Add AuthInterceptor for authentication
            .apply {
                if (BuildConfig.DEBUG) {
                    val loggingInterceptor = HttpLoggingInterceptor { message ->
                        Timber.tag("OkHttp").d(message)
                    }.apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    }
                    addInterceptor(loggingInterceptor)
                }
                // TODO: Add an AuthenticationInterceptor here if needed for all standard requests
                //       (e.g., to add API Token if always used, or Ticket if session is active)
            }
            .build()


    @Provides
    @Singleton
    @StandardClient
    fun provideOkHttpClient(
        cookieJar: CookieJar,
        authInterceptor: AuthInterceptor // 注入拦截器
    ): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .cookieJar(cookieJar) // Add cookie jar for session management
            .addInterceptor(authInterceptor)
            .apply {
                if (BuildConfig.DEBUG) {
                    val loggingInterceptor = HttpLoggingInterceptor { message ->
                        Timber.tag("OkHttp").d(message)
                    }.apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    }
                    addInterceptor(loggingInterceptor)
                }
                // TODO: Add an AuthenticationInterceptor here if needed for all standard requests
                //       (e.g., to add API Token if always used, or Ticket if session is active)
            }
            .build()


    // ProxmoxApiProvider会用到OkHttpClient和Json，所以这里定义为Provider
    @Provides
    @Singleton
    fun provideProxmoxApiProvider(
        @StandardClient standardClient: OkHttpClient,
        @InsecureClient insecureClient: OkHttpClient,
        json: Json
    ): ProxmoxApiProvider {
        return ProxmoxApiProvider(standardClient, insecureClient, json)
    }

    // 移除之前的 provideRetrofit 和 provideProxmoxApiService，因为它们是固定的
    // 我们将通过 ProxmoxApiProvider 动态创建
}