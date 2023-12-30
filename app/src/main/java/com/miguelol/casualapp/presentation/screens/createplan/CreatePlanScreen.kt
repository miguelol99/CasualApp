package com.miguelol.casualapp.presentation.screens.createplan

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.miguelol.casualapp.R
import com.miguelol.casualapp.presentation.components.CustomProgressIndicator
import com.miguelol.casualapp.presentation.components.CustomTopBar
import com.miguelol.casualapp.presentation.screens.components.CustomFloatingActionButton
import com.miguelol.casualapp.presentation.theme.CasualAppTheme
import com.miguelol.casualapp.utils.Constants.PRIVATE
import com.miguelol.casualapp.utils.Constants.PUBLIC

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePlanScreen(
    uiState: CreatePlanUiState,
    onEvent: (CreatePlanEvents) -> Unit,
    onNavigateToMyPlansScreen: () -> Unit,
    onNavigateBack: () -> Unit
) {

    Scaffold(
        topBar = {
            CustomTopBar(
                title = "New Plan",
                navigateBack = true,
                onNavigateBack = { onNavigateBack() }
            )
        },
        floatingActionButton = {
            when(uiState.type) {
                "" ->
                    CustomFloatingActionButton(
                        onCLick = { onEvent(CreatePlanEvents.OnCreate)},
                        icon = R.drawable.round_check_24
                    )
                PUBLIC ->
                    CustomFloatingActionButton(
                        onCLick = { onEvent(CreatePlanEvents.OnCreate)},
                        icon = R.drawable.round_check_24
                    )
                else ->
                    CustomFloatingActionButton(
                    onCLick = { /*TODO*/},
                    icon = R.drawable.round_person_add_alt_1_24
                )
            }
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

        if (uiState.flag) {
            LaunchedEffect(Unit) { onNavigateToMyPlansScreen() }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCreatePlanScreen() {
    CasualAppTheme {
         CreatePlanScreen(
             uiState = CreatePlanUiState(),
             onEvent = {},
             onNavigateToMyPlansScreen = {},
             onNavigateBack = {}
         )
    }
}