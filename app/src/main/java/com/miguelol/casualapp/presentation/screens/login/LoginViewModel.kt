package com.miguelol.casualapp.presentation.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.model.Success
import com.miguelol.casualapp.domain.model.Error
import com.miguelol.casualapp.domain.usecases.auth.AuthUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val errorMessage: String? = null
)

sealed interface LoginEvents {
    object OnLogin: LoginEvents
    data class OnUsernameInput(val input: String): LoginEvents
    data class OnPasswordInput(val input: String): LoginEvents
    object OnErrorMessageShown: LoginEvents
}

@HiltViewModel
class LoginViewModel @Inject constructor(private val authUseCases: AuthUseCases): ViewModel() {

    private val _isLoggedIn= MutableStateFlow(false)
    private val _isLoading = MutableStateFlow(false)
    private val _error: MutableStateFlow<String?> = MutableStateFlow(null)
    private val _email = MutableStateFlow("mnsendino@gmail.com")
    private val _password = MutableStateFlow("123456")

    val uiState = combine(_isLoggedIn, _email, _password, _isLoading, _error)
    { isLoggedIn, email, password, isLoading, error ->
        LoginUiState(
            email = email,
            password = password,
            isLoading = (isLoggedIn || isLoading),
            isLoggedIn = isLoggedIn,
            errorMessage = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = LoginUiState()
    )

    fun onEvent(event: LoginEvents) {
        when(event) {
            is LoginEvents.OnLogin -> onLogin()
            is LoginEvents.OnUsernameInput -> _email.value = event.input
            is LoginEvents.OnPasswordInput -> _password.value = event.input
            is LoginEvents.OnErrorMessageShown -> _error.update { null }
        }
    }
    private fun onLogin() {
        _isLoading.value = true
        viewModelScope.launch {
            when(val resp = authUseCases.logIn(_email.value, _password.value)){
                is Error -> _error.update{ resp.e.message}
                is Success -> _isLoggedIn.value = true
            }
            _isLoading.value = false
        }
    }
}