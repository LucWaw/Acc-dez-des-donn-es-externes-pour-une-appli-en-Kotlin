package com.aura.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.data.repository.Result
import com.aura.data.repository.UserRepository
import com.aura.domain.model.GrantResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LoginActivityViewModel @Inject constructor(private val userRepository: UserRepository) :
    ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> get() = _uiState.asStateFlow()

    fun validateLogin(userName: String, password: String) {
        _uiState.update { currentState ->
            if (userName.isNotEmpty() && password.isNotEmpty()) {
                currentState.copy(isCheckReady = true)
            } else {
                currentState.copy(isCheckReady = false)
            }
        }
    }

    fun pushConnexionData(userName: String, password: String) {
        userRepository.callUserVerification(userName, password).onEach { result ->
            when (result) {
                is Result.Failure -> _uiState.update { currentState ->
                    currentState.copy(
                        login = GrantResponse(false),
                        isViewLoading = false,
                        errorMessage = result.message,
                        isCheckReady = true
                    )
                }

                Result.Loading -> _uiState.update { currentState ->
                    currentState.copy(
                        isViewLoading = true,
                        errorMessage = null, isCheckReady = false
                    )
                }

                is Result.Success -> _uiState.update { currentState ->
                    currentState.copy(
                        login = result.value,
                        isViewLoading = false,
                        errorMessage = null,
                        isCheckReady = false
                    )
                }
            }
        }.launchIn(viewModelScope)
    }
}

data class LoginUiState(
    val isCheckReady: Boolean = false,
    val login: GrantResponse = GrantResponse(false),
    val isViewLoading: Boolean = false,
    val errorMessage: String? = null
)