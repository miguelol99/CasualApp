package com.miguelol.casualapp.presentation.screens.plans

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miguelol.casualapp.domain.model.PlanPreview
import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.model.Success
import com.miguelol.casualapp.domain.model.Error
import com.miguelol.casualapp.domain.repositories.PlansPreview
import com.miguelol.casualapp.domain.usecases.auth.AuthUseCases
import com.miguelol.casualapp.domain.usecases.plans.PlanUseCases
import com.miguelol.casualapp.utils.Constants.ALL
import com.miguelol.casualapp.utils.Constants.PRIVATE
import com.miguelol.casualapp.utils.Constants.TYPE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class PlansUiState(
    val plans: List<PlanPreview> = emptyList(),
    val isLoading: Boolean = false,
    val topBarLabel: String = "All plans",
    val errorMessage: String? = null
)

sealed interface PlansEvents {
    object OnAllFilter: PlansEvents
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

    private val _filterType = savedStateHandle.getStateFlow(TYPE, ALL)
    private val _filteredPlans = getFilteredPlansFlow()
    private val _error = MutableStateFlow<String?>(null)

    private fun getFilteredPlansFlow(): Flow<List<PlanPreview>?> =
        combine(getAllPlansFlow(), _filterType) { plans, type ->
            planUseCases.filterPlans(plans, type)
        }

    private fun getAllPlansFlow(): Flow<List<PlanPreview>?> =
        planUseCases.getAllPlans(uid)
            .onEach { resp -> if (resp is Error) _error.update { resp.e.message } }
            .filter { it is Success }
            .map { (it as Success).data }


    var uiState = combine(_filteredPlans, _filterType, _error) { plans, type, error ->
        PlansUiState(
            plans = plans ?: emptyList(),
            topBarLabel =  when(type) {
                ALL -> "All plans"
                else -> "Private plans"
            },
            isLoading = false,
            errorMessage = error
        )

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = PlansUiState(isLoading = true)
    )

    fun onEvent(event: PlansEvents) {
        when(event) {
            PlansEvents.OnPrivateFilter -> setFiltering(PRIVATE)
            PlansEvents.OnAllFilter -> setFiltering(ALL)
            PlansEvents.OnErrorMessageShown -> _error.update { null }
        }
    }

    private fun setFiltering(requestType: String) {
        savedStateHandle[TYPE] = requestType
    }


}