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
    val isFollowing: Boolean
)

private data class ProfileResponse(val data: ProfileData)
private data class ProfileData(val user: User, val isFollowing: Boolean = false)


class UserRepository(private val context: Context) {

    suspend fun get(id: Int): GetUserResponse? {
        return try {
            val token = SessionManager(context).getToken() ?: return null
            val url = "${BuildConfig.BACKEND_API_URL}${BuildConfig.API_VERSION}/account/profile?userId=$id"
            val result = withContext(Dispatchers.IO) { API.callApi(url, token, "GET", "") }
            val direct = Gson().fromJson(result, GetUserResponse::class.java)
            val parsed = if (direct?.user != null) direct
                         else Gson().fromJson(result, ProfileResponse::class.java)?.data
                             ?.let { GetUserResponse(user = it.user, isFollowing = it.isFollowing) }
            if (parsed?.user != null) parsed else null
        } catch (e: Exception) { e.printStackTrace(); null }
    }

    suspend fun getOwnProfile(): GetUserResponse? {
        return try {
            val token = SessionManager(context).getToken() ?: return null
            val url = "${BuildConfig.BACKEND_API_URL}${BuildConfig.API_VERSION}/account/profile"
            val result = withContext(Dispatchers.IO) { API.callApi(url, token, "GET", "") }
            val data = Gson().fromJson(result, ProfileResponse::class.java)?.data ?: return null
            GetUserResponse(user = data.user, isFollowing = data.isFollowing)
        } catch (e: Exception) { e.printStackTrace(); null }
    }

    suspend fun updateBio(bio: String): Boolean {
        return try {
            val session = SessionManager(context)
            val token = session.getToken()
            if (token.isNullOrEmpty()) return false
            val url = "${BuildConfig.BACKEND_API_URL}${BuildConfig.API_VERSION}/account/updateBio"
            val result = withContext(Dispatchers.IO) {
                API.callApi(url, token, "PATCH", mapOf("bio" to bio))
            }
            val response = Gson().fromJson(result, GeneralResponse::class.java)
            response.error == false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun ChangeFollowStatus(
        changeFollowStatus: Boolean,
        userId: Int
    ): Boolean {
        try {
            val session = SessionManager(context)
            val token = session.getToken()
            if (token.isNullOrEmpty()) return false

            val url = if (!changeFollowStatus)
                "${BuildConfig.BACKEND_API_URL}${BuildConfig.API_VERSION}/account/follow"
            else
                "${BuildConfig.BACKEND_API_URL}${BuildConfig.API_VERSION}/account/unfollow"

            val result = withContext(Dispatchers.IO) {
                API.callApi(url, token, "POST", mapOf("userId" to userId))
            }
            val response = Gson().fromJson(result, GeneralResponse::class.java)
            return response.error == false
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }
}
