package com.miguelol.casualapp.presentation.screens.myplans

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miguelol.casualapp.domain.model.Error
import com.miguelol.casualapp.domain.model.PlanPreview
import com.miguelol.casualapp.domain.model.Success
import com.miguelol.casualapp.domain.usecases.auth.AuthUseCases
import com.miguelol.casualapp.domain.usecases.plans.PlanUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class MyPlansUiState(
    val plans: List<PlanPreview> = emptyList(),
    val isLoading: Boolean = false,
    var errorMessage: String? = null
)

sealed interface MyPlansEvents {
    object OnErrorMessageShown: MyPlansEvents
}

@HiltViewModel
class MyPlansViewModel @Inject constructor(
    authUseCases: AuthUseCases,
    planUseCases: PlanUseCases
) : ViewModel() {

    private val uid: String = authUseCases.getCurrentUser()?.uid!!

    private val _myPlans = planUseCases.getMyPlans(uid)
    //private val _error = MutableStateFlow<String>(null)

    var uiState: StateFlow<MyPlansUiState> = _myPlans.map{ resp ->
        when(resp) {
            is Error -> MyPlansUiState(isLoading = true, errorMessage = resp.e.message)
            is Success -> MyPlansUiState(plans = resp.data)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = MyPlansUiState(isLoading = true)
    )

    fun onEvent(event: MyPlansEvents){
        when(event){
            MyPlansEvents.OnErrorMessageShown -> uiState.value.errorMessage = null
        }
    }

}