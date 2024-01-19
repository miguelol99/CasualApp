package com.miguelol.casualapp.presentation.screens.planprofile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miguelol.casualapp.domain.model.Plan
import com.miguelol.casualapp.domain.model.RequestState
import com.miguelol.casualapp.domain.model.Response.Error
import com.miguelol.casualapp.domain.model.Response.Success
import com.miguelol.casualapp.domain.model.UserPreview
import com.miguelol.casualapp.domain.usecases.PlanRequestUseCases
import com.miguelol.casualapp.domain.usecases.auth.AuthUseCases
import com.miguelol.casualapp.domain.usecases.plans.AddParticipant
import com.miguelol.casualapp.domain.usecases.plans.PlanUseCases
import com.miguelol.casualapp.presentation.navigation.DestinationArgs.PLAN_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlanProfileUiState(
    val plan: Plan = Plan(),
    val participants: List<UserPreview> = emptyList(),
    val requestState: RequestState = RequestState.NOT_SENT,
    var fromChat: Boolean = false,
    val myUid: String = "",
    val iAmTheHost: Boolean = false,
    var iAmAParticipant: Boolean = false,
    var planIsDeleted: Boolean = false,
    var isLoading: Boolean = false,
    var errorMessage: String? = null
)

sealed interface PlanProfileEvents {
    object OnJoinButtonPressed: PlanProfileEvents
    data class OnDeleteParticipant(val uid: String): PlanProfileEvents
    object OnErrorMessageShown : PlanProfileEvents
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PlanProfileViewModel @Inject constructor(
    authUseCases: AuthUseCases,
    private val planUseCases: PlanUseCases,
    private val planRequestUseCases: PlanRequestUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val myUid = authUseCases.getCurrentUser()!!.uid
    private val planId: String = savedStateHandle[PLAN_ID]!!
    private val fromChat: Boolean = savedStateHandle["fromChat"]!!

    private val _plan = planUseCases.getPlan(planId)
    private val _participants = planUseCases.getParticipants(planId)
    private val _errorMessage = MutableStateFlow<String?>(null)
    private val _isLoading = MutableStateFlow(false)
    private val _requestState = _plan.flatMapLatest { resp ->
        when(resp) {
            is Error -> flowOf(resp)
            is Success -> {
                if (resp.data != null) {
                    val hostUid = resp.data.host.uid
                    planRequestUseCases.getRequestState(myUid, hostUid, planId)
                } else
                    flowOf(Success(RequestState.PENDING))
            }
        }
    }

    private var _lastRequestState: RequestState? = null

    val uiState = combine(_plan, _participants, _requestState, _isLoading, _errorMessage)
    { plan, participants, state, isLoading, error ->
        when {
            plan is Error -> PlanProfileUiState(isLoading = true, errorMessage = plan.e.message)
            participants is Error -> PlanProfileUiState(isLoading = true, errorMessage = participants.e.message)
            state is Error -> PlanProfileUiState(isLoading = true, errorMessage = state.e.message)
            else -> {
                PlanProfileUiState(
                    plan = (plan as Success).data ?: Plan(),
                    participants = (participants as Success).data,
                    requestState = (state as Success).data,
                    myUid = myUid,
                    iAmTheHost = plan.data?.host?.uid == myUid,
                    iAmAParticipant = plan.data?.participants?.contains(myUid) ?: false,
                    planIsDeleted = plan.data == null,
                    fromChat = fromChat,
                    isLoading = isLoading || plan.data == null || (fromChat && !plan.data.participants.contains(myUid)),
                    errorMessage = error
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = PlanProfileUiState(isLoading = true)
    )

    fun onEvent(event: PlanProfileEvents) {
        when (event) {
            is PlanProfileEvents.OnJoinButtonPressed -> onJoinButtonPressed()
            is PlanProfileEvents.OnErrorMessageShown -> _errorMessage.update { null }
            is PlanProfileEvents.OnDeleteParticipant -> deleteParticipant(event.uid)
        }
    }

    private fun deleteParticipant(uid: String) = viewModelScope.launch {
        planUseCases.deleteParticipant(planId = planId, uid = uid)
    }

    private fun onJoinButtonPressed() = viewModelScope.launch {
        val hostUid = uiState.value.plan.host.uid
        if (uiState.value.iAmTheHost) {
            _isLoading.update { true }
            when (val resp = planUseCases.deletePlan(planId)){
                is Error -> {
                    _isLoading.update { false }
                    _errorMessage.update { resp.e.message }
                }
                is Success -> Unit
            }
        } else {
            val resp = when(uiState.value.requestState) {
                RequestState.ACCEPTED -> planUseCases.deleteParticipant(planId = planId, uid = myUid)
                RequestState.NOT_SENT -> planRequestUseCases.createRequest(fromUid = myUid, toUid = hostUid, planId = planId)
                RequestState.PENDING -> planRequestUseCases.declineRequest(toUid = hostUid, fromUid = myUid, planId =  planId)
            }
            when (resp) {
                is Error -> _errorMessage.update { resp.e.message }
                is Success -> { /*TODO*/}
            }
        }
    }
}