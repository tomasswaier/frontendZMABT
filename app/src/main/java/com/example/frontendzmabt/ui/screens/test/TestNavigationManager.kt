package com.example.frontendzmabt.ui.screens.test

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.navArgument
import com.example.frontendzmabt.ui.screens.main.HomeScreen
import com.example.frontendzmabt.ui.screens.main.MapScreen
import com.example.frontendzmabt.ui.screens.main.PlaceScreen
import com.example.frontendzmabt.ui.screens.main.PostCreateScreen
import com.example.frontendzmabt.ui.screens.main.PostScreen
import com.example.frontendzmabt.ui.screens.main.ProfileScreen
import com.example.frontendzmabt.ui.screens.auth.LoginScreen
import com.example.frontendzmabt.ui.screens.auth.RegisterScreen

@Composable
fun TestGetNavHost(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        navigation(
            startDestination = "login_screen",
            route = "auth"
        ) {
            composable("login_screen") {
                LoginScreen(navController)
            }

            composable("register_screen") {
                RegisterScreen(navController)
            }
        }

        navigation(
            startDestination = "home_screen",
            route = "main"
        ) {
            composable("home_screen") {
                HomeScreen(navController)
            }

            composable(
                "place_screen?placeId={placeId}",
                arguments = listOf(navArgument("placeId") { type = NavType.IntType })
            ) { backStackEntry ->
                val placeId = backStackEntry.arguments?.getInt("placeId") ?: 0
                PlaceScreen(navController, placeId)
            }

            composable(
                "post_edit_screen?postId={postId}",
                arguments = listOf(navArgument("postId") { type = NavType.IntType })
            ) { backStackEntry ->
                val postId = backStackEntry.arguments?.getInt("postId") ?: 0
                PostCreateScreen(navController, postId)
            }

            composable(
                "profile_screen?userId={userId}",
                arguments = listOf(navArgument("userId") { type = NavType.IntType })
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getInt("userId") ?: 0
                ProfileScreen(navController, userId, false)
            }

            composable("user_profile_screen") {
                ProfileScreen(navController, 0, true)
            }

            composable("map_screen") {
                MapScreen(navController)
            }

            composable("post_create_screen") {
                PostCreateScreen(navController, 0)
            }

            composable(
                "post_screen?postId={postId}&isUser={isUser}",
                arguments = listOf(
                    navArgument("postId") { type = NavType.IntType },
                    navArgument("isUser") { type = NavType.BoolType }
                )
            ) { backStackEntry ->
                val postId = backStackEntry.arguments?.getInt("postId") ?: 0
                val isUser = backStackEntry.arguments?.getBoolean("isUser") ?: false
                PostScreen(navController, postId, isUser)
            }
        }
    }
}