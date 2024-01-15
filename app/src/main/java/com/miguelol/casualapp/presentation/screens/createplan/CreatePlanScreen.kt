package com.miguelol.casualapp.presentation.screens.createplan

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
import com.miguelol.casualapp.presentation.components.CustomProgressIndicator
import com.miguelol.casualapp.presentation.components.CustomTopBar
import com.miguelol.casualapp.presentation.screens.components.CustomFloatingActionButton
import com.miguelol.casualapp.presentation.theme.CasualAppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePlanScreen(
    uiStateFlow: StateFlow<CreatePlanUiState>,
    onEvent: (CreatePlanEvents) -> Unit,
    onNavigateToMyPlansScreen: () -> Unit,
    onNavigateBack: () -> Unit
) {

    val uiState by uiStateFlow.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState)},
        topBar = {
            CustomTopBar(
                title = "New Plan",
                navigateBack = true,
                onNavigateBack = { onNavigateBack() }
            )
        },
        floatingActionButton = {
            CustomFloatingActionButton(
                onCLick = { onEvent(CreatePlanEvents.OnCreate)},
                icon = R.drawable.round_check_24
            )
        }

    ) { paddingValues ->

        if (uiState.isLoading){
            CustomProgressIndicator()
        } else {
            CreatePlanContent(
                modifier = Modifier.padding(paddingValues),
                uiState = uiState,
                onEvent = onEvent
            )
        }

        if (uiState.planCreated) {
            LaunchedEffect(Unit) { onNavigateToMyPlansScreen() }
        }

        uiState.errorMessage?.let {message ->
            LaunchedEffect(message) {
                snackbarHostState.showSnackbar(message)
                onEvent(CreatePlanEvents.OnErrorMessageShown)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCreatePlanScreen() {
    CasualAppTheme {
         CreatePlanScreen(
             uiStateFlow = MutableStateFlow(
                 CreatePlanUiState(

                 )
             ),
             onEvent = {},
             onNavigateToMyPlansScreen = { },
             onNavigateBack = {}
         )
    }
}