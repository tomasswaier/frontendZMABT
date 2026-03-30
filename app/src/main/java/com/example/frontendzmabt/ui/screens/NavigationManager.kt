package com.example.frontendzmabt.ui.screens
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.frontendzmabt.R
import com.example.frontendzmabt.data.SessionManager
import com.example.frontendzmabt.data.User
import com.example.frontendzmabt.ui.screens.auth.LoginScreen
import com.example.frontendzmabt.ui.screens.auth.RegisterScreen
import com.example.frontendzmabt.ui.screens.main.HomeScreen
import com.example.frontendzmabt.ui.screens.main.MapScreen
import com.example.frontendzmabt.ui.screens.main.ProfileScreen
import com.example.frontendzmabt.ui.screens.main.PostCreateScreen
import com.example.frontendzmabt.ui.screens.main.PostScreen


data class ProfileNavArgs(
    val userId: Int,
)
fun ProfileNavArgs.toRoute(): String {
    return "profile_screen?userId=$userId"
}
data class PostNavArgs(
    val postId: Int,
    val isUser: Boolean
)
fun PostNavArgs.toRoute(): String {
    return "post_screen?postId=$postId&isUser=$isUser"
}


sealed class Screen(val route: String) {
    object LoginScreen: Screen("login_screen")
    object RegisterScreen: Screen("register_screen")
    object HomeScreen: Screen("home_screen")
    object ProfileScreen: Screen("profile_screen?userId={userId}")
    object UserProfileScreen: Screen("user_profile_screen")
    object MapScreen: Screen("map_screen")
    object PostCreateScreen: Screen("post_create_screen")
    object PostScreen: Screen("post_screen?postId={postId}&isUser={isUser}")

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
            composable(route =Screen.PostCreateScreen.route) {
                PostCreateScreen(navController)
            }
            composable(
                route = Screen.PostScreen.route,
                arguments = listOf(
                    navArgument("postId") { type = NavType.IntType },
                    navArgument("isUser") { type = NavType.BoolType}
                )
            ) { backStackEntry ->

                val postId= backStackEntry.arguments?.getInt("postId") ?: 0
                val isUser= backStackEntry.arguments?.getBoolean("isUser") ?: false

                PostScreen(navController, postId,isUser)
            }

        }
    }
    val context = LocalContext.current
    val session = SessionManager(context)

    var user by remember { mutableStateOf<User?>(null) }
    LaunchedEffect(Unit) {
        user = session.getUser()
    }
    if(user!=null && user?.id!=null) {
        navController.navigate(Screen.HomeScreen.route)
    }

}