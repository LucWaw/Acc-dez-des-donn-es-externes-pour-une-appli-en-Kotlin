package com.aura.ui.transfer

import androidx.lifecycle.ViewModel
import com.aura.data.repository.UserRepository
import com.aura.domain.model.TransferResponseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import com.aura.data.repository.Result

@HiltViewModel
class TransferActivityViewModel @Inject constructor(private val userRepository: UserRepository) :
    ViewModel() {

    private val _uiBusinessState = MutableStateFlow(TransferBusinessState())
    val uiBusinessState: StateFlow<TransferBusinessState> get() = _uiBusinessState.asStateFlow()


    private val _uiState = MutableStateFlow(TransferUIState())
    val uiState: StateFlow<TransferUIState> = _uiState.asStateFlow()



    fun validateLogin(recipient : String, amount: String) {
        _uiState.update { currentState ->
            if (recipient.isNotEmpty() && amount.isNotEmpty()) {
                currentState.copy(isTransferReady = true)
            } else {
                currentState.copy(isTransferReady = false)
            }
        }
    }

    /**
     * Try to transfer then update State with emit
     * Use copy instead of update because no race condition can append on a single call
     *
     * @param userName the identifier of the user
     * @param password the password Of the user
     */
    fun pushTransferData(sender: String, recipient : String, amount: Double): Flow<Result<TransferResponseModel>> {
        return userRepository.askForTransfer(sender, recipient, amount).onEach { result ->
            when (result) {
                is Result.Loading -> {
                    _uiBusinessState.value = _uiBusinessState.value.copy(
                        isViewLoading = true,
                        errorMessage = null,
                        isCheckReadyByApiCall = false
                    )
                }
                is Result.Failure -> {
                    _uiBusinessState.value = _uiBusinessState.value.copy(
                        transfer = TransferResponseModel(false),
                        isViewLoading = false,
                        errorMessage = result.message,
                        isCheckReadyByApiCall = true
                    )
                }
                is Result.Success -> {
                    _uiBusinessState.value = _uiBusinessState.value.copy(
                        transfer = result.value,
                        isViewLoading = false,
                        errorMessage = null,
                        isCheckReadyByApiCall = true
                    )
                }
            }
        }
    }
}

