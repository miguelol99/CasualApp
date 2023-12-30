package com.miguelol.casualapp.presentation.screens.requests

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.miguelol.casualapp.R
import com.miguelol.casualapp.domain.model.FriendRequest
import com.miguelol.casualapp.domain.model.RequestState
import com.miguelol.casualapp.domain.model.UserPreview
import com.miguelol.casualapp.presentation.components.CustomProgressIndicator
import com.miguelol.casualapp.presentation.components.CustomTopBar
import com.miguelol.casualapp.presentation.screens.components.CustomEmptyIcon
import com.miguelol.casualapp.presentation.theme.CasualAppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestsScreen(
    uiStateFlow: StateFlow<RequestsUiState>,
    onEvent: (RequestsEvents) -> Unit,
    onNavigateToProfile: (String) -> Unit
) {
    val uiState by uiStateFlow.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CustomTopBar(
                title = "Requests"
            )
        }
    ) { padding ->

        when {
            uiState.isLoading -> CustomProgressIndicator()
            uiState.requests.isEmpty() ->
                CustomEmptyIcon(
                    icon = R.drawable.round_person_24,
                    text = "You have no requests"
                )
            else ->
                RequestsContent(
                modifier = Modifier.padding(padding),
                uiState = uiState,
                onEvent = onEvent,
                onNavigateToProfile = onNavigateToProfile
            )
        }

        uiState.errorMessage?.let {message ->
            LaunchedEffect(message) {
                snackbarHostState.showSnackbar(message)
            }
        }

    }
}

@Preview
@Composable
fun PreviewNotificationsScreen() {
    CasualAppTheme {
        RequestsScreen(
            uiStateFlow = MutableStateFlow(RequestsUiState(
                requests = listOf(FriendRequest(
                    fromUser = UserPreview(
                        username = "marianocp7",
                        name = "Mariano Conde Perez Saiz"
                    ),
                    state = RequestState.PENDING
                    )
                ))
            ),
            onEvent = {},
            onNavigateToProfile = {}
        )
    }
}