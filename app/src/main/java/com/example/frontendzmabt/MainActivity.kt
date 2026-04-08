package com.example.frontendzmabt


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.example.frontendzmabt.data.SessionManager
import com.example.frontendzmabt.data.SocketManager
import com.example.frontendzmabt.ui.screens.NavigationManager


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme{
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    val context = LocalContext.current

                    LaunchedEffect(Unit) {
                        val session = SessionManager(context)
                        val token = session.getToken()

                        if (token != null) {
                            SocketManager.init(token)
                        }
                    }

                    NavigationManager()
                }
            }
            /*FrontendZMABTTheme {
                FrontendZMABTApp()
            }*/
        }
    }
}

enum class InitScreens() {
    LoginScreen,
    RegisterScreen

}



