package com.miguelol.casualapp.presentation.screens.myplans

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.miguelol.casualapp.domain.model.PlanPreview
import com.miguelol.casualapp.presentation.screens.plans.PlansUiState
import com.miguelol.casualapp.presentation.screens.plans.components.PlanItem
import com.miguelol.casualapp.presentation.theme.CasualAppTheme


@Composable
fun MyPlansContent(
    modifier: Modifier = Modifier,
    uiState: MyPlansUiState,
    onNavigateToPlanDetails: (String) -> Unit
) {


    LazyColumn(
        modifier = modifier.fillMaxSize(),
    ) {
        item { Spacer(modifier = Modifier.height(12.dp)) }
        items(
            items = uiState.plans,
            key = { plan -> plan.id }
        ) { plan ->
            PlanItem(
                plan = plan,
                onClick = onNavigateToPlanDetails
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewPlansContent() {
    CasualAppTheme {
        MyPlansContent(
            uiState = MyPlansUiState(
                plans = listOf(
                    PlanPreview(
                        datetime = Timestamp.now(),
                        host = "marianocp7",
                        location = "Mi casa",
                        title = "Vamos al cine"
                    )
                )
            ),
            onNavigateToPlanDetails = {}
        )
    }
}