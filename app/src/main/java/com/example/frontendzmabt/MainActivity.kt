package com.example.frontendzmabt


import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.example.frontendzmabt.data.API
import com.example.frontendzmabt.data.SessionManager
import com.example.frontendzmabt.data.SocketManager
import com.example.frontendzmabt.services.createNotificationChannel
import com.example.frontendzmabt.ui.screens.NavigationManager
import com.example.frontendzmabt.ui.theme.FrontendZMABTTheme
import com.example.frontendzmabt.ui.theme.ThemeManager
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    private val requiredPermissions = buildList {
        // Notifications (API 33+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(android.Manifest.permission.POST_NOTIFICATIONS)
            add(android.Manifest.permission.READ_MEDIA_IMAGES)
            add(android.Manifest.permission.READ_MEDIA_VIDEO)
            add(android.Manifest.permission.READ_MEDIA_AUDIO)
        } else {
            add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        add(android.Manifest.permission.CAMERA)
        add(android.Manifest.permission.RECORD_AUDIO)
        add(android.Manifest.permission.ACCESS_FINE_LOCATION)
        add(android.Manifest.permission.ACCESS_COARSE_LOCATION)
    }.toTypedArray()

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            results.forEach { (permission, granted) ->
                println("Permission $permission: ${if (granted) "GRANTED" else "DENIED"}")
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val missing = requiredPermissions.filter {
            checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (missing.isNotEmpty()) {
            permissionLauncher.launch(missing)
        }

        enableEdgeToEdge()
        setContent {
            FrontendZMABTTheme(darkTheme = ThemeManager.isDarkMode) {
                val context = LocalContext.current
                // At the top of onCreate, before setContent
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(
                            arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                            1001
                        )
                    }
                }

                LaunchedEffect(Unit) {
                    val session = SessionManager(context)
                    val authToken = session.getToken()

                    if (authToken != null) {
                        createNotificationChannel(context)
                        SocketManager.init(authToken)
                        println("AUTH TOKEN FOUND ______ BEFORE FIREBASE MESSAGING ")

                        FirebaseMessaging.getInstance().token.addOnSuccessListener { fcmToken ->
                            println("FCM TOKEN: $fcmToken")
                            println("Sending token to backend: $fcmToken")

                            CoroutineScope(Dispatchers.IO).launch {
                                var result=API.callApi(
                                    BuildConfig.BACKEND_API_URL + "/fcm-token",
                                    authToken,
                                    "POST",
                                    mapOf("token" to fcmToken)
                                )
                                println("FCM OKTEN SEND"+result)
                            }
                        }
                    }
                }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val placeIdFromNotification = intent?.getIntExtra("placeId", -1)
                    println(placeIdFromNotification)
                    NavigationManager(placeIdFromNotification)
                }
            }            /*FrontendZMABTTheme {
                FrontendZMABTApp()
            }*/
        }
    }
}

enum class InitScreens() {
    LoginScreen,
    RegisterScreen

}



