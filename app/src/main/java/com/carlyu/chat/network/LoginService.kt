package com.carlyu.chat.network

import com.carlyu.chat.api.ApiService
import javax.inject.Inject

class LoginService @Inject constructor(private val apiService: ApiService) {
    suspend fun login(username: String, password: String): Result {
        val response = apiService.login(username, password)

        return if (response.code == 200) {
            Result.Success
        } else {
            Result.Failure(response.msg)
        }
    }
}

sealed class Result {
    data object Success : Result()
    data class Failure(val message: String) : Result()
}