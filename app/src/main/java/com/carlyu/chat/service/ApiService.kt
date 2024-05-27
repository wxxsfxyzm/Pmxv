package com.carlyu.chat.service

import com.carlyu.chat.models.ExampleDataModel
import retrofit2.Response

import retrofit2.http.GET

interface ApiService {
    @GET("endpoint")
    suspend fun getData(): Response<List<ExampleDataModel>>
}