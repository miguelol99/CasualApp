package com.miguelol.casualapp.presentation.screens.searchusers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.model.Success
import com.miguelol.casualapp.domain.model.Error
import com.miguelol.casualapp.domain.model.User
import com.miguelol.casualapp.domain.usecases.auth.AuthUseCases
import com.miguelol.casualapp.domain.usecases.users.UserUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class SearchUsersUiState(
    val myUid: String = "",
    val searchText: String = "",
    val users: List<User> = emptyList(),
    val thereAreNoMatches: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed interface SearchEvents {
    data class OnSearchTextInput(val input: String) : SearchEvents
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchUsersViewModel @Inject constructor(
    authUseCases: AuthUseCases,
    private val userUseCases: UserUseCases,
) : ViewModel() {

    private val _myUid = authUseCases.getCurrentUser()!!.uid

    private val _searchText = MutableStateFlow("")
    private val _isSearching = MutableStateFlow(false)
    private val _users = getUsersFlow()


    val uiState: StateFlow<SearchUsersUiState> = combine(_searchText, _users)
    { text, resp ->
        when(resp) {
            is Error ->
                SearchUsersUiState(
                    searchText = text,
                    errorMessage = resp.e.message
                )
            is Success ->
                SearchUsersUiState(
                    myUid = _myUid,
                    searchText = text,
                    users = resp.data,
                    isLoading = _isSearching.value,
                    thereAreNoMatches = text.isNotBlank() && resp.data.isEmpty() && !_isSearching.value
                )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SearchUsersUiState()
    )

    private fun getUsersFlow() =
        _searchText.debounce(300L)
            .onEach { _isSearching.update { true } }
            .flatMapLatest { userUseCases.searchUsers(it) }
            .onEach { _isSearching.update { false } }
            .onStart { emit(Response.Success(emptyList())) }


    fun onEvent(event: SearchEvents) {
        when (event) {
            is SearchEvents.OnSearchTextInput -> onSearchTextInput(event.input)
        }
    }

    private fun onSearchTextInput(input: String) {
        _searchText.value = input
    }
}

