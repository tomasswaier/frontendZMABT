package com.example.frontendzmabt.data.repository

import com.example.frontendzmabt.BuildConfig
import kotlinx.coroutines.Dispatchers
import android.content.Context
import android.widget.Toast
import com.example.frontendzmabt.data.API
import com.example.frontendzmabt.data.SessionManager
import com.example.frontendzmabt.data.SocketManager
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import com.example.frontendzmabt.data.User

data class LoginResponse(val data: LoginData)
data class LogOutResponse(val data: LoginData)
data class LoginData(
    val token: String,
    val user: User
)

class AuthRepository(private val context: Context) {
    suspend fun signInWithGoogle(idToken: String): Boolean {
        return try {
            val apiUrl = BuildConfig.BACKEND_API_URL + BuildConfig.API_VERSION + "/auth/google"
            val requestBody = mapOf("idToken" to idToken)
            val result = withContext(Dispatchers.IO) {
                API.callApi(apiUrl, "", "POST", requestBody)
            }
            android.util.Log.d("GoogleSignIn", "backend raw response: $result")
            val response = Gson().fromJson(result, LoginResponse::class.java)
            SessionManager(context).saveToken(
                response.data.token,
                response.data.user.username,
                response.data.user.email,
                response.data.user.id
            )
            SocketManager.init(response.data.token)
            try {
                val fcmToken = FirebaseMessaging.getInstance().token.await()
                saveFcmToken(fcmToken)
            } catch (e: Exception) {
                android.util.Log.w("AuthRepository", "FCM token registration failed", e)
            }
            true
        } catch (e: Exception) {
            android.util.Log.e("GoogleSignIn", "backend call failed: ${e.message}", e)
            false
        }
    }

    suspend fun saveFcmToken(token: String): Boolean {
        android.util.Log.d("FCM_TOKEN", "token: $token")
        return try {
            val sessionToken = SessionManager(context).getToken() ?: return false
            val apiUrl = BuildConfig.BACKEND_API_URL + BuildConfig.API_VERSION + "/account/fcm-token"
            withContext(Dispatchers.IO) {
                API.callApi(apiUrl, sessionToken, "PATCH", mapOf("fcmToken" to token))
            }
            true
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "saveFcmToken failed", e)
            false
        }
    }

    suspend fun logout(): Boolean {
        return try {
            val sessionToken = SessionManager(context).getToken() ?: ""
            val apiUrl = BuildConfig.BACKEND_API_URL + BuildConfig.API_VERSION + "/auth/logout"
            withContext(Dispatchers.IO) {
                API.callApi(apiUrl, sessionToken, "POST", "")
            }
            SessionManager(context).logout()
            true
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "logout failed", e)
            false
        }
    }

    suspend fun logIn(username: String, password: String): Boolean {
        if (!validateLogin(username, password)) return false
        return try {
            val apiUrl = BuildConfig.BACKEND_API_URL + BuildConfig.API_VERSION + "/auth/login"
            val requestBody = mapOf(
                "username" to username,
                "password" to password
            )
            val result = withContext(Dispatchers.IO) {
                API.callApi(apiUrl, "", "POST", requestBody)
            }
            val response = Gson().fromJson(result, LoginResponse::class.java)
            SessionManager(context).saveToken(
                response.data.token,
                response.data.user.username,
                response.data.user.email,
                response.data.user.id
            )
            SocketManager.init(response.data.token)
            try {
                val fcmToken = FirebaseMessaging.getInstance().token.await()
                saveFcmToken(fcmToken)
            } catch (e: Exception) {
                android.util.Log.w("AuthRepository", "FCM token registration failed", e)
            }
            true
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "logIn failed", e)
            false
        }
    }

    suspend fun register(username: String, email: String, password: String, passwordConfirmation: String): Boolean {
        if (!validateRegister(username, password, passwordConfirmation, email)) return false
        return try {
            val apiUrl = BuildConfig.BACKEND_API_URL + BuildConfig.API_VERSION + "/auth/signup"
            val requestBody = mapOf(
                "username" to username,
                "password" to password,
                "passwordConfirmation" to passwordConfirmation,
                "email" to email,
            )
            val result = withContext(Dispatchers.IO) {
                API.callApi(apiUrl, "", "POST", requestBody)
            }
            if (!result.trimStart().startsWith("{")) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, result, Toast.LENGTH_LONG).show()
                }
                return false
            }
            val response = Gson().fromJson(result, LoginResponse::class.java)
            SessionManager(context).saveToken(
                response.data.token,
                response.data.user.username,
                response.data.user.email,
                response.data.user.id
            )
            SocketManager.init(response.data.token)
            true
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "register failed: ${e.message}", e)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
            false
        }
    }
}
fun validateRegister(username:String,password:String,passwordConfirmation: String,email: String):Boolean {
    if (username.isEmpty() || username.length<2){
        return false
    }
    if (password.isEmpty() || password.length<2 ||password!=passwordConfirmation){
        return false
    }
    return true
}
fun validateLogin(username:String,password:String):Boolean {
    if (username.isEmpty() || username.length<2){
        return false
    }
    if (password.isEmpty() || password.length<2){
        return false
    }
    return true
}
