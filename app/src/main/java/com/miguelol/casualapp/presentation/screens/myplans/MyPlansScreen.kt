package com.miguelol.casualapp.presentation.screens.myplans

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
import com.miguelol.casualapp.R
import com.miguelol.casualapp.domain.model.Plan
import com.miguelol.casualapp.domain.model.UserPreview
import com.miguelol.casualapp.presentation.components.CustomProgressIndicator
import com.miguelol.casualapp.presentation.screens.components.CustomEmptyIcon
import com.miguelol.casualapp.presentation.screens.components.CustomFloatingActionButton
import com.miguelol.casualapp.presentation.theme.CasualAppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPlansScreen(
    uiStateFlow: StateFlow<MyPlansUiState>,
    onEvent: (MyPlansEvents) -> Unit,
    onNavigateToCreatePlan: () -> Unit,
    onNavigateToPlanDetails: (String) -> Unit
) {

    val uiState by uiStateFlow.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState)},
        floatingActionButton = {
            CustomFloatingActionButton(
                onCLick = { onNavigateToCreatePlan() },
                icon = R.drawable.outline_add_24
            )
        }
    ) { contentPadding ->

        when {
            uiState.isLoading -> CustomProgressIndicator()
            uiState.plans.isEmpty() -> CustomEmptyIcon(
                icon = R.drawable.round_add_circle_outline_24,
                text = "Create a plan or join one!"
            )
            else ->
                MyPlansContent(
                    modifier = Modifier.padding(contentPadding),
                    uiState = uiState,
                    onNavigateToPlanDetails = { onNavigateToPlanDetails(it) }
                )
        }

        uiState.errorMessage?.let { message ->
            LaunchedEffect(message) {
                snackbarHostState.showSnackbar(message)
                onEvent(MyPlansEvents.OnErrorMessageShown)
            }
        }
    }
}

@Preview
@Composable
fun PreviewMyPlansScreen() {
    CasualAppTheme {
        MyPlansScreen(
            uiStateFlow = MutableStateFlow(
                MyPlansUiState(listOf(
                    Plan(
                        datetime = Timestamp.now(),
                        host = UserPreview(username = "marianocp7"),
                        location = "Mi casa",
                        title = "Vamos al cine"
                    )
                )
            )
            ),
            onEvent = {},
            onNavigateToCreatePlan = {  },
            onNavigateToPlanDetails = {}
        )
    }
}