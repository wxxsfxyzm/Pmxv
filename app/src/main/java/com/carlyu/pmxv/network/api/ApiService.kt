package com.carlyu.pmxv.network.api

import com.carlyu.pmxv.network.response.LoginResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Url
import javax.inject.Singleton

@Singleton
interface ApiService {
    // 其他函数...

    @FormUrlEncoded
    @POST
    suspend fun login(
        @Url uri: String,
        @Field(("realm")) realm: String,
        @Field("username") username: String,
        @Field("password") password: String
    ): LoginResponse
}