package com.aura.ui.home

import com.aura.domain.model.AccountResponseModel

data class HomeBusinessState(
    val accounts: List<AccountResponseModel> = emptyList(),//Logic business
    val isViewLoading: Boolean = false,//Logic business
    val errorMessage: String? = null//Logic business
)
