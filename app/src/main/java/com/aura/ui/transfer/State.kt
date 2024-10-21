package com.aura.ui.transfer

import com.aura.domain.model.GrantResponseModel
import com.aura.domain.model.TransferResponseModel


data class TransferBusinessState(
    val isCheckReadyByApiCall: Boolean = true,
    val transfer: TransferResponseModel = TransferResponseModel(false),//Logic business
    val isViewLoading: Boolean = false,//Logic business
    val errorMessage: String? = null//Logic business)
)

data class TransferUIState(
    internal val isTransferReady: Boolean = false
)