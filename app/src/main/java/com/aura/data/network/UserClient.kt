package com.aura.data.network

import com.aura.data.call.UserInfo
import com.aura.data.response.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserClient {
    @POST("/login")
    suspend fun pushConnexionRequest(
        @Body userInfo: UserInfo
    ): Response<LoginResponse>
}