package com.miguelol.casualapp.presentation.screens.requests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miguelol.casualapp.domain.model.Error
import com.miguelol.casualapp.domain.model.FriendRequest
import com.miguelol.casualapp.domain.model.RequestState
import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.model.Success
import com.miguelol.casualapp.domain.usecases.FriendRequestUseCases
import com.miguelol.casualapp.domain.usecases.auth.AuthUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RequestsUiState(
    val requests: List<FriendRequest> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed interface RequestsEvents {
    data class OnAccept(val request: FriendRequest) : RequestsEvents
    data class OnDecline(val request: FriendRequest) : RequestsEvents
}

@HiltViewModel
class RequestsViewModel @Inject constructor(
    authUseCases: AuthUseCases,
    private val friendRequestUseCases: FriendRequestUseCases
) : ViewModel() {

    private val _myUid = authUseCases.getCurrentUser()!!.uid

    private val _requests = friendRequestUseCases.getFriendRequests(_myUid)
    private val _updateResp = MutableStateFlow<Response<Unit>?>(null)

    val uiState: StateFlow<RequestsUiState> = combine(_requests, _updateResp)
    { request, update ->
        when {
            request is Error -> RequestsUiState(isLoading = true, errorMessage = request.e.message)
            update is Error -> RequestsUiState(errorMessage = update.e.message)
            else -> RequestsUiState(requests = (request as Success).data)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = RequestsUiState(isLoading = true)
    )

    fun onEvent(event: RequestsEvents) {
        when (event) {
            is RequestsEvents.OnAccept -> onAccept(event.request)
            is RequestsEvents.OnDecline -> onDecline(event.request)
        }
    }

    private fun onAccept(request: FriendRequest) = viewModelScope.launch {
        _updateResp.value = friendRequestUseCases.acceptRequest(request.fromUser.uid, _myUid)
    }

    private fun onDecline(request: FriendRequest) = viewModelScope.launch {
        _updateResp.value = friendRequestUseCases.declineRequest(request.fromUser.uid, _myUid)
    }
}