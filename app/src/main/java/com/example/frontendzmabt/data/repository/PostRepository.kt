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

data class PostCreateResponse(
    val error: Boolean,
    val message:String
)
data class PaginatedResponse<T>(
    val data: List<T>,
    val meta: Meta
)

data class Meta(
    val total: Int,
    val perPage: Int,
    val currentPage: Int,
    val lastPage: Int
)


data class PostBody(
    val id: Int,
    val userId: Int,
    val placeId: Int,
    val description: String,
    val createdAt: String,
    val updatedAt: String?,
    val stars: Int
)class PostRepository(private val context: Context) {

    suspend fun create(postText:String, rating:Int,longitude:Double,latitude:Double):Boolean{
        try {
            val session = SessionManager(context);
            val token=session.getToken()
            val apiUrl = BuildConfig.BACKEND_API_URL+"/posts/create"//+"/api/v1/login"
            val requestBody = mapOf(
                "postText" to postText,
                "rating" to rating,
                "longitude" to longitude,
                "latitude" to latitude,
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
            val response= gson.fromJson(result, PostCreateResponse::class.java)
            if (response.error==false) {
                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false;
    }

    fun getPostsPager(id:Int,isUser:Boolean): Flow<PagingData<PostBody>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { PostPagingSource(context,id,isUser) }
        ).flow
    }
}


