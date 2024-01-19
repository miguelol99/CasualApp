package com.miguelol.casualapp.presentation.screens.planprofile

import android.widget.Space
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.miguelol.casualapp.R
import com.miguelol.casualapp.domain.model.Plan
import com.miguelol.casualapp.domain.model.PlanType
import com.miguelol.casualapp.domain.model.RequestState
import com.miguelol.casualapp.domain.model.UserPreview
import com.miguelol.casualapp.presentation.screens.chat.ChatEvents
import com.miguelol.casualapp.presentation.screens.components.CustomAsyncImage
import com.miguelol.casualapp.presentation.screens.components.CustomDivider
import com.miguelol.casualapp.presentation.screens.components.CustomEmptyIcon
import com.miguelol.casualapp.presentation.screens.components.CustomIcon
import com.miguelol.casualapp.presentation.screens.components.UserPreviewItem
import com.miguelol.casualapp.presentation.theme.CasualAppTheme
import com.miguelol.casualapp.presentation.theme.PowderBlue
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale

@Composable
fun PlanProfileContent(
    modifier: Modifier = Modifier,
    uiState: PlanProfileUiState,
    onEvent: (PlanProfileEvents) -> Unit,
    onNavigateToProfile: (String) -> Unit,
    onNavigateToMyProfile: (String) -> Unit,
    onNavigateToChat: () -> Unit
) {

    val formattedDate = remember(uiState.plan.datetime) {
        val date: Date = uiState.plan.datetime?.toDate() ?: Date()
        val formatter = SimpleDateFormat("MMM dd - HH:mm", Locale.getDefault())
        formatter.format(date).uppercase()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CustomAsyncImage(
                        modifier = Modifier
                            .height(200.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp)),
                        image = uiState.plan.image,
                        progressIndicatorColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
            item {
                Spacer(Modifier.height(12.dp))
                Row( //LOCATION ROW
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CustomIcon(
                        icon = R.drawable.round_edit_calendar_24,
                        color = PowderBlue,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.titleMedium,
                        color = PowderBlue
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    CustomIcon(
                        icon = R.drawable.baseline_map_24,
                        color = PowderBlue,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        modifier = Modifier.weight(1f),
                        text = uiState.plan.location,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = PowderBlue
                    )
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    text = uiState.plan.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )
                Spacer(Modifier.height(2.dp))
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(
                    modifier = Modifier
                        .sizeIn(maxHeight = 200.dp)
                ) {
                    item {
                        Text(text = uiState.plan.description, textAlign = TextAlign.Justify)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                Spacer(Modifier.height(12.dp))
                CustomDivider()
                Spacer(Modifier.height(12.dp))
            }

            if (uiState.participants.isEmpty()){
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    CustomEmptyIcon(
                        icon = R.drawable.round_person_24,
                        text = "There are no participants yet!"
                    )
                }
            } else {
                items(
                    items = uiState.participants,
                    key = {it.uid}
                ) { participant ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        UserPreviewItem(
                            modifier = Modifier.weight(1f),
                            user = participant,
                            onClick = if (participant.uid == uiState.myUid)
                                onNavigateToMyProfile
                            else
                                onNavigateToProfile
                        )
                        if (participant.uid == uiState.plan.host.uid) {
                            TextButton(onClick = { /*TODO*/ }) {
                                Text(text = "HOST")
                            }
                        } else {
                            if (uiState.iAmTheHost) {
                                IconButton(onClick = { onEvent(PlanProfileEvents.OnDeleteParticipant(participant.uid)) }) {
                                    CustomIcon(
                                        modifier = Modifier.padding(end = 10.dp),
                                        icon = R.drawable.round_delete_24,
                                        color = PowderBlue
                                    )
                                }
                            }
                        }
                    }

                }
            }

        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (uiState.iAmTheHost) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = { onEvent(PlanProfileEvents.OnJoinButtonPressed) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text(text = "Delete")
                    Spacer(modifier = Modifier.width(2.dp))
                    CustomIcon(icon = R.drawable.round_delete_24, modifier = Modifier.size(20.dp))
                }
                if (!uiState.fromChat) {
                    Spacer(modifier = Modifier.width(8.dp))
                    FilledIconButton(
                        modifier = Modifier.size(44.dp),
                        onClick = { onNavigateToChat() }
                    ) {
                        CustomIcon(
                            icon = R.drawable.round_chat_24)
                    }
                }
            } else
                when(uiState.requestState) {
                    RequestState.ACCEPTED -> {
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = { onEvent(PlanProfileEvents.OnJoinButtonPressed) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Text(text = "Leave")
                        }
                        if (!uiState.fromChat) {
                            Spacer(modifier = Modifier.width(8.dp))
                            FilledIconButton(
                                modifier = Modifier.size(44.dp),
                                onClick = { onNavigateToChat() }
                            ) {
                                CustomIcon(
                                    icon = R.drawable.round_chat_24
                                )
                            }
                        }
                    }
                    RequestState.NOT_SENT ->
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = { onEvent(PlanProfileEvents.OnJoinButtonPressed) }
                        ) {
                            Text(text = "Join")
                        }
                    RequestState.PENDING ->
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = { onEvent(PlanProfileEvents.OnJoinButtonPressed) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PowderBlue
                            )
                        ) {
                            Text(text = "Pending")
                        }
                }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPlanProfileContent() {
    CasualAppTheme {
        PlanProfileContent(
            uiState = PlanProfileUiState(
                iAmTheHost = true,
                participants = listOf(
                    UserPreview(
                        uid = "myUid",
                        username = "miguelol99",
                        name = "Miguel Antonio",
                    ),
                    UserPreview(
                        uid = "otherUid",
                        username = "marinaocp7",
                        name = "Mariano Conde",
                    )
                ),
                plan = Plan(
                    title = "Title of the plan",
                    host = UserPreview(
                        uid = "myUid",
                        username = "marianela"
                    ),
                    datetime = Timestamp.now(),
                    location = "Calle Doctor cazallaaaaaaaaaaaa",
                    description = "This is an example of how a description might look like" + "in the plan profile"
                )
            ),
            onEvent = {},
            onNavigateToMyProfile = {},
            onNavigateToProfile = {},
            onNavigateToChat = {}
        )
    }
}