package com.aura.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.data.repository.Result
import com.aura.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HomeActivityViewModel @Inject constructor(private val userRepository: UserRepository) :
    ViewModel() {

    private val _uiBusinessState = MutableStateFlow(BusinessState())
    val uiBusinessState: StateFlow<BusinessState> get() = _uiBusinessState.asStateFlow()

    fun pushConnexionData(userName: String) {
        userRepository.getUserAccounts(userName).onEach { result ->
            when (result) {
                is Result.Failure -> _uiBusinessState.update { currentState ->
                    currentState.copy(
                        errorMessage = result.message
                    )
                }

                Result.Loading -> _uiBusinessState.update { currentState ->
                    currentState.copy(
                        isViewLoading = true,
                        errorMessage = null
                    )
                }

                is Result.Success -> {
                    _uiBusinessState.update { currentState ->
                        currentState.copy(
                            accounts = result.value,
                            errorMessage = null
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }
}