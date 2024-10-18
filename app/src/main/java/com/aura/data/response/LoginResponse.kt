package com.aura.data.response


import com.aura.domain.model.GrantResponseModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginResponse(
    @Json(name = "granted")
    val granted: Boolean
) {
    fun toDomainModel(): GrantResponseModel {
        return GrantResponseModel(granted)
    }
}