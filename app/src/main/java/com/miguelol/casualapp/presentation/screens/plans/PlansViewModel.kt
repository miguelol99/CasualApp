package com.miguelol.casualapp.presentation.screens.plans

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miguelol.casualapp.domain.model.Plan
import com.miguelol.casualapp.domain.model.Success
import com.miguelol.casualapp.domain.model.Error
import com.miguelol.casualapp.domain.usecases.auth.AuthUseCases
import com.miguelol.casualapp.domain.usecases.plans.PlanUseCases
import com.miguelol.casualapp.utils.Constants.ALL
import com.miguelol.casualapp.utils.Constants.PRIVATE
import com.miguelol.casualapp.utils.Constants.TYPE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class PlansUiState(
    val plans: List<Plan> = emptyList(),
    val isLoading: Boolean = false,
    val topBarLabel: String = "All plans",
    var errorMessage: String? = null
)

sealed interface PlansEvents {
    object OnPublicFilter: PlansEvents
    object OnPrivateFilter: PlansEvents
    object OnErrorMessageShown: PlansEvents
}

@HiltViewModel
class PlansViewModel @Inject constructor(
    authUseCases: AuthUseCases,
    private val planUseCases: PlanUseCases,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val uid: String = authUseCases.getCurrentUser()?.uid!!

    private val _plans = planUseCases.getPlans(uid) //TODO ESTO SOLO TRAE PLANES PUBLICOS
    private val _filterType = savedStateHandle.getStateFlow(TYPE, ALL)

    var uiState = combine(_plans, _filterType) { plans, type ->

        when(plans) {
            is Error -> PlansUiState(isLoading = true, errorMessage = plans.e.message)
            is Success -> {
                PlansUiState(
                    plans = planUseCases.filterPlans(plans.data, type),
                    topBarLabel = when(type) {
                        ALL -> "All plans"
                        else -> "Private plans"
                    }
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = PlansUiState(isLoading = true)
    )

    fun onEvent(event: PlansEvents) {
        when(event) {
            PlansEvents.OnPrivateFilter -> setFiltering(PRIVATE)
            PlansEvents.OnPublicFilter -> setFiltering(ALL)
            PlansEvents.OnErrorMessageShown -> uiState.value.errorMessage = null
        }
    }

    private fun setFiltering(requestType: String) {
        savedStateHandle[TYPE] = requestType
    }


}