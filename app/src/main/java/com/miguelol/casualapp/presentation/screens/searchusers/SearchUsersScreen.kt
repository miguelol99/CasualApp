package com.miguelol.casualapp.presentation.screens.searchusers

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.miguelol.casualapp.R
import com.miguelol.casualapp.domain.model.User
import com.miguelol.casualapp.presentation.components.CustomProgressIndicator
import com.miguelol.casualapp.presentation.components.CustomSearchField
import com.miguelol.casualapp.presentation.components.CustomTopBar
import com.miguelol.casualapp.presentation.theme.CasualAppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchUsersScreen(
    uiStateFlow: StateFlow<SearchUsersUiState>,
    onEvent: (SearchEvents) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToUserProfile: (String) -> Unit,
    onNavigateToMyProfile: (String) -> Unit
) {

    val uiState by uiStateFlow.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState)},
        topBar = {
            CustomTopBar(
                navigateBack = true,
                onNavigateBack = onNavigateBack,
                actions = {
                    CustomSearchField(
                        modifier = Modifier
                            .fillMaxWidth(0.80f)
                            .padding(end = 4.dp),
                        value = uiState.searchText,
                        icon = R.drawable.round_search_24,
                        placeholder = "Search users",
                        onValueChange = { onEvent(SearchEvents.OnSearchTextInput(it)) }
                    )
                }
            )
        }
    ) { padding ->

        if (uiState.isLoading){
            CustomProgressIndicator()
        } else {
            SearchUsersContent(
                modifier = Modifier.padding(padding),
                uiState = uiState,
                onNavigateToProfile = onNavigateToUserProfile,
                onNavigateToMyProfile = onNavigateToMyProfile
            )
        }

        uiState.errorMessage?.let { message ->
            LaunchedEffect(message) {
                snackbarHostState.showSnackbar(message)
            }
        }
    }
}

@Preview
@Composable
fun PreviewSearchScreen() {
    CasualAppTheme {
        SearchUsersScreen(
            uiStateFlow = MutableStateFlow(SearchUsersUiState(
                users = listOf(
                    User(username = "miguelol99", name = "Miquel Nunez"),
                    User(username = "marianocp7", name = "Mariano Conde"),
                    User(username = "pablogdr", name = "Pablo Garcia"),
                )
            )),
            onEvent = {},
            onNavigateBack = {},
            onNavigateToUserProfile = {},
            onNavigateToMyProfile = {}
        )
    }
}