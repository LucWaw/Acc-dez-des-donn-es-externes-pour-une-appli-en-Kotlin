package com.aura.ui.transfer

import com.aura.domain.model.GrantResponseModel


data class TransferBusinessState(
    val isCheckReadyByApiCall: Boolean = true,
    val login: GrantResponseModel = GrantResponseModel(false),//Logic business
    val isViewLoading: Boolean = false,//Logic business
    val errorMessage: String? = null//Logic business)
)

data class TransferUIState(
    internal val isTransferReady: Boolean = false
)