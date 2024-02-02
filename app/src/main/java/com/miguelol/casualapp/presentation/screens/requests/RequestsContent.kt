package com.miguelol.casualapp.presentation.screens.requests

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.miguelol.casualapp.domain.model.FriendRequest
import com.miguelol.casualapp.domain.model.PlanRequest
import com.miguelol.casualapp.domain.model.UserPreview
import com.miguelol.casualapp.presentation.screens.components.FriendRequestItem
import com.miguelol.casualapp.presentation.screens.components.PlanRequestItem
import com.miguelol.casualapp.presentation.theme.CasualAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestsContent(
    modifier: Modifier = Modifier,
    uiState: RequestsUiState,
    onEvent: (RequestsEvents) -> Unit,
    onNavigateToProfile: (String) -> Unit,
    onNavigateToPlan: (String) -> Unit
) {

    LazyColumn(
        modifier = modifier
            .padding(horizontal = 8.dp)
            .padding(top = 16.dp)
            .fillMaxSize(),
    ) {
        items(
            items = uiState.requests,
            key = { request -> request.id }
        ) { request ->

            when (request) {
                is CombinedRequest.Friend ->  FriendRequestItem(
                    request = request.friendRequest,
                    onAccept = { onEvent(RequestsEvents.OnAccept(request)) },
                    onDecline = { onEvent(RequestsEvents.OnDecline(request)) },
                    onClick = { onNavigateToProfile(request.friendRequest.fromUser.uid) }
                )
                is CombinedRequest.Plan -> PlanRequestItem(
                    request = request.planRequest,
                    onAccept = { onEvent(RequestsEvents.OnAccept(request)) },
                    onDecline = { onEvent(RequestsEvents.OnDecline(request)) },
                    onNavigateToProfile = { onNavigateToProfile(request.planRequest.fromUser.uid) },
                    onNavigateToPlan = { onNavigateToPlan(request.planRequest.plan.id) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

    }
}

@Preview
@Composable
fun PreviewRequestsContent() {
    CasualAppTheme {
        RequestsContent(
            uiState = RequestsUiState(
                requests = listOf(
                    CombinedRequest.Friend(
                        friendRequest = FriendRequest(
                            fromUser = UserPreview(
                                username = "marianocp7",
                                name = "Mariano Conde"
                            )
                        )
                    ),
                    CombinedRequest.Plan(
                        planRequest = PlanRequest(
                            fromUser = UserPreview(
                                username = "pablogdr",
                                name = "Pablo García"
                            ),
                            plan = PlanPreview(
                                title = "Tomar unas cañas",
                            )
                        )
                    )
                )
            ),
            onEvent = {},
            onNavigateToProfile = {},
            onNavigateToPlan = {}
        )
    }
}