package com.miguelol.casualapp.presentation.screens.login

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.miguelol.casualapp.presentation.components.CustomProgressIndicator
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    uiStateFlow: StateFlow<LoginUiState>,
    onEvent: (LoginEvents) -> Unit,
    onNavigateToHome: () -> Unit
) {

    val uiState by uiStateFlow.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->

        if (uiState.isLoading) {
            CustomProgressIndicator()
        } else {
            LoginContent(
                modifier = Modifier.padding(innerPadding),
                uiState = uiState,
                onEvent = onEvent,
            )
        }

        if (uiState.errorMessage != null) {
            LaunchedEffect(uiState.errorMessage) {
                snackbarHostState.showSnackbar(uiState.errorMessage!!)
                onEvent(LoginEvents.OnErrorMessageShown)
            }
        }
    }

    if (uiState.isLoggedIn) {
        LaunchedEffect(Unit) { onNavigateToHome() }
    }
}