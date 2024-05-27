package com.carlyu.chat.repository

import com.carlyu.chat.models.ExampleDataModel
import com.carlyu.chat.service.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getData(): List<ExampleDataModel> {
        val response = apiService.getData()
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            throw Exception("Error: ${response.code()}")
        }
    }
}
