package com.example.frontendzmabt.data.repository


import com.example.frontendzmabt.BuildConfig
import kotlinx.coroutines.Dispatchers
import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.frontendzmabt.data.API
import com.example.frontendzmabt.data.SessionManager
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

import com.example.frontendzmabt.data.User

class UserRepository(private val context: Context) {

    suspend fun get(id:Int):User?{
        try {
            val session = SessionManager(context);
            val token=session.getToken()
            val apiUrl = BuildConfig.BACKEND_API_URL+"/account/profile?userId=$id"
            if (token==null|| token=="") {
                return null
            }
            val result = withContext(Dispatchers.IO) {
                API.callApi(apiUrl, token, "GET", "")
            }
            println(result)
            val gson= Gson()
            val response= gson.fromJson(result, User::class.java)
            return response
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null;
    }
}


