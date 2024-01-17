package com.miguelol.casualapp.presentation.screens.userprofile

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miguelol.casualapp.domain.model.RequestState
import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.model.Success
import com.miguelol.casualapp.domain.model.Error
import com.miguelol.casualapp.domain.model.User
import com.miguelol.casualapp.domain.usecases.FriendRequestUseCases
import com.miguelol.casualapp.domain.usecases.FriendUseCases
import com.miguelol.casualapp.domain.usecases.auth.AuthUseCases
import com.miguelol.casualapp.domain.usecases.UserUseCases
import com.miguelol.casualapp.presentation.navigation.DestinationArgs.UID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class UserProfileUiState(
    val friendState: RequestState = RequestState.PENDING,
    val user: User = User(),
    val isLoading: Boolean = false,
    var errorMessage: String? = null
)

sealed interface UserProfileEvents {
    data class OnFollowButtonPressed(val friendState: RequestState): UserProfileEvents
    object OnErrorMessageShown : UserProfileEvents
}

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    authUseCases: AuthUseCases,
    userUseCases: UserUseCases,
    private val friendRequestUseCases: FriendRequestUseCases,
    private val friendUseCases: FriendUseCases
) : ViewModel() {

    private val _myUid: String = authUseCases.getCurrentUser()?.uid!!
    private val _profileUid: String = savedStateHandle[UID]!!

    private val _user = userUseCases.getUser(_profileUid)
    private val _friendState = friendRequestUseCases.getFriendState(_myUid, _profileUid)
    private val _updateResponse = MutableStateFlow<Response<Unit>?>(null)

    val uiState = combine(_user, _friendState, _updateResponse) { user, friendState, update ->

        when {
            user is Error ->
                UserProfileUiState(errorMessage = user.e.message, isLoading = true)
            friendState is Error ->
                UserProfileUiState(errorMessage = friendState.e.message, isLoading = true)
            else -> {
                val userData = (user as Success).data
                UserProfileUiState(
                    errorMessage = if (update is Error) update.e.message else null,
                    friendState = (friendState as Success).data,
                    user = userData.copy(age = calculateAge(userData.age))
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = UserProfileUiState(isLoading = true)
    )

    fun onEvent(event: UserProfileEvents) {
        when(event) {
            is UserProfileEvents.OnFollowButtonPressed -> onFollowButtonPressed(event.friendState)
            is UserProfileEvents.OnErrorMessageShown -> _updateResponse.update { null }
        }
    }

    private fun onFollowButtonPressed(friendState: RequestState) {
        Log.d("EYY", "BUTTON PRESSED")
        viewModelScope.launch {
            _updateResponse.value = when (friendState) {
                RequestState.ACCEPTED -> friendUseCases.deleteFriend(_myUid, _profileUid)
                RequestState.PENDING -> friendRequestUseCases.declineRequest(_myUid, _profileUid)
                RequestState.NOT_SENT ->
                    friendRequestUseCases.createRequest(_myUid, _profileUid)
            }
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