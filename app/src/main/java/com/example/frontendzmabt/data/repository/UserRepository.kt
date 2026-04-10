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

data class GetUserResponse(
    val user: User,
    val isFollowing:Boolean
)


class UserRepository(private val context: Context) {

    suspend fun get(id:Int):GetUserResponse?{
        try {
            val session = SessionManager(context);
            val token=session.getToken()
            val apiUrl = BuildConfig.BACKEND_API_URL+BuildConfig.API_VERSION+"/account/get?userId=$id"
            if (token==null|| token=="") {
                return null
            }
            val result = withContext(Dispatchers.IO) {
                API.callApi(apiUrl, token, "GET", "")
            }
            println(result)
            val gson= Gson()
            val response= gson.fromJson(result, GetUserResponse::class.java)

            println(response)
            return response
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null;
    }
    suspend fun ChangeFollowStatus(
        changeFollowStatus: Boolean,
        userId:Int
    ): Boolean {
        try {
            val session = SessionManager(context)
            val token = session.getToken()

            if (token.isNullOrEmpty()) return false
            var url="";
            if (!changeFollowStatus) {
                url = "${BuildConfig.BACKEND_API_URL+BuildConfig.API_VERSION}/account/follow"
            }else{
                url = "${BuildConfig.BACKEND_API_URL+BuildConfig.API_VERSION}/account/unfollow"
            }


            val requestBody = mapOf(
                "userId" to userId,
            )
            if (token==null|| token=="") {
                return false
            }
            val result = withContext(Dispatchers.IO) {
                API.callApi(url, token, "POST", requestBody)
            }
            println(result)
            val gson= Gson()
            val response= gson.fromJson(result, GeneralResponse::class.java)
            if (response.error==false) {
                return true
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }
}


