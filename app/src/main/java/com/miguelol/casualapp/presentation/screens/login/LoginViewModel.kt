package com.miguelol.casualapp.presentation.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miguelol.casualapp.domain.model.Error
import com.miguelol.casualapp.domain.model.Success
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
    val errorMessage: String? = null,
    val signedUp: Boolean = false
)

sealed interface LoginEvents {
    object OnLogin: LoginEvents
    object OnSignUp: LoginEvents
    data class OnUsernameInput(val input: String): LoginEvents
    data class OnPasswordInput(val input: String): LoginEvents
    object OnErrorMessageShown: LoginEvents
}

@HiltViewModel
class LoginViewModel @Inject constructor(private val authUseCases: AuthUseCases): ViewModel() {

    private val _isLoggedIn= MutableStateFlow(false)
    private val _signedUp = MutableStateFlow(false)
    private val _isLoading = MutableStateFlow(false)
    private val _error: MutableStateFlow<String?> = MutableStateFlow(null)
    private val _email = MutableStateFlow("")
    private val _password = MutableStateFlow("")

    val uiState = combine(_isLoggedIn, _signedUp, _email, _password, _isLoading, _error)
    { array ->
        val isLoggedIn = array[0] as Boolean
        val signedUp = array[1] as Boolean
        val email = array[2] as String
        val password = array[3] as String
        val isLoading = array[4] as Boolean
        val error = array[5] as String?

        LoginUiState(
            email = email,
            password = password,
            isLoading = (isLoggedIn || isLoading),
            isLoggedIn = isLoggedIn,
            signedUp = signedUp,
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
            is LoginEvents.OnSignUp -> {}//onRegister()
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
    private fun onRegister() {
        _isLoading.value = true
        viewModelScope.launch {
            when(val resp = authUseCases.signUp(_email.value, _password.value)){
                is Error -> _error.update{ resp.e.message}
                is Success -> _signedUp.update { true}
            }
            _isLoading.value = false
        }
    }

}