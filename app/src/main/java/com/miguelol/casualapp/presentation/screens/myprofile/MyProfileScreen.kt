package com.miguelol.casualapp.presentation.screens.myprofile

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
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
import com.miguelol.casualapp.domain.model.User
import com.miguelol.casualapp.presentation.components.CustomProgressIndicator
import com.miguelol.casualapp.presentation.components.CustomTopBar
import com.miguelol.casualapp.presentation.screens.components.CustomIcon
import com.miguelol.casualapp.presentation.theme.CasualAppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyProfileScreen(
    uiStateFlow: StateFlow<MyProfileUiState>,
    onEvent: (MyProfileEvents) -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToFriendList: (String) -> Unit,
    onNavigateToAddNewFriend: () -> Unit
) {

    val uiState by uiStateFlow.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CustomTopBar(
                title = uiState.user.username,
                actions = {
                    IconButton(onClick = { onNavigateToAddNewFriend() }) {
                        CustomIcon(icon = R.drawable.round_person_search_24)
                    }
                    IconButton(onClick = { onNavigateToEditProfile() }) {
                        CustomIcon(icon = R.drawable.round_settings_24)
                    }
                }
            )
        }
    ) {
        if (uiState.isLoading) {
            CustomProgressIndicator()
        } else {
            ProfileContent(
                modifier = Modifier.padding(it),
                uiState = uiState,
                onNavigateToFriendList = onNavigateToFriendList
            )
        }

        uiState.errorMessage?.let { message ->
            LaunchedEffect(message) {
                snackbarHostState.showSnackbar(message)
                onEvent(MyProfileEvents.OnErrorMessageShown)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewProfileScreen() {
    CasualAppTheme {
        MyProfileScreen(
            uiStateFlow = MutableStateFlow(MyProfileUiState(
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
            onNavigateToEditProfile = {},
            onNavigateToFriendList = {},
            onNavigateToAddNewFriend = {}
        )
    }
}