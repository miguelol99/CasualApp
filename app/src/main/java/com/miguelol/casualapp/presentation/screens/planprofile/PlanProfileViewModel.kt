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
import com.miguelol.casualapp.domain.usecases.plans.PlanUseCases
import com.miguelol.casualapp.presentation.navigation.DestinationArgs.PLAN_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlanProfileUiState(
    val plan: Plan = Plan(),
    val participants: List<UserPreview> = emptyList(),
    val requestState: RequestState = RequestState.NOT_SENT,
    val myUid: String = "",
    val isLoading: Boolean = false,
    var errorMessage: String? = null
)

sealed interface PlanProfileEvents {
    object OnJoinButtonPressed: PlanProfileEvents
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

    private val _plan = planUseCases.getPlan(planId)
    private val _participants = planUseCases.getParticipants(planId)
    private val _requestState = _plan.flatMapLatest { resp ->
        when(resp) {
            is Error -> flowOf(resp)
            is Success -> {
                val hostUid = resp.data.host.uid
                planRequestUseCases.getRequestState(myUid, hostUid, planId)
            }
        }
    }

    val uiState = combine(_plan, _participants, _requestState)
    { plan, part, state ->
        when {
            plan is Error -> PlanProfileUiState(isLoading = true, errorMessage = plan.e.message)
            part is Error -> PlanProfileUiState(isLoading = true, errorMessage = part.e.message)
            state is Error -> PlanProfileUiState(isLoading = true, errorMessage = state.e.message)
            else ->
                PlanProfileUiState(
                    plan = (plan as Success).data,
                    participants = (part as Success).data,
                    requestState = (state as Success).data,
                    myUid = myUid
                )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = PlanProfileUiState(isLoading = true)
    )

    fun onEvent(event: PlanProfileEvents) {
        when (event) {
            is PlanProfileEvents.OnJoinButtonPressed -> onJoinButtonPressed()
            is PlanProfileEvents.OnErrorMessageShown -> uiState.value.errorMessage = null
        }
    }

    private fun onJoinButtonPressed() = viewModelScope.launch {
        val hostUid = uiState.value.plan.host.uid
        when(uiState.value.requestState) {
            RequestState.ACCEPTED -> planUseCases.deleteParticipant(planId = planId, uid = myUid)
            RequestState.NOT_SENT -> planRequestUseCases.createRequest(fromUid = myUid, toUid = hostUid, planId = planId)
            RequestState.PENDING -> planRequestUseCases.declineRequest(toUid = hostUid, fromUid = myUid, planId =  planId)
        }
    }
}