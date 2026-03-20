package com.example.frontendzmabt.ui.screens.auth
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.frontendzmabt.ui.screens.main.HomeScreen


sealed class Screen(val route: String) {
    object LoginScreen: Screen("login_screen")
    object HomeScreen: Screen("main")
    object RegisterScreen: Screen("register_screen")
}


@Composable
fun AuthNavigationManager() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination ="auth") {
        navigation(startDestination = Screen.LoginScreen.route, route = "auth") {
            composable(route =Screen.LoginScreen.route) {
                LoginScreen(navController);
            }
            composable(
                route =Screen.RegisterScreen.route ,
                /*arguments = listOf(
                    navArgument("text") {
                        type = NavType.StringType
                        nullable = true
                    }
                )*/
            ) {
                RegisterScreen()
            }
            composable(route =Screen.LoginScreen.route) {
                LoginScreen(navController);
            }
        }

        navigation(startDestination = "home", route = "main") {
            composable("home") {
                HomeScreen(navController)
            }
        }
    }
}