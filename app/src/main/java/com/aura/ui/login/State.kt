package com.aura.ui.login

import com.aura.domain.model.GrantResponse


data class LoginBusinessState(
    val loginUIState: LoginUIState = LoginUIState(false),
    val login: GrantResponse = GrantResponse(false),//Logic business
    val isViewLoading: Boolean = false,//Logic business
    val errorMessage: String? = null//Logic business
)

data class LoginUIState(
    internal val isCheckReady: Boolean
)