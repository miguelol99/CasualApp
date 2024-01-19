package com.miguelol.casualapp.presentation.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.miguelol.casualapp.presentation.navigation.DestinationArgs.FIRST_TIME
import com.miguelol.casualapp.presentation.navigation.DestinationArgs.PLAN_ID
import com.miguelol.casualapp.presentation.navigation.DestinationArgs.UID
import com.miguelol.casualapp.presentation.navigation.Destinations.CHAT_ROUTE
import com.miguelol.casualapp.presentation.navigation.Destinations.CREATE_PLAN_ROUTE
import com.miguelol.casualapp.presentation.navigation.Destinations.EDIT_PROFILE_ROUTE
import com.miguelol.casualapp.presentation.navigation.Destinations.LOGIN_ROUTE
import com.miguelol.casualapp.presentation.navigation.Destinations.MY_PLANS_ROUTE
import com.miguelol.casualapp.presentation.navigation.Destinations.REQUESTS_ROUTE
import com.miguelol.casualapp.presentation.navigation.Destinations.PLANS_ROUTE
import com.miguelol.casualapp.presentation.navigation.Destinations.MY_PROFILE_ROUTE
import com.miguelol.casualapp.presentation.navigation.Destinations.PLAN_PROFILE_ROUTE
import com.miguelol.casualapp.presentation.navigation.Destinations.SEARCH_FRIENDS_ROUTE
import com.miguelol.casualapp.presentation.navigation.Destinations.SEARCH_USERS_ROUTE
import com.miguelol.casualapp.presentation.navigation.Destinations.USER_PROFILE_ROUTE
import com.miguelol.casualapp.presentation.navigation.Screens.CHAT_SCREEN
import com.miguelol.casualapp.presentation.navigation.Screens.EDIT_PROFILE_SCREEN
import com.miguelol.casualapp.presentation.navigation.Screens.PLAN_PROFILE_SCREEN
import com.miguelol.casualapp.presentation.navigation.Screens.PROFILE_SCREEN
import com.miguelol.casualapp.presentation.navigation.Screens.SEARCH_FRIENDS_SCREEN
import com.miguelol.casualapp.presentation.screens.chat.ChatScreen
import com.miguelol.casualapp.presentation.screens.chat.ChatViewModel
import com.miguelol.casualapp.presentation.screens.createplan.CreatePlanScreen
import com.miguelol.casualapp.presentation.screens.createplan.CreatePlanViewModel
import com.miguelol.casualapp.presentation.screens.editprofile.EditProfileScreen
import com.miguelol.casualapp.presentation.screens.editprofile.EditProfileViewModel
import com.miguelol.casualapp.presentation.screens.login.LoginScreen
import com.miguelol.casualapp.presentation.screens.login.LoginViewModel
import com.miguelol.casualapp.presentation.screens.myplans.MyPlansScreen
import com.miguelol.casualapp.presentation.screens.myplans.MyPlansViewModel
import com.miguelol.casualapp.presentation.screens.requests.RequestsScreen
import com.miguelol.casualapp.presentation.screens.requests.RequestsViewModel
import com.miguelol.casualapp.presentation.screens.plans.PlansScreen
import com.miguelol.casualapp.presentation.screens.plans.PlansViewModel
import com.miguelol.casualapp.presentation.screens.myprofile.MyProfileScreen
import com.miguelol.casualapp.presentation.screens.myprofile.MyProfileViewModel
import com.miguelol.casualapp.presentation.screens.planprofile.PlanProfileScreen
import com.miguelol.casualapp.presentation.screens.planprofile.PlanProfileViewModel
import com.miguelol.casualapp.presentation.screens.searchfriends.SearchFriendsScreen
import com.miguelol.casualapp.presentation.screens.searchfriends.SearchFriendsUiState
import com.miguelol.casualapp.presentation.screens.searchfriends.SearchFriendsViewModel
import com.miguelol.casualapp.presentation.screens.searchusers.SearchUsersScreen
import com.miguelol.casualapp.presentation.screens.searchusers.SearchUsersViewModel
import com.miguelol.casualapp.presentation.screens.userprofile.UserProfileScreen
import com.miguelol.casualapp.presentation.screens.userprofile.UserProfileViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CasualNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startingDestination: String = LOGIN_ROUTE
) {

    //TODO CREATE MYPROFILE AND OTHER PROFILE
    val screens = listOf(
            MY_PROFILE_ROUTE,
            PLANS_ROUTE,
            MY_PLANS_ROUTE,
            REQUESTS_ROUTE,
    )

    val showBottomBar = navController.currentBackStackEntryAsState().value?.destination?.route in screens.map { it }

    Scaffold(
        bottomBar = {
            if (showBottomBar) CustomNavigationBar(navController = navController)
        }
    ) {

        NavHost(
            modifier = modifier.padding(it),
            navController = navController,
            startDestination = startingDestination
        ) {

            composable(route = LOGIN_ROUTE) {
                val viewModel: LoginViewModel = hiltViewModel()
                LoginScreen(
                    uiStateFlow = viewModel.uiState,
                    onEvent = viewModel::onEvent,
                    onNavigateToHome = {
                        navController.popBackStack(LOGIN_ROUTE, true)
                        navController.navigate(PLANS_ROUTE)
                    }
                )
            }

            composable(route = MY_PROFILE_ROUTE) {
                val viewModel: MyProfileViewModel = hiltViewModel()
                MyProfileScreen(
                    uiStateFlow = viewModel.uiState,
                    onEvent =  viewModel::onEvent,
                    onNavigateToEditProfile = {
                        navController.navigate(EDIT_PROFILE_SCREEN)
                    },
                    onNavigateToFriendList = { uid ->
                        navController.navigate("$SEARCH_FRIENDS_SCREEN?$UID=$uid")
                    },
                    onNavigateToAddNewFriend = {
                        navController.navigate(SEARCH_USERS_ROUTE)
                    }
                )
            }

            composable(
                route = USER_PROFILE_ROUTE,
                arguments = listOf(navArgument(UID) { type = NavType.StringType; })
            ) {
                val viewModel: UserProfileViewModel = hiltViewModel()
                UserProfileScreen(
                    uiStateFlow = viewModel.uiState,
                    onEvent =  viewModel::onEvent,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToFriendList = { uid ->
                        navController.navigate("$SEARCH_FRIENDS_SCREEN?$UID=$uid")
                    }
                )
            }

            composable(
                route = SEARCH_FRIENDS_ROUTE,
                arguments = listOf(navArgument(UID) { type = NavType.StringType; nullable = true})
            ) {
                val viewModel: SearchFriendsViewModel = hiltViewModel()
                val uiState: SearchFriendsUiState by viewModel.uiState.collectAsStateWithLifecycle()
                SearchFriendsScreen(
                    uiState = uiState,
                    onEvent = viewModel::onEvent,
                    onNavigateBack = {navController.popBackStack() },
                    onNavigateToProfile = { uid ->
                        navController.navigate("$PROFILE_SCREEN/$uid")
                    },
                    onNavigateToMyProfile = {
                        navController.navigate(PROFILE_SCREEN) {
                            popUpTo(navController.graph.findStartDestination().id)
                        }
                    }
                )
            }

            composable(route = SEARCH_USERS_ROUTE) {
                val viewModel: SearchUsersViewModel = hiltViewModel()
                SearchUsersScreen(
                    uiStateFlow = viewModel.uiState,
                    onEvent = viewModel::onEvent,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToUserProfile = { uid ->
                        navController.navigate("$PROFILE_SCREEN/$uid")
                    },
                    onNavigateToMyProfile = {
                        navController.navigate(PROFILE_SCREEN) {
                            popUpTo(navController.graph.findStartDestination().id)
                        }
                    }
                )
            }

            composable(
                route = EDIT_PROFILE_ROUTE,
                arguments = listOf(navArgument(FIRST_TIME) { type = NavType.BoolType; defaultValue = false })
            ) {
                val viewModel: EditProfileViewModel = hiltViewModel()
                EditProfileScreen(
                    uiStateFlow = viewModel.uiState,
                    onEvent = viewModel::onEvent,
                    onNavigateToProfileScreen = {
                        navController.popBackStack(EDIT_PROFILE_ROUTE, true)
                        navController.navigate(PROFILE_SCREEN)
                    },
                    onSignOut = {
                        navController.navigate(LOGIN_ROUTE){
                            popUpTo(navController.graph.findStartDestination().id)
                        }
                    }
                )
            }

            composable( route = PLANS_ROUTE) {
                val viewModel: PlansViewModel = hiltViewModel()
                PlansScreen(
                    uiStateFlow = viewModel.uiState,
                    onEvent = viewModel::onEvent,
                    onNavigateToPlanDetails =  { planId ->
                        navController.navigate("${PLAN_PROFILE_SCREEN}/$planId/${false}")
                    }
                )
            }

            composable( route = MY_PLANS_ROUTE) {
                val viewModel: MyPlansViewModel = hiltViewModel()
                MyPlansScreen(
                    uiStateFlow = viewModel.uiState,
                    onEvent = viewModel::onEvent,
                    onNavigateToCreatePlan = { navController.navigate(CREATE_PLAN_ROUTE) },
                    onNavigateToChat = { planId ->
                        navController.navigate("${CHAT_SCREEN}/$planId")
                    }
                )
            }

            composable( route = CREATE_PLAN_ROUTE) {
                val viewModel: CreatePlanViewModel = hiltViewModel()
                CreatePlanScreen(
                    uiStateFlow = viewModel.uiState,
                    onEvent = viewModel::onEvent,
                    onNavigateToMyPlansScreen = {
                        navController.popBackStack(CREATE_PLAN_ROUTE, true)
                        navController.navigate(MY_PLANS_ROUTE)
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(route = REQUESTS_ROUTE){
                val viewModel: RequestsViewModel = hiltViewModel()
                RequestsScreen(
                    uiStateFlow = viewModel.uiState,
                    onEvent = viewModel::onEvent,
                    onNavigateToProfile = { uid ->
                        navController.navigate("$PROFILE_SCREEN/$uid")
                    },
                    onNavigateToPlan = { planId ->
                        navController.navigate("${PLAN_PROFILE_SCREEN}/$planId/${false}")
                    }
                )
            }

            composable(
                route = PLAN_PROFILE_ROUTE,
                arguments = listOf(
                    navArgument(PLAN_ID) { type = NavType.StringType},
                    navArgument("fromChat") {type = NavType.BoolType; defaultValue = false }
                )

            ){
                val viewModel: PlanProfileViewModel = hiltViewModel()
                PlanProfileScreen(
                    uiStateFlow = viewModel.uiState,
                    onEvent = viewModel::onEvent,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToProfile = { uid ->
                        navController.navigate("$PROFILE_SCREEN/$uid")
                    },
                    onNavigateToMyProfile = {
                        navController.navigate(PROFILE_SCREEN) {
                            popUpTo(navController.graph.findStartDestination().id)
                        }
                    },
                    onNavigateToMyPlans = {
                        //navController.popBackStack(PLAN_PROFILE_ROUTE, true)
                        navController.navigate(MY_PLANS_ROUTE)
                    },
                    onNavigateToChat = {
                        navController.popBackStack(PLAN_PROFILE_ROUTE, true)
                        navController.navigate("${CHAT_SCREEN}/${it.arguments!!.getString(PLAN_ID)}")
                    }
                )
            }

            composable(
                route = CHAT_ROUTE,
                arguments = listOf(navArgument(PLAN_ID) { type = NavType.StringType })
            ) {
                val viewModel: ChatViewModel = hiltViewModel()
                ChatScreen(
                    uiStateFlow = viewModel.uiState,
                    onEvent = viewModel::onEvent,
                    onNavigateToPlanProfile = {
                        navController.navigate("${PLAN_PROFILE_SCREEN}/${it.arguments!!.getString(PLAN_ID)}/${true}")
                    },
                    onNavigateToMyPlans = {
                        navController.navigate(MY_PLANS_ROUTE)
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

        }
    }


}