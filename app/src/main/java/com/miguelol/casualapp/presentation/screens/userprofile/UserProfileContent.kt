package com.miguelol.casualapp.presentation.screens.userprofile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.miguelol.casualapp.R
import com.miguelol.casualapp.domain.model.RequestState
import com.miguelol.casualapp.domain.model.User
import com.miguelol.casualapp.presentation.screens.components.CustomAsyncImage
import com.miguelol.casualapp.presentation.screens.components.CustomIcon
import com.miguelol.casualapp.presentation.theme.CasualAppTheme
import com.miguelol.casualapp.presentation.theme.PowderBlue

@Composable
fun UserProfileContent(
    modifier: Modifier,
    uiState: UserProfileUiState,
    onEvent: (UserProfileEvents) -> Unit,
    onNavigateToFriendList: (String) -> Unit,
) {

    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = modifier
            .weight(1f)
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp)
            .verticalScroll(rememberScrollState())
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp),
                contentAlignment = Alignment.Center

            ) {
                CustomAsyncImage(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp)),
                    image = uiState.user.image,
                    progressIndicatorColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 2.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = "${uiState.user.name}, ${uiState.user.age}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onNavigateToFriendList(uiState.user.uid) },
                        colors = ButtonDefaults.buttonColors(containerColor = PowderBlue)
                    ) {
                        Text(text = uiState.user.friendCount.toString())
                        Spacer(modifier = Modifier.width(2.dp))
                        CustomIcon(
                            modifier = Modifier.size(24.dp),
                            icon = R.drawable.round_person_24,
                            color = MaterialTheme.colorScheme.background
                        )
                    }
                }

                Text(text = "@${uiState.user.username}", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = uiState.user.description)
                Spacer(modifier = Modifier.weight(1f))
            }
        }
        Column(modifier = Modifier.padding(16.dp)) {
            when(uiState.friendState) {
                RequestState.ACCEPTED ->
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onEvent(UserProfileEvents.OnFollowButtonPressed(uiState.friendState))},
                        colors = ButtonDefaults.buttonColors(containerColor = PowderBlue)
                    ) { Text("Followed") }

                RequestState.PENDING ->
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onEvent(UserProfileEvents.OnFollowButtonPressed(uiState.friendState))},
                        colors = ButtonDefaults.buttonColors(containerColor = PowderBlue)
                    ) { Text("Pending") }
                RequestState.NOT_SENT ->
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onEvent(UserProfileEvents.OnFollowButtonPressed(uiState.friendState))}
                    ) { Text("Follow") }
            }
        }
    }

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewProfileContent() {
    CasualAppTheme {
        UserProfileContent(
            modifier = Modifier,
            uiState = UserProfileUiState(
                friendState = RequestState.ACCEPTED,
                user = User(
                    name = "Miguel Antonio",
                    username = "miguelol_99",
                    age = "25",
                    description = "La composición de Navigation también admite argumentos de navegación" +
                            "opcionales.",
                    image = ""
                )
            ),
            onEvent = {},
            onNavigateToFriendList = {}
        )
    }
}