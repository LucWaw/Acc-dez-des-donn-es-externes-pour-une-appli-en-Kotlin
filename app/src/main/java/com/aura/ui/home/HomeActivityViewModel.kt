package com.aura.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aura.data.repository.Result
import com.aura.data.repository.UserRepository
import com.aura.domain.model.AccountResponseModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class HomeActivityViewModel @Inject constructor(private val userRepository: UserRepository) :
    ViewModel() {

    private val _homeBusinessState = MutableLiveData<HomeBusinessState>(HomeBusinessState())
    val homeBusinessState: LiveData<HomeBusinessState> get() = _homeBusinessState

    fun pushConnexionData(userName: String): Flow<Result<List<AccountResponseModel>>> {
        return userRepository.getUserAccounts(userName).onEach { result ->
            when (result) {
                is Result.Failure -> {
                    _homeBusinessState.value = _homeBusinessState.value?.copy(
                        errorMessage = result.message,
                        isViewLoading = false,
                        accounts = emptyList()
                    )
                }

                is Result.Loading -> {
                    _homeBusinessState.value = _homeBusinessState.value?.copy(
                        isViewLoading = true,
                        errorMessage = null
                    )
                }

                is Result.Success -> {
                    _homeBusinessState.value = _homeBusinessState.value?.copy(
                        accounts = result.value,
                        isViewLoading = false,
                        errorMessage = null
                    )
                }
            }
        }
    }
}