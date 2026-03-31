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

class ImageRepository(private val context: Context) {

    suspend fun get(id:Int): Post?{
        try {
            val session = SessionManager(context);
            val token=session.getToken()
            val apiUrl = BuildConfig.BACKEND_API_URL+BuildConfig.API_VERSION+"/posts/get?postId=$id"
            if (token==null|| token=="") {
                return null
            }
            val result = withContext(Dispatchers.IO) {
                API.callApi(apiUrl, token, "GET", "")
            }
            println(result)
            val gson= Gson()
            val response= gson.fromJson(result, Post::class.java)
            return response
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null;
    }/*
    suspend fun create(commentText:String,postId:Int):Boolean{
        try {
            val session = SessionManager(context);
            val token=session.getToken()
            val apiUrl = BuildConfig.BACKEND_API_URL+BuildConfig.API_VERSION+"/comments/create"//+"/api/v1/login"
            val requestBody = mapOf(
                "content" to commentText,
                "postId" to postId,
            )
            println(apiUrl);
            if (token==null|| token=="") {
                return false
            }
            println(token)
            val result = withContext(Dispatchers.IO) {
                API.callApi(apiUrl, token, "POST", requestBody)
            }
            val gson= Gson()
            val response= gson.fromJson(result, CommentCreateResponse::class.java)
            if (response.error==false) {
                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false;
    }*/
}


