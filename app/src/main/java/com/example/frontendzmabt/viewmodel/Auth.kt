package com.example.frontendzmabt.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import com.example.frontendzmabt.BuildConfig
import com.example.frontendzmabt.data.API
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Data classes for the request and response
data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val token: String)
fun register(username:String,email:String,password:String,scope: CoroutineScope,context: Context) {
    println("Username: $username")
    println("email: $email")
    println("Password: $password")
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


}
fun logIn(username:String,password:String,scope: CoroutineScope,context: Context){
    println("Username: $username")
    println("Password: $password")
    scope.launch(Dispatchers.IO) {
        try {
            val apiUrl = BuildConfig.BACKEND_API_URL+"/login"//+"/api/v1/login"
            val token = "testValue"
            val requestBody = mapOf(
                "test" to "fungujem?",
                "meow" to "meow"
            )
            println(apiUrl);

            // Make network request on IO thread
            val result = API.callApi(apiUrl, token, "POST", requestBody)

            // Switch to Main thread to show a Toast or update UI
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Result: $result", Toast.LENGTH_LONG).show()
                println(result);
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

}