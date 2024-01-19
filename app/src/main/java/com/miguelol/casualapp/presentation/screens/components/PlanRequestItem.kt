package com.miguelol.casualapp.presentation.screens.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import com.miguelol.casualapp.domain.model.PlanPreview
import com.miguelol.casualapp.domain.model.PlanRequest
import com.miguelol.casualapp.domain.model.UserPreview
import com.miguelol.casualapp.presentation.theme.CasualAppTheme
import com.miguelol.casualapp.presentation.theme.PowderBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanRequestItem(
    request: PlanRequest,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToPlan: () -> Unit
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        onClick = onNavigateToProfile
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
            Button(onClick = onAccept ) {
                CustomIcon(icon = R.drawable.round_check_24,)
            }
            Spacer(modifier = Modifier.width(4.dp))
            IconButton(onClick = onDecline) {
                CustomIcon(
                    icon = R.drawable.round_delete_24,
                    color = PowderBlue
                )
            }
        }
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Wants to join: ",
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            Text(
                modifier = Modifier.clickable { onNavigateToPlan() },
                text = request.plan.title,
                style = MaterialTheme.typography.titleMedium,
                color = PowderBlue,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }
}

@Preview
@Composable
fun PreviewPlanRequestItem() {
    CasualAppTheme {
        PlanRequestItem(
            request = PlanRequest(
                fromUser = UserPreview(
                    username = "pablogdr",
                    name = "Pablo García"
                ),
                plan = PlanPreview(
                    title = "Tomar unas cañas en algun lado por ahi al lado",
                )
            ),
            onAccept = { },
            onDecline = { },
            onNavigateToProfile = {},
            onNavigateToPlan = {}
        )
    }
}