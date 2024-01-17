package com.miguelol.casualapp.presentation.navigation

import androidx.navigation.NavHostController
import com.miguelol.casualapp.presentation.navigation.DestinationArgs.PLAN_ID
import com.miguelol.casualapp.presentation.navigation.DestinationArgs.UID
import com.miguelol.casualapp.presentation.navigation.Destinations.PLANS_ROUTE
import com.miguelol.casualapp.presentation.navigation.Screens.CREATE_PLAN_SCREEN
import com.miguelol.casualapp.presentation.navigation.Screens.EDIT_PROFILE_SCREEN
import com.miguelol.casualapp.presentation.navigation.Screens.LOGIN_SCREEN
import com.miguelol.casualapp.presentation.navigation.Screens.MY_PLANS_SCREEN
import com.miguelol.casualapp.presentation.navigation.Screens.REQUESTS_SCREEN
import com.miguelol.casualapp.presentation.navigation.Screens.PLANS_SCREEN
import com.miguelol.casualapp.presentation.navigation.Screens.PLAN_PROFILE_SCREEN
import com.miguelol.casualapp.presentation.navigation.Screens.PROFILE_SCREEN
import com.miguelol.casualapp.presentation.navigation.Screens.SEARCH_FRIENDS_SCREEN
import com.miguelol.casualapp.presentation.navigation.Screens.SEARCH_USERS_SCREEN

object Screens {
    const val PROFILE_SCREEN = "profile"
    const val SEARCH_FRIENDS_SCREEN = "search_friends"
    const val SEARCH_USERS_SCREEN = "search_users"
    const val EDIT_PROFILE_SCREEN = "edit_profile"
    const val PLANS_SCREEN = "plans"
    const val CREATE_PLAN_SCREEN = "create_plan"
    const val MY_PLANS_SCREEN = "my_plans"
    const val REQUESTS_SCREEN = "requests"
    const val LOGIN_SCREEN = "login"
    const val PLAN_PROFILE_SCREEN = "plan_profile"
}

object DestinationArgs {
    const val UID = "uid"
    const val PLAN_ID = "planId"
    const val USERNAME = "username"
    const val FIRST_TIME = "firstTime"
}

object Destinations {
    const val LOGIN_ROUTE = LOGIN_SCREEN
    const val SEARCH_FRIENDS_ROUTE = "$SEARCH_FRIENDS_SCREEN?$UID={$UID}"
    const val SEARCH_USERS_ROUTE = SEARCH_USERS_SCREEN
    const val MY_PROFILE_ROUTE = PROFILE_SCREEN
    const val USER_PROFILE_ROUTE = "$PROFILE_SCREEN/{$UID}"
    const val EDIT_PROFILE_ROUTE = "$EDIT_PROFILE_SCREEN?$UID={$UID}"
    const val PLANS_ROUTE = PLANS_SCREEN
    const val CREATE_PLAN_ROUTE = CREATE_PLAN_SCREEN
    const val MY_PLANS_ROUTE = MY_PLANS_SCREEN
    const val REQUESTS_ROUTE = REQUESTS_SCREEN
    const val PLAN_PROFILE_ROUTE = "$PLAN_PROFILE_SCREEN/{$PLAN_ID}"
}

class NavigationActions(private val navController: NavHostController) {

    fun navigateBack() {
        navController.popBackStack()
    }

    fun navigateToProfile(uid: String?, fromNavBar: Boolean = false) {
        val destination = if (uid == null) PROFILE_SCREEN else "$PROFILE_SCREEN/$uid"


        navController.navigate(destination) {

        }

        navController.navigate("$PROFILE_SCREEN/$uid") {
            popUpTo(navController.graph.startDestinationId) { inclusive = true }
            launchSingleTop = true
        }
    }

    fun navigateToPlans() {
        navController.popBackStack(navController.graph.startDestinationId, true)
        navController.navigate(PLANS_ROUTE) { launchSingleTop = true }
    }
}
