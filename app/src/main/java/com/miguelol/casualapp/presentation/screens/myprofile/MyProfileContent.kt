package com.miguelol.casualapp.presentation.screens.myprofile

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.miguelol.casualapp.domain.model.FriendState
import com.miguelol.casualapp.domain.model.User
import com.miguelol.casualapp.presentation.screens.components.CustomAsyncImage
import com.miguelol.casualapp.presentation.screens.components.CustomIcon
import com.miguelol.casualapp.presentation.theme.CasualAppTheme

@Composable
fun ProfileContent(
    modifier: Modifier,
    uiState: MyProfileUiState,
    onNavigateToFriendList: (String) -> Unit,
) {

    Column(modifier = modifier
        .fillMaxSize()
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
                OutlinedButton(
                    onClick = { onNavigateToFriendList(uiState.user.uid) }
                ) {
                    Text(text = uiState.user.friendCount.toString(), color = MaterialTheme.colorScheme.onBackground)
                    Spacer(modifier = Modifier.width(2.dp))
                    CustomIcon(
                        modifier = Modifier.size(24.dp),
                        icon = R.drawable.round_person_24,
                    )
                }
            }

            Text(text = "@${uiState.user.username}", style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = uiState.user.description)
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewProfileContent() {
    CasualAppTheme {
        ProfileContent(
            modifier = Modifier,
            uiState = MyProfileUiState(
                user = User(
                    name = "Miguel Antonio",
                    username = "miguelol_99",
                    age = "25",
                    description = "La composición de Navigation también admite argumentos de navegación" +
                            "opcionales.",
                    image = ""
                )
            ),
            onNavigateToFriendList = {}
        )
    }
}