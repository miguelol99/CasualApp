package com.miguelol.casualapp.presentation.screens.myprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miguelol.casualapp.domain.model.Error
import com.miguelol.casualapp.domain.model.FriendState
import com.miguelol.casualapp.domain.model.Success
import com.miguelol.casualapp.domain.model.User
import com.miguelol.casualapp.domain.usecases.UserUseCases
import com.miguelol.casualapp.domain.usecases.auth.AuthUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class MyProfileUiState(
    val user: User = User(),
    val isLoading: Boolean = false,
    var errorMessage: String? = null
)

sealed interface MyProfileEvents {
    object OnErrorMessageShown : MyProfileEvents
}

@HiltViewModel
class MyProfileViewModel @Inject constructor(
    authUseCases: AuthUseCases,
    val userUseCases: UserUseCases,
) : ViewModel() {

    private val uid: String = authUseCases.getCurrentUser()?.uid!!

    private val _user = userUseCases.getUser(uid)

    val uiState: StateFlow<MyProfileUiState> = _user.map { resp ->
        when(resp) {
            is Error ->
                MyProfileUiState(
                    isLoading = true,
                    errorMessage = resp.e.message
                )
            is Success ->
                MyProfileUiState(
                    user = resp.data.copy(age = calculateAge(resp.data.age))
                )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = MyProfileUiState(isLoading = true)
    )

    fun onEvent(event: MyProfileEvents) {
        when(event){
            is MyProfileEvents.OnErrorMessageShown -> uiState.value.errorMessage = null
            else -> Unit
        }
    }

    private fun calculateAge(birthString: String): String {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val birthDate = LocalDate.parse(birthString, formatter)
        val currentDate = LocalDate.now()
        val period = Period.between(birthDate, currentDate)
        return period.years.toString()
    }
}