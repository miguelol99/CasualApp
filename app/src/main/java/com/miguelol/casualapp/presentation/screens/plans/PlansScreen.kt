package com.miguelol.casualapp.presentation.screens.plans

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
import com.miguelol.casualapp.presentation.components.CustomTopBar
import com.miguelol.casualapp.presentation.screens.components.CustomDropdownMenu
import com.miguelol.casualapp.presentation.screens.components.CustomEmptyIcon
import com.miguelol.casualapp.presentation.theme.CasualAppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlansScreen(
    uiStateFlow: StateFlow<PlansUiState>,
    onEvent: (PlansEvents) -> Unit,
    onNavigateToPlanDetails: (String) -> Unit
) {

    val uiState by uiStateFlow.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            CustomTopBar(title = uiState.topBarLabel) {
                CustomDropdownMenu(
                    icon = R.drawable.round_filter_list_24,
                    onPublicPlansSelected = { onEvent(PlansEvents.OnPublicFilter) },
                    onPrivatePlansSelected = { onEvent(PlansEvents.OnPrivateFilter)}
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { contentPadding ->

        when{
            uiState.isLoading -> CustomProgressIndicator()
            uiState.plans.isEmpty() ->
                CustomEmptyIcon(
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
            uiStateFlow = MutableStateFlow(PlansUiState(
                plans = listOf(
                    Plan(
                        datetime = Timestamp.now(),
                        host = UserPreview(username = "marianocp7"),
                        location = "Mi casa",
                        title = "Vamos al cine"
                    )
                )
            )),
            onEvent = {},
            onNavigateToPlanDetails = {}
        )
    }
}