package com.aura.ui.transfer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aura.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class TransferActivityViewModel @Inject constructor(private val userRepository: UserRepository) :
    ViewModel() {

    // Dans le ViewModel, remplacer le StateFlow par LiveData
    private val _uiBusinessState = MutableLiveData(TransferBusinessState())
    val uiBusinessState: LiveData<TransferBusinessState> get() = _uiBusinessState


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

    /*fun pushTransferData(sender: String, recipient : String, amount: String): Flow<Result<GrantResponseModel>> {
        return userRepository.callUserVerification(userName, password).onEach { result ->
            when (result) {
                is Result.Loading -> {
                    _uiBusinessState.value = _uiBusinessState.value?.copy(
                        isViewLoading = true,
                        errorMessage = null,
                        isCheckReadyByApiCall = false
                    )
                }
                is Result.Failure -> {
                    _uiBusinessState.value = _uiBusinessState.value?.copy(
                        login = GrantResponseModel(false),
                        isViewLoading = false,
                        errorMessage = result.message,
                        isCheckReadyByApiCall = true
                    )
                }
                is Result.Success -> {
                    _uiBusinessState.value = _uiBusinessState.value?.copy(
                        login = result.value,
                        isViewLoading = false,
                        errorMessage = null,
                        isCheckReadyByApiCall = true
                    )
                }
            }
        }
    }*/



    /*private fun apiSucessButBadData(){
        _uiBusinessState.update { currentState ->
            currentState.copy(
                isCheckReadyByApiCall = true
            )
        }
    }

    private fun apiSuccessAndDataVerified(){
        _uiBusinessState.update { currentState ->
            currentState.copy(
                isCheckReadyByApiCall = false
            )
        }
    }*/
}

