package com.miguelol.casualapp.presentation.screens.plans

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.miguelol.casualapp.R
import com.miguelol.casualapp.domain.model.PlanPreview
import com.miguelol.casualapp.presentation.components.CustomProgressIndicator
import com.miguelol.casualapp.presentation.screens.components.CustomEmptyIcon
import com.miguelol.casualapp.presentation.screens.plans.components.CustomTopBarDropdownMenu
import com.miguelol.casualapp.presentation.theme.CasualAppTheme
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlansScreen(
    uiState: PlansUiState,
    onEvent: (PlansEvents) -> Unit,
    onNavigateToPlanDetails: (String) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {

    Scaffold(
        topBar = {
            CustomTopBarDropdownMenu(
                title = uiState.topBarLabel,
                onAllFilterSelected = { onEvent(PlansEvents.OnAllFilter) },
                onPrivateFilterSelected = { onEvent(PlansEvents.OnPrivateFilter) }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { contentPadding ->

        when{
            uiState.isLoading -> CustomProgressIndicator()
            uiState.plans.isEmpty() -> CustomEmptyIcon(
                icon = R.drawable.round_person_24,
                text = "Wow! There are no plans"
            )
            else ->
                PlansContent(
                    modifier = Modifier.padding(contentPadding),
                    uiState = uiState,
                    onNavigateToPlanDetails = { onNavigateToPlanDetails(it) }
                )
        }

        uiState.errorMessage?.let { message ->
            LaunchedEffect(message) {
                snackbarHostState.showSnackbar(message)
                onEvent(PlansEvents.OnErrorMessageShown)
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewPlansScreen() {
    CasualAppTheme {
        PlansScreen(
            uiState = PlansUiState(
                plans = listOf(
                    PlanPreview(
                        datetime = Timestamp.now(),
                        host = "marianocp7",
                        location = "Mi casa",
                        title = "Vamos al cine"
                    )
                )
            ),
            onEvent = {},
            onNavigateToPlanDetails = {}
        )
    }
}