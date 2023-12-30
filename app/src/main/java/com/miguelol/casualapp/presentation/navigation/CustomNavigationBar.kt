package com.miguelol.casualapp.presentation.navigation

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.miguelol.casualapp.R
import com.miguelol.casualapp.presentation.navigation.Screens.MY_PLANS_SCREEN
import com.miguelol.casualapp.presentation.navigation.Screens.REQUESTS_SCREEN
import com.miguelol.casualapp.presentation.navigation.Screens.PLANS_SCREEN
import com.miguelol.casualapp.presentation.navigation.Screens.PROFILE_SCREEN
import com.miguelol.casualapp.presentation.screens.components.CustomIcon
import com.miguelol.casualapp.presentation.screens.components.CustomText
import com.miguelol.casualapp.presentation.theme.CasualAppTheme

data class CustomNavigationItem(
    val route: String,
    val title: String,
    val selectedIcon: Int,
    val unselectedIcon: Int
)

val items = listOf(
    CustomNavigationItem(
        route = PLANS_SCREEN,
        title = "Discover",
        selectedIcon = R.drawable.round_groups_24,
        unselectedIcon = R.drawable.outline_groups_24
    ),
    CustomNavigationItem(
        route = MY_PLANS_SCREEN,
        title = "My Plans",
        selectedIcon = R.drawable.round_favorite_24,
        unselectedIcon = R.drawable.baseline_favorite_border_24
    ),
    CustomNavigationItem(
        route = REQUESTS_SCREEN,
        title = "Notifications",
        selectedIcon = R.drawable.round_circle_notifications_24,
        unselectedIcon = R.drawable.outline_circle_notifications_24
    ),
    CustomNavigationItem(
        route = PROFILE_SCREEN,
        title = "Profile",
        selectedIcon = R.drawable.baseline_account_circle_24,
        unselectedIcon = R.drawable.outline_account_circle_24
    )
)

@Composable
fun CustomNavigationBar(
    navController: NavController
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        tonalElevation = 0.dp,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        items.forEach { item ->
            val isSelected: Boolean = currentRoute == item.route
            NavigationBarItem(
                selected = isSelected,
                icon = {
                    when(isSelected) {
                        true -> CustomIcon(icon = item.selectedIcon, contentDescription = item.title)
                        false -> CustomIcon(icon = item.unselectedIcon, contentDescription = item.title)
                    }
                },
                label = { CustomText(text = item.title) },
                onClick = {
                    //if (!isSelected) {
                        // navController.popBackStack(currentRoute!!, true)
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    //}

                }
            )
        }
    }
}

@Preview
@Composable
fun PreviewCustomNavigationBar() {
    CasualAppTheme() {
        CustomNavigationBar(navController = rememberNavController())
    }
}