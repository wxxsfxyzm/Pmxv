package com.carlyu.pmxv.network

import com.carlyu.pmxv.network.api.ApiService
import javax.inject.Inject

class LoginService @Inject constructor(private val apiService: ApiService) {
    suspend fun login(uri: String, username: String, password: String): Result {
        val response = apiService.login(uri, "pam", username, password)

        return if (response.data != null) {
            Result.Success
        } else {
            Result.Failure(response.message ?: "Unknown error")
        }
    }
}

sealed class Result {
    data object Success : Result()
    data class Failure(val message: String) : Result()
}