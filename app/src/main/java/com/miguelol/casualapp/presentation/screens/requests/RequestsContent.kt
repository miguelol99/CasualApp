package com.miguelol.casualapp.presentation.screens.requests

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.miguelol.casualapp.R
import com.miguelol.casualapp.domain.model.FriendRequest
import com.miguelol.casualapp.domain.model.RequestState
import com.miguelol.casualapp.domain.model.UserPreview
import com.miguelol.casualapp.presentation.screens.components.CustomAsyncImage
import com.miguelol.casualapp.presentation.screens.components.CustomIcon
import com.miguelol.casualapp.presentation.theme.CasualAppTheme
import com.miguelol.casualapp.presentation.theme.PowderBlue
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestsContent(
    modifier: Modifier = Modifier,
    uiState: RequestsUiState,
    onEvent: (RequestsEvents) -> Unit,
    onNavigateToProfile: (String) -> Unit
) {

    LazyColumn(
        modifier = modifier
            .padding(horizontal = 8.dp)
            .padding(top = 16.dp)
            .fillMaxSize(),
    ) {
        items(
            items = uiState.requests,
            key = { request -> request.fromUser.uid }
        ) { request ->

            ElevatedCard(
                elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                onClick = { onNavigateToProfile(request.fromUser.uid) }
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 10.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        CustomAsyncImage(
                            image = request.fromUser.image,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = request.fromUser.name,
                            style = MaterialTheme.typography.titleMedium,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            color = PowderBlue,
                            text = request.fromUser.username,
                            style = MaterialTheme.typography.titleSmall,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(onClick = { onEvent(RequestsEvents.OnAccept(request)) }) {
                        CustomIcon(icon = R.drawable.round_person_add_alt_1_24,)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = { onEvent(RequestsEvents.OnDecline(request)) },
                    ) {
                        CustomIcon(
                            icon = R.drawable.round_delete_24,
                            color = PowderBlue
                        )
                    }
                }
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
                    FriendRequest(
                        fromUser = UserPreview(
                            username = "marianocp7",
                            name = "Mariano Conde Perez Saiz"
                        ),
                        state = RequestState.PENDING
                    )
                )
            ),
            onEvent = {},
            onNavigateToProfile = {}
        )
    }
}