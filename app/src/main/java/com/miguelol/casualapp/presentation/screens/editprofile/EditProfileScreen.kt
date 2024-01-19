package com.miguelol.casualapp.presentation.screens.editprofile

import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.miguelol.casualapp.R
import com.miguelol.casualapp.presentation.components.CustomProgressIndicator
import com.miguelol.casualapp.presentation.components.CustomTopBar
import com.miguelol.casualapp.presentation.screens.components.CustomFloatingActionButton
import com.miguelol.casualapp.presentation.screens.editprofile.components.EditProfileContent
import com.miguelol.casualapp.presentation.theme.CasualAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    uiStateFlow: StateFlow<EditProfileUiState>,
    onEvent: (EditProfileEvents) -> Unit,
    onNavigateToProfileScreen: () -> Unit,
    onSignOut: () -> Unit
) {

    val uiState by uiStateFlow.collectAsStateWithLifecycle()

    val scope: CoroutineScope = rememberCoroutineScope()
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            CustomTopBar(
                title = "Profile Settings",
                navigateBack = true,
                onNavigateBack = { onNavigateToProfileScreen() }
            ) {
                TextButton(onClick = {onEvent(EditProfileEvents.OnSignOut) }) {
                    Text(text = "Sign Out")
                }
            }
        },
        floatingActionButton = {
            CustomFloatingActionButton(
                onCLick = { onEvent(EditProfileEvents.OnSave) },
                icon = R.drawable.round_check_24
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->

        if (uiState.isLoading){
            CustomProgressIndicator()
        } else {
            EditProfileContent(
                padding = paddingValues,
                uiState = uiState,
                onUsernameInput = { onEvent(EditProfileEvents.OnUsernameInput(it)) },
                onNameInput = { onEvent(EditProfileEvents.OnNameInput(it)) },
                onDescriptionInput = { onEvent(EditProfileEvents.OnDescriptionInput(it)) },
                onDateOfBirthInput = { onEvent(EditProfileEvents.OnDateOfBirthInput(it)) },
                onImageInput = { onEvent(EditProfileEvents.OnImageInput(it)) },
            )
        }

        if (uiState.errorMessage != null)
            LaunchedEffect(uiState.errorMessage) {
                scope.launch {
                    snackbarHostState.showSnackbar(uiState.errorMessage!!)
                    onEvent(EditProfileEvents.OnErrorMessageShown)
                }
            }
    }

    if (uiState.updated) {
        LaunchedEffect(Unit) { onNavigateToProfileScreen() }
    }

    if (uiState.signOut){
        LaunchedEffect(Unit) { onSignOut() }
    }

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewEditProfileScreen() {
    CasualAppTheme {
        EditProfileScreen(
            uiStateFlow = MutableStateFlow(EditProfileUiState()),
            onEvent = {},
            onNavigateToProfileScreen = {},
            onSignOut = {}
        )
    }
}


