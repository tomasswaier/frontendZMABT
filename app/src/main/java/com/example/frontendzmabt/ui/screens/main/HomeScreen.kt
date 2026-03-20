package com.example.frontendzmabt.ui.screens.main

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.frontendzmabt.BuildConfig
import com.example.frontendzmabt.data.API
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Greeting(
            name = "Bugga",
            modifier = Modifier.padding(innerPadding)
        )
        TestApiButton()
    }
}
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}
@Composable
fun TestApiButton() {
    val context = LocalContext.current

    // Coroutine scope tied to the composable
    val scope = rememberCoroutineScope()

    Button(onClick = {
        scope.launch(Dispatchers.IO) {
            try {
                val apiUrl = BuildConfig.BACKEND_API_URL
                val token = "testValue"
                val requestBody = mapOf(
                    "test" to "fungujem?",
                    "meow" to "meow"
                )

                // Make network request on IO thread
                val result = API.callApi(apiUrl, token, "POST", requestBody)

                // Switch to Main thread to show a Toast or update UI
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Result: $result", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }) {
        Text("CLICK ME")
    }
}

