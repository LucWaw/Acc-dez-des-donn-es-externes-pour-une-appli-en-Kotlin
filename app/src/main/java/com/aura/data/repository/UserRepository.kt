package com.aura.data.repository

import com.aura.data.call.UserInfo
import com.aura.data.network.UserClient
import com.aura.data.response.toDomainModel
import com.aura.domain.model.AccountResponseModel
import com.aura.domain.model.GrantResponseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class UserRepository(private val dataClient: UserClient) {
    fun callUserVerification(userName: String, password: String): Flow<Result<GrantResponseModel>> =
        flow {
            emit(Result.Loading)
            val result = dataClient.pushConnexionRequest(
                UserInfo(userName, password)
            )
            val model = result.body()?.toDomainModel() ?: throw Exception("Invalid data")
            emit(Result.Success(model))
        }.catch { error ->
            emit(Result.Failure(error.message))
        }.flowOn(Dispatchers.IO)


    fun getUserAccounts(userName: String): Flow<Result<List<AccountResponseModel>>> =
        flow {
            emit(Result.Loading)
            val result = dataClient.getAccounts(
                userId = userName
            )
            val model = toDomainModel(result.body()?: throw Exception("Invalid data"))
            emit(Result.Success(model))
        }.catch { error ->
            emit(Result.Failure(error.message))
        }.flowOn(Dispatchers.IO)
}