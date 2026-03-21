package com.example.frontendzmabt.data.repository

import com.example.frontendzmabt.BuildConfig
import kotlinx.coroutines.Dispatchers
import android.content.Context
import android.widget.Toast
import com.example.frontendzmabt.data.API
import com.example.frontendzmabt.data.SessionManager
import com.google.gson.Gson
import kotlinx.coroutines.withContext
import com.example.frontendzmabt.data.User

data class LoginResponse(val data: LoginData)
data class LoginData(
    val token: String,
    val user: User
)

class AuthRepository(private val context: Context) {

    suspend fun logIn(username:String, password:String):Boolean{
        println("Username: $username")
        println("Password: $password")
        try {
            val apiUrl = BuildConfig.BACKEND_API_URL+"/auth/login"//+"/api/v1/login"
            val requestBody = mapOf(
                "username" to username,
                "password" to password
            )
            println(apiUrl);

            // Make network request on IO thread
            val result = withContext(Dispatchers.IO) {
                API.callApi(apiUrl, "", "POST", requestBody)
            }
            val gson= Gson()
            val response= gson.fromJson(result,LoginResponse::class.java)
            val session= SessionManager(context);
            println(response);
            session.saveToken(

                response.data.token,
                response.data.user.username,
                response.data.user.email,
                response.data.user.id
            )

            return true
            //println(session.getToken())
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
        return false;

    }
    suspend fun register(username:String,email:String,password:String,passwordConfirmation:String):Boolean {
        println("Username: $username")
        println("email: $email")
        println("Password: $password")
        println("Password: $passwordConfirmation")
        try {
            val apiUrl = BuildConfig.BACKEND_API_URL+"/auth/signup"
            val requestBody = mapOf(
                "username" to username,
                "password" to password,
                "passwordConfirmation" to passwordConfirmation,
                "email" to email,
            )
            val result = withContext(Dispatchers.IO) {
                API.callApi(apiUrl, "", "POST", requestBody)
            }

            val gson= Gson()
            val response= gson.fromJson(result,LoginResponse::class.java)
            val session= SessionManager(context);
            session.saveToken(
                response.data.token,
                response.data.user.username,
                response.data.user.email,
                        response.data.user.id
            )
            println(result);
            return true
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
        return false



    }
}