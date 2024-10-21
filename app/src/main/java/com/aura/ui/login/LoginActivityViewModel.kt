package com.aura.ui.login

import androidx.lifecycle.ViewModel
import com.aura.data.repository.Result
import com.aura.data.repository.UserRepository
import com.aura.domain.model.GrantResponseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LoginActivityViewModel @Inject constructor(private val userRepository: UserRepository) :
    ViewModel() {

    private val _uiBusinessState = MutableStateFlow(LoginBusinessState())
    val uiBusinessState: StateFlow<LoginBusinessState> get() = _uiBusinessState.asStateFlow()


    private val _uiState = MutableStateFlow(LoginUIState())
    val uiState: StateFlow<LoginUIState> = _uiState.asStateFlow()


    /**
     * Bussiness rule to check if two connexion keys are not empty
     *
     * @param userName the identifier of the user
     * @param password the password Of the user
     */
    fun validateLogin(userName: String, password: String) {
        _uiState.update { currentState ->
            if (userName.isNotEmpty() && password.isNotEmpty()) {
                currentState.copy(isCheckReady = true)
            } else {
                currentState.copy(isCheckReady = false)
            }
        }
    }

    /**
     * Try to connect then update State with emit
     * Use copy instead of update because no race condition can append on a single call
     *
     * @param userName the identifier of the user
     * @param password the password Of the user
     */
    fun pushConnexionData(userName: String, password: String): Flow<Result<GrantResponseModel>> {
        return userRepository.callUserVerification(userName, password).onEach { result ->
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
                        login = GrantResponseModel(false),
                        isViewLoading = false,
                        errorMessage = result.message,
                        isCheckReadyByApiCall = true
                    )
                }
                is Result.Success -> {
                    _uiBusinessState.value = _uiBusinessState.value.copy(
                        login = result.value,
                        isViewLoading = false,
                        errorMessage = null,
                        isCheckReadyByApiCall = true
                    )
                }
            }
        }
    }
}

