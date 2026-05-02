package com.example.frontendzmabt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.frontendzmabt.data.SessionManager
import com.example.frontendzmabt.data.SocketManager
import com.example.frontendzmabt.ui.screens.NavigationManager
import com.example.frontendzmabt.ui.theme.FrontendZMABTTheme
import com.example.frontendzmabt.ui.theme.LocalDarkMode
import com.example.frontendzmabt.ui.theme.LocalSetDarkMode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val prefs = getSharedPreferences("app_settings", MODE_PRIVATE)

        setContent {
            val darkMode = remember { mutableStateOf(prefs.getBoolean("dark_mode", false)) }

            CompositionLocalProvider(
                LocalDarkMode provides darkMode.value,
                LocalSetDarkMode provides { enabled ->
                    darkMode.value = enabled
                    prefs.edit().putBoolean("dark_mode", enabled).apply()
                }
            ) {
                FrontendZMABTTheme(darkTheme = darkMode.value) {
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
            }
        }
    }
}

enum class InitScreens {
    LoginScreen,
    RegisterScreen
}
