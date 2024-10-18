package com.aura.data.response


import com.aura.domain.model.AccountResponseModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

data class AccountsResponse (
    @Json(name = "list")
    val accounts: List<AccountsResponseItem>,
) {
    @JsonClass(generateAdapter = true)
    data class AccountsResponseItem(
        @Json(name = "balance")
        val balance: Double,
        @Json(name = "id")
        val id: String,
        @Json(name = "main")
        val main: Boolean
    )

    fun toDomainModel(): List<AccountResponseModel> {
        return accounts.map { account ->
            AccountResponseModel(
                balance = account.balance,
                id = account.id,
                main = account.main
            )
        }
    }

}