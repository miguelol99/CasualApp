package com.miguelol.casualapp.presentation.screens.myplans

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miguelol.casualapp.domain.model.Error
import com.miguelol.casualapp.domain.model.Plan
import com.miguelol.casualapp.domain.model.Success
import com.miguelol.casualapp.domain.usecases.auth.AuthUseCases
import com.miguelol.casualapp.domain.usecases.plans.PlanUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class MyPlansUiState(
    val plans: List<Plan> = emptyList(),
    var pageState: Int = 0,
    val isLoading: Boolean = false,
    var errorMessage: String? = null
)

sealed interface MyPlansEvents {
    data class OnTabClicked(val index: Int): MyPlansEvents
    object OnErrorMessageShown: MyPlansEvents
}

@HiltViewModel
class MyPlansViewModel @Inject constructor(
    authUseCases: AuthUseCases,
    planUseCases: PlanUseCases
) : ViewModel() {

    private val _myUid: String = authUseCases.getCurrentUser()?.uid!!

    private val _myPlans = planUseCases.getMyPlans(_myUid)
    private val _pageState = MutableStateFlow(0)
    //private val _error = MutableStateFlow<String>(null)

    var uiState: StateFlow<MyPlansUiState> = combine(_myPlans, _pageState){ resp, state ->
        when(resp) {
            is Error -> MyPlansUiState(isLoading = true, errorMessage = resp.e.message)
            is Success -> {
                val filtered = if (state == 0) resp.data.filter { it.host.uid == _myUid }
                    else resp.data.filter { it.host.uid != _myUid && it.participants.contains(_myUid) }
                MyPlansUiState(
                    plans = filtered,
                    pageState = state
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = MyPlansUiState(isLoading = true)
    )

    fun onEvent(event: MyPlansEvents){
        when(event){
            is MyPlansEvents.OnTabClicked -> _pageState.update { event.index }
            MyPlansEvents.OnErrorMessageShown -> uiState.value.errorMessage = null
        }
    }

}