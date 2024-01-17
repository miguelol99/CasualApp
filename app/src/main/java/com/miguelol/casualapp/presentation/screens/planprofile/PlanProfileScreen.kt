package com.miguelol.casualapp.presentation.screens.planprofile

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
import com.google.firebase.Timestamp
import com.miguelol.casualapp.domain.model.Plan
import com.miguelol.casualapp.domain.model.UserPreview
import com.miguelol.casualapp.presentation.components.CustomProgressIndicator
import com.miguelol.casualapp.presentation.components.CustomTopBar
import com.miguelol.casualapp.presentation.theme.CasualAppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanProfileScreen(
    uiStateFlow: StateFlow<PlanProfileUiState>,
    onEvent: (PlanProfileEvents) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToMyProfile: (String) -> Unit,
    onNavigateToProfile: (String) -> Unit
) {

    val uiState by uiStateFlow.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CustomTopBar(
                title = "Plan Profile",
                navigateBack = true,
                onNavigateBack = onNavigateBack
            )
        }
    ) {

        if (uiState.isLoading) {
            CustomProgressIndicator()
        } else {
            PlanProfileContent(
                modifier = Modifier.padding(it),
                uiState = uiState,
                onEvent = onEvent,
                onNavigateToMyProfile = onNavigateToMyProfile,
                onNavigateToProfile = onNavigateToProfile
            )
        }

        uiState.errorMessage?.let{ message ->
            LaunchedEffect(message) {
                snackbarHostState.showSnackbar(message)
                onEvent(PlanProfileEvents.OnErrorMessageShown)
            }
        }

    }
}

@Preview
@Composable
fun PreviewPlanProfileScreen() {
    CasualAppTheme {
        PlanProfileScreen(
            uiStateFlow = MutableStateFlow(
                PlanProfileUiState(
                    plan = Plan(
                        title = "Title of the plan",
                        host = UserPreview(
                            username = "marianela"
                        ),
                        datetime = Timestamp.now(),
                        description = "This is an example of how a description might look like" +
                                "in the plan profile",
                        location = "Calle Doctor Cazalla"
                    )
                )
            ),
            onEvent = {},
            onNavigateBack = {},
            onNavigateToProfile = {},
            onNavigateToMyProfile = {}
        )
    }
}