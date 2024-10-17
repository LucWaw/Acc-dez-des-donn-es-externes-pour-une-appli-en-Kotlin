package com.aura.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.R
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

    private val _uiState = MutableStateFlow(LoginBusinessState())
    val uiState: StateFlow<LoginBusinessState> get() = _uiState.asStateFlow()

    var errorLabel: MutableLiveData<Int> = MutableLiveData<Int>()

    fun validateLogin(userName: String, password: String) {
        _uiState.update { currentState ->
            if (userName.isNotEmpty() && password.isNotEmpty()) {
                currentState.copy(loginUIState = LoginUIState(true))
            } else {
                currentState.copy(loginUIState = LoginUIState(false))
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
                        loginUIState = LoginUIState(true)
                    )
                }

                Result.Loading -> _uiState.update { currentState ->
                    currentState.copy(
                        isViewLoading = true,
                        errorMessage = null,
                        loginUIState = LoginUIState(false)
                    )
                }

                is Result.Success -> {
                    if (result.value.isGranted) {
                        apiSuccessAndDataVerified()
                    } else {
                        apiSucessButBadData()
                    }
                    errorLabel.value = R.string.invalid_credentials

                    _uiState.update { currentState ->
                        currentState.copy(
                            login = result.value,
                            isViewLoading = false,
                            errorMessage = "Not translated placeholder"
                            )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun apiSucessButBadData(){
        _uiState.update { currentState ->
            currentState.copy(
                loginUIState = LoginUIState(true)
            )
        }
    }

    private fun apiSuccessAndDataVerified(){
        _uiState.update { currentState ->
            currentState.copy(
                loginUIState = LoginUIState(false)
            )
        }
    }
}

