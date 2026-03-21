package com.example.frontendzmabt.ui.screens
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.frontendzmabt.R
import com.example.frontendzmabt.ui.screens.auth.LoginScreen
import com.example.frontendzmabt.ui.screens.auth.RegisterScreen
import com.example.frontendzmabt.ui.screens.main.HomeScreen
import com.example.frontendzmabt.ui.screens.main.MapScreen
import com.example.frontendzmabt.ui.screens.main.ProfileScreen


sealed class Screen(val route: String) {
    object LoginScreen: Screen("login_screen")
    object RegisterScreen: Screen("register_screen")
    object HomeScreen: Screen("home_screen")
    object ProfileScreen: Screen("profile_screen/{userId}")
    object UserProfileScreen: Screen("user_profile_screen")
    object MapScreen: Screen("map_screen")
}
enum class AppNavigation(var label:String,val route:String,val icon:Int,){
    Profile("Profile",Screen.UserProfileScreen.route,R.drawable.ic_account_box),
    HOME("Home",Screen.HomeScreen.route,R.drawable.ic_home),
    MAP("Map",Screen.MapScreen.route,R.drawable.ic_launcher_background),
    //Home("Home",R.drawable.ic_home),

}


@Composable
fun NavigationManager() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination ="auth") {
        navigation(startDestination = Screen.LoginScreen.route, route = "auth") {
            composable(route =Screen.LoginScreen.route) {
                LoginScreen(navController);
            }
            composable(
                route =Screen.RegisterScreen.route ,
            ) {
                RegisterScreen(navController)
            }
            composable(route =Screen.LoginScreen.route) {
                LoginScreen(navController);
            }
        }

        navigation(startDestination = Screen.HomeScreen.route, route = "main") {
            composable(route =Screen.HomeScreen.route) {
                HomeScreen(navController)
            }
            composable(
                route = Screen.ProfileScreen.route,
                arguments = listOf(
                    navArgument("userId") { type = NavType.IntType }
                )
            ) { backStackEntry ->

                val userId = backStackEntry.arguments?.getInt("userId") ?: 0

                ProfileScreen(navController, userId,false)
            }
            composable(
                route = Screen.UserProfileScreen.route,
            ) {
                    ProfileScreen(navController, 0,true)

            }
            composable(route =Screen.MapScreen.route) {
                MapScreen(navController)
            }

        }
    }
}