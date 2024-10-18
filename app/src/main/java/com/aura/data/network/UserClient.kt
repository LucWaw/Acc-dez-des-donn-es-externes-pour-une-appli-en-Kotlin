package com.aura.data.network

import com.aura.data.call.UserInfo
import com.aura.data.response.AccountsResponse
import com.aura.data.response.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserClient {
    @POST("/login")
    suspend fun pushConnexionRequest(
        @Body userInfo: UserInfo
    ): Response<LoginResponse>

    @GET("/accounts/{id}")
    suspend fun getAccounts(
        @Path("id") userId: String
    ):Response<AccountsResponse>
}