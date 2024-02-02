package com.miguelol.casualapp.presentation.screens.searchfriends

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.model.UserPreview
import com.miguelol.casualapp.domain.usecases.friends.FriendUseCases
import com.miguelol.casualapp.presentation.navigation.DestinationArgs.UID
import com.miguelol.casualapp.domain.usecases.auth.AuthUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class SearchFriendsUiState(
    val myUid: String = "",
    val searchText: String = "",
    val friends: List<UserPreview> = emptyList(),
    val thereAreNoMatches: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed interface SearchFriendsEvents {
    data class OnSearchTextInput(val input: String) : SearchFriendsEvents
}

@HiltViewModel
class SearchFriendsViewModel @Inject constructor(
    authUseCases: AuthUseCases,
    friendsUseCases: FriendUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _myUid = authUseCases.getCurrentUser()!!.uid

    private val _searchText = MutableStateFlow("")
    private val _friends = friendsUseCases.getFriends(savedStateHandle[UID]!!)

    val uiState = combine(_searchText, _friends) { text, resp ->

        when(resp) {
            is Response.Error ->
                SearchFriendsUiState(
                    searchText = text,
                    errorMessage = resp.e.message
                )
            is Response.Success -> {
                val friends = if (resp.data.isNotEmpty()) resp.data.filter{ it.matchesTerm(text) }
                    else emptyList()

                SearchFriendsUiState(
                    myUid = _myUid,
                    searchText = text,
                    friends = friends,
                    thereAreNoMatches = (text.isNotBlank() && friends.isEmpty())
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SearchFriendsUiState(isLoading = true)
    )

    fun onEvent(event: SearchFriendsEvents) {
        when (event) {
            is SearchFriendsEvents.OnSearchTextInput -> _searchText.value = event.input
        }
    }
}

