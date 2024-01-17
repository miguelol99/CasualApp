package com.miguelol.casualapp.presentation.screens.planprofile

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.miguelol.casualapp.presentation.screens.components.CustomAsyncImage
import com.miguelol.casualapp.presentation.screens.components.CustomDivider
import com.miguelol.casualapp.presentation.screens.components.CustomIcon
import com.miguelol.casualapp.presentation.screens.components.UserPreviewItem
import com.miguelol.casualapp.presentation.theme.CasualAppTheme
import com.miguelol.casualapp.presentation.theme.PowderBlue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PlanProfileContent(
    modifier: Modifier = Modifier,
    uiState: PlanProfileUiState,
    onEvent: (PlanProfileEvents) -> Unit,
    onNavigateToProfile: (String) -> Unit,
    onNavigateToMyProfile: (String) -> Unit
) {

    val formattedDate = remember(uiState.plan.datetime) {
        val date: Date = uiState.plan.datetime!!.toDate()
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
                        .height(130.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    CustomAsyncImage(
                        modifier = Modifier
                            .size(130.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        image = uiState.plan.image,
                        progressIndicatorColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 2.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = uiState.plan.type.toString(),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.width(8.dp))
                            CustomIcon(
                                icon = when (uiState.plan.type) {
                                    PlanType.PUBLIC -> R.drawable.round_public_24
                                    PlanType.PRIVATE -> R.drawable.outline_shield_24
                                    PlanType.SECRET -> R.drawable.round_visibility_off_24
                                }, color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(Modifier.weight(1f))
                        Row( //LOCATION ROW
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CustomIcon(
                                icon = R.drawable.outline_location_on_24, color = PowderBlue
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = uiState.plan.location,
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = PowderBlue
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Row( //LOCATION ROW
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Spacer(Modifier.width(2.dp))
                            CustomIcon(
                                modifier = Modifier.size(20.dp),
                                icon = R.drawable.round_edit_calendar_24,
                                color = PowderBlue
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = formattedDate,
                                style = MaterialTheme.typography.titleMedium,
                                color = PowderBlue
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Row( //LOCATION ROW
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Spacer(Modifier.width(2.dp))
                            CustomIcon(
                                modifier = Modifier.size(20.dp),
                                icon = R.drawable.round_alternate_email_24,
                                color = PowderBlue
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = uiState.plan.host.username,
                                style = MaterialTheme.typography.titleMedium,
                                color = PowderBlue
                            )
                        }
                    }
                }
            }
            item {
                Spacer(Modifier.height(12.dp))
                CustomDivider()
                Spacer(Modifier.height(8.dp))
                Text(
                    text = uiState.plan.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .sizeIn(maxHeight = 200.dp)
                ) {
                    item {
                        Text(text = uiState.plan.description, textAlign = TextAlign.Justify)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                Spacer(Modifier.height(12.dp))
                CustomDivider()
                Spacer(Modifier.height(8.dp))
            }
            item {
                Text(
                    text = "Participants",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            items(
                items = uiState.participants,
                key = {it.uid}
            ) { participant ->
                UserPreviewItem(
                    user = participant,
                    onClick = if (participant.uid == uiState.myUid)
                        onNavigateToMyProfile
                    else
                        onNavigateToProfile
                )
            }
        }
        when(uiState.requestState) {
            RequestState.ACCEPTED ->
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onEvent(PlanProfileEvents.OnJoinButtonPressed) }
                ) {
                    Text(text = "Leave")
                }
            RequestState.NOT_SENT ->
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onEvent(PlanProfileEvents.OnJoinButtonPressed) }
                ) {
                    Text(text = "Join")
                }
            RequestState.PENDING ->
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onEvent(PlanProfileEvents.OnJoinButtonPressed) }
                ) {
                    Text(text = "Pending")
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
                plan = Plan(
                    title = "Title of the plan",
                    host = UserPreview(
                        username = "marianela"
                    ),
                    datetime = Timestamp.now(),
                    location = "Calle Doctor cazalla",
                    description = "This is an example of how a description might look like" + "in the plan profile"
                )
            ),
            onEvent = {},
            onNavigateToMyProfile = {},
            onNavigateToProfile = {}
        )
    }
}