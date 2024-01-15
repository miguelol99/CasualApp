package com.miguelol.casualapp.presentation.screens.plans.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.miguelol.casualapp.R
import com.miguelol.casualapp.domain.model.Plan
import com.miguelol.casualapp.domain.model.PlanType
import com.miguelol.casualapp.domain.model.UserPreview
import com.miguelol.casualapp.presentation.screens.components.CustomAsyncImage
import com.miguelol.casualapp.presentation.screens.components.CustomIcon
import com.miguelol.casualapp.presentation.theme.CasualAppTheme
import com.miguelol.casualapp.presentation.theme.PowderBlue
import com.miguelol.casualapp.utils.Constants.PRIVATE
import com.miguelol.casualapp.utils.Constants.PUBLIC
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PlanItem(
    plan: Plan,
    onClick: (String) -> Unit
) {

    val formattedDate = remember(plan.datetime) {
        val date: Date = plan.datetime!!.toDate()
        val formatter = SimpleDateFormat("MMM dd - HH:mm", Locale.getDefault())
        formatter.format(date).uppercase()
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        onClick = { onClick(plan.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(116.dp),
            contentAlignment = Alignment.CenterStart
        ){
            Row(modifier = Modifier.fillMaxWidth()) {
                CustomAsyncImage(
                    image = plan.image,
                    modifier = Modifier
                        .size(116.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 8.dp, end = 8.dp)
                ) {
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.labelLarge,
                        color = PowderBlue
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = plan.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row( //LOCATION ROW
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CustomIcon(
                            modifier = Modifier.size(12.dp),
                            icon = R.drawable.outline_location_on_24,
                            color = PowderBlue
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = plan.location,
                            style = MaterialTheme.typography.labelMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = PowderBlue
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "by ${plan.host.username}",
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = PowderBlue
                    )
                }
            }
            when(plan.type) {
                PlanType.PRIVATE ->
                    CustomIcon(
                        modifier = Modifier
                            .padding(8.dp)
                            .size(20.dp)
                            .align(Alignment.TopEnd),
                        icon = R.drawable.outline_shield_24,
                    )
                PlanType.PUBLIC ->
                    CustomIcon(
                        modifier = Modifier
                            .padding(8.dp)
                            .size(20.dp)
                            .align(Alignment.TopEnd),
                        icon = R.drawable.round_public_24,
                    )
                PlanType.SECRET -> TODO()
            }

            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.BottomEnd),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = plan.participants.size.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(2.dp))
                CustomIcon(
                    modifier = Modifier.size(15.dp),
                    icon = R.drawable.round_person_24,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }



}

@Preview
@Composable
fun PreviewPlanCard() {
    CasualAppTheme {
        PlanItem(
            plan = Plan(
                title = "A tomar unas cañas",
                datetime = Timestamp(Date(999999999999)),
                location = "Patton Pub",
                host = UserPreview(username = "marianocp7"),
                type = PlanType.PUBLIC,
                participants = listOf("1","2")
            ),
            onClick = {}
        )
    }
}