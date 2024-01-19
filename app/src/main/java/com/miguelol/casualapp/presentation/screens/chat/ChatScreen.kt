package com.miguelol.casualapp.presentation.screens.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.miguelol.casualapp.R
import com.miguelol.casualapp.presentation.components.CustomProgressIndicator
import com.miguelol.casualapp.presentation.screens.components.CustomAsyncImage
import com.miguelol.casualapp.presentation.screens.components.CustomIcon
import com.miguelol.casualapp.presentation.theme.CasualAppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    uiStateFlow: StateFlow<ChatUiState>,
    onEvent: (ChatEvents) -> Unit,
    onNavigateToPlanProfile: () -> Unit,
    onNavigateToMyPlans: () -> Unit,
    onNavigateBack: () -> Unit
) {

    val uiState by uiStateFlow.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .clickable { onNavigateToPlanProfile() }
                    ) {
                        CustomAsyncImage(
                            image = uiState.image,
                            progressIndicatorColor = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = uiState.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack() }) {
                        CustomIcon(icon = R.drawable.round_arrow_back_24)
                    }
                }
            )
        }
    ) { padding ->

        when {
            uiState.isLoading -> CustomProgressIndicator()
            else -> ChatContent(
                modifier = Modifier.padding(padding),
                uiState = uiState,
                onEvent = onEvent
            )
        }

        if (uiState.isDeleted || !uiState.iAmAParticipant)
            LaunchedEffect(Unit) { onNavigateToMyPlans() }


        if (uiState.error != null)
            LaunchedEffect(Unit) {
                snackbarHostState.showSnackbar(uiState.error!!)
                onEvent(ChatEvents.OnErrorMessageShown)
            }


    }
}

@Preview(showBackground = true)
@Composable
fun PreviewChatScreen() {
    CasualAppTheme {
         ChatScreen(
             uiStateFlow = MutableStateFlow(
                 ChatUiState(
                     title = "Vamos a tomar unas cañas con los colegass",
                     iAmAParticipant = false
                 )
             ),
             onEvent = {},
             onNavigateToPlanProfile = {},
             onNavigateBack = {},
             onNavigateToMyPlans = {}
         )
    }
}