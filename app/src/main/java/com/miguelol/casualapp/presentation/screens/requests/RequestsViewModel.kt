package com.miguelol.casualapp.presentation.screens.requests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miguelol.casualapp.domain.model.Error
import com.miguelol.casualapp.domain.model.FriendRequest
import com.miguelol.casualapp.domain.model.PlanRequest
import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.model.Success
import com.miguelol.casualapp.domain.usecases.friendRequests.FriendRequestUseCases
import com.miguelol.casualapp.domain.usecases.planRequests.PlanRequestUseCases
import com.miguelol.casualapp.domain.usecases.auth.AuthUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RequestsUiState(
    val requests: List<CombinedRequest> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed interface RequestsEvents {
    data class OnAccept(val request: CombinedRequest) : RequestsEvents
    data class OnDecline(val request: CombinedRequest) : RequestsEvents
}

@HiltViewModel
class RequestsViewModel @Inject constructor(
    authUseCases: AuthUseCases,
    private val friendRequestUseCases: FriendRequestUseCases,
    private val planRequestUseCases: PlanRequestUseCases
) : ViewModel() {

    private val _myUid = authUseCases.getCurrentUser()!!.uid

    private val _friendReq = friendRequestUseCases.getFriendRequests(_myUid)
    private val _planReq = planRequestUseCases.getRequests(_myUid)
    private val _updateResp = MutableStateFlow<Response<Unit>?>(null)

    val uiState: StateFlow<RequestsUiState> =
        combine(_friendReq, _planReq, _updateResp) { friendReq, planReq, update ->
            when {
                friendReq is Error -> RequestsUiState(
                    isLoading = true,
                    errorMessage = friendReq.e.message
                )

                planReq is Error -> RequestsUiState(
                    isLoading = true,
                    errorMessage = planReq.e.message
                )

                update is Error -> RequestsUiState(errorMessage = update.e.message)
                else -> {
                    val friends = (friendReq as Success).data
                    val plans = (planReq as Success).data
                    val combined = (friends.map { CombinedRequest.Friend(it) }
                            + plans.map { CombinedRequest.Plan(it) }
                        ).sortedBy {
                            when (it) {
                                is CombinedRequest.Friend -> it.friendRequest.timestamp
                                is CombinedRequest.Plan -> it.planRequest.timestamp
                            }
                    }
                    RequestsUiState(requests = combined)
                }
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

    private fun onAccept(request: CombinedRequest) = viewModelScope.launch {
        when(request) {
            is CombinedRequest.Friend -> {
                val fromUid = request.friendRequest.fromUser.uid
                _updateResp.value =
                    friendRequestUseCases.acceptRequest(fromUid = fromUid, toUid = _myUid)
            }
            is CombinedRequest.Plan -> {
                _updateResp.value = planRequestUseCases.acceptRequest(toUid = _myUid, requestId = request.id)
            }
        }
    }

    private fun onDecline(request: CombinedRequest) = viewModelScope.launch {
        when(request) {
            is CombinedRequest.Friend -> {
                val fromUid = request.friendRequest.fromUser.uid
                _updateResp.value =
                    friendRequestUseCases.declineRequest(fromUid = fromUid, toUid = _myUid)
            }
            is CombinedRequest.Plan -> {
                val fromUid = request.planRequest.fromUser.uid
                val planId = request.planRequest.plan.id
                _updateResp.value = planRequestUseCases.declineRequest(toUid = _myUid, fromUid = fromUid, planId = planId)
            }
        }
    }
}

sealed class CombinedRequest(val id: String) {
    data class Friend(val friendRequest: FriendRequest) : CombinedRequest(friendRequest.fromUser.uid)
    data class Plan(val planRequest: PlanRequest) : CombinedRequest(planRequest.id)
}