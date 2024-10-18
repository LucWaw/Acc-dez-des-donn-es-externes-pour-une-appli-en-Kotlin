package com.aura.ui.login

import com.aura.domain.model.GrantResponseModel


data class LoginBusinessState(
    val isCheckReadyByApiCall: Boolean = true,
    val login: GrantResponseModel = GrantResponseModel(false),//Logic business
    val isViewLoading: Boolean = false,//Logic business
    val errorMessage: String? = null//Logic business)
)

data class LoginUIState(
    internal val isCheckReady: Boolean = false
)