package com.miguelol.casualapp.presentation.screens.userprofile

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
import com.miguelol.casualapp.domain.model.User
import com.miguelol.casualapp.presentation.components.CustomProgressIndicator
import com.miguelol.casualapp.presentation.components.CustomTopBar
import com.miguelol.casualapp.presentation.screens.myprofile.MyProfileEvents
import com.miguelol.casualapp.presentation.theme.CasualAppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    uiStateFlow: StateFlow<UserProfileUiState>,
    onEvent: (UserProfileEvents) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToFriendList: (String) -> Unit
) {

    val uiState by uiStateFlow.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CustomTopBar(
                title = uiState.user.username,
                navigateBack = true,
                onNavigateBack = onNavigateBack,
            )
        }
    ) {
        if (uiState.isLoading) {
            CustomProgressIndicator()
        } else {
            UserProfileContent(
                modifier = Modifier.padding(it),
                uiState = uiState,
                onEvent = onEvent,
                onNavigateToFriendList = onNavigateToFriendList
            )
        }

        uiState.errorMessage?.let { message ->
            LaunchedEffect(message) {
                snackbarHostState.showSnackbar(message)
                onEvent(UserProfileEvents.OnErrorMessageShown)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewProfileScreen() {
    CasualAppTheme {
        UserProfileScreen(
            uiStateFlow = MutableStateFlow(UserProfileUiState(
                user = User(
                    name = "Miguel Antonio",
                    username = "miguelol_99",
                    age = "25",
                    description = "La composición de Navigation también admite argumentos de navegación" +
                            "opcionales.",
                    image = ""
                )
            )),
            onEvent = {},
            onNavigateBack = {},
            onNavigateToFriendList = {},
        )
    }
}