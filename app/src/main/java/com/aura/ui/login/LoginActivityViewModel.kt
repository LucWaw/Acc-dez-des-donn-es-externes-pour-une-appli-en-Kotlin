package com.aura.ui.login

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class LoginActivityViewModel @Inject constructor() : ViewModel() {

    private val _isLoginValid = MutableStateFlow(false)
    val isLoginValid: StateFlow<Boolean> get() = _isLoginValid

    fun validateLogin(userName: String, password: String) {
        _isLoginValid.value = userName.isNotEmpty() && password.isNotEmpty()
    }
}