package com.miguelol.casualapp.presentation.screens.searchfriends

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.miguelol.casualapp.R
import com.miguelol.casualapp.domain.model.UserPreview
import com.miguelol.casualapp.presentation.components.CustomProgressIndicator
import com.miguelol.casualapp.presentation.components.CustomSearchField
import com.miguelol.casualapp.presentation.components.CustomTopBar
import com.miguelol.casualapp.presentation.screens.components.CustomEmptyIcon
import com.miguelol.casualapp.presentation.screens.components.UserPreviewItem
import com.miguelol.casualapp.presentation.theme.CasualAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchFriendsScreen(
    uiState: SearchFriendsUiState,
    onEvent: (SearchFriendsEvents) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToProfile: (String) -> Unit,
    onNavigateToMyProfile: (String) -> Unit
) {

    Scaffold(
        topBar = {
            CustomTopBar(
                navigateBack = true,
                onNavigateBack = onNavigateBack,
                actions = {
                    CustomSearchField(
                        modifier = Modifier
                            .fillMaxWidth(0.80f)
                            .padding(end = 4.dp),
                        value = uiState.searchText,
                        icon = R.drawable.round_search_24,
                        placeholder = "Search friends",
                        onValueChange = {
                            onEvent(SearchFriendsEvents.OnSearchTextInput(it))
                        }
                    )
                }
            )
        }
    ) { paddingValues ->

            when {
                uiState.isLoading -> CustomProgressIndicator()
                uiState.searchText.isBlank() && uiState.friends.isEmpty() ->
                    CustomEmptyIcon(
                        icon = R.drawable.round_person_add_alt_1_24,
                        text = "You have no friends!"
                    )
                uiState.thereAreNoMatches ->
                    CustomEmptyIcon(
                        icon = R.drawable.round_person_24,
                        text = "No user matches"
                    )
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .padding(paddingValues)
                            .padding(top = 16.dp)
                            .fillMaxSize()
                    ) {
                        items(items = uiState.friends) { friend ->
                            UserPreviewItem(
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
fun PreviewSearchScreen() {
    CasualAppTheme {
        SearchFriendsScreen(
            uiState = SearchFriendsUiState(
                friends = listOf(
                    UserPreview(username = "miguelol99", name = "Miquel Nunez"),
                    UserPreview(username = "marianocp7", name = "Mariano Conde"),
                    UserPreview(username = "pablogdr", name = "Pablo Garcia"),
                )
            ),
            onEvent = {},
            onNavigateBack = {},
            onNavigateToProfile = {},
            onNavigateToMyProfile = {}
        )
    }
}