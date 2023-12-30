package com.miguelol.casualapp.presentation.screens.searchusers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.miguelol.casualapp.R
import com.miguelol.casualapp.domain.model.User
import com.miguelol.casualapp.presentation.components.CustomProgressIndicator
import com.miguelol.casualapp.presentation.screens.components.CustomEmptyIcon
import com.miguelol.casualapp.presentation.screens.components.CustomIcon
import com.miguelol.casualapp.presentation.screens.components.UserItem
import com.miguelol.casualapp.presentation.theme.CasualAppTheme
import com.miguelol.casualapp.presentation.theme.PowderBlue

@Composable
fun SearchUsersContent(
    modifier: Modifier = Modifier,
    uiState: SearchUsersUiState,
    onNavigateToProfile: (String) -> Unit,
    onNavigateToMyProfile: (String) -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when {
            uiState.searchText.isBlank() -> {
                CustomEmptyIcon(
                    icon = R.drawable.round_search_24,
                    text = "Find users by username!"
                )
            }

            uiState.thereAreNoMatches -> {
                CustomEmptyIcon(
                    icon = R.drawable.round_person_24,
                    text = "No user matches"
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxSize()
                ) {
                    items(items = uiState.users) { friend ->
                        UserItem(
                            user = friend,
                            onClick = if (friend.uid == uiState.myUid)
                                onNavigateToMyProfile
                            else
                                onNavigateToProfile
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewSearchUsersContent() {
    CasualAppTheme {
        SearchUsersContent(
            uiState = SearchUsersUiState(
                users = listOf(
                    User(username = "miguelol99", name = "Miquel Nunez"),
                    User(username = "marianocp7", name = "Mariano Conde"),
                    User(username = "pablogdr", name = "Pablo Garcia"),
                ),
                searchText = "eey",
                thereAreNoMatches = false
            ),
            onNavigateToProfile = {},
            onNavigateToMyProfile = {}
        )
    }
}