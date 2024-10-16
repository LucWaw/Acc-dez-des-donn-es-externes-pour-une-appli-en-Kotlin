package com.aura.data.response


import com.aura.domain.model.GrantResponse
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginResponse(
    @Json(name = "granted")
    val granted: Boolean
) {
    fun toDomainModel(): GrantResponse {
        return GrantResponse(granted)
    }
}