package com.example.frontendzmabt.data.repository



import com.example.frontendzmabt.BuildConfig
import kotlinx.coroutines.Dispatchers
import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.frontendzmabt.data.API
import com.example.frontendzmabt.data.SessionManager
import com.example.frontendzmabt.data.SocketManager
import com.google.gson.Gson
import io.socket.client.IO.socket
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

data class CommentCreateResponse(
    val error: Boolean,
    val message:String
)
/*
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

*/
data class Comment(
    val id: Int,
    val userId: Int,
    val parentCommentId: Int,
    val content: String,
    val createdAt: String,
    val updatedAt: String?,
    //val stars: Int
)
class CommentRepository(private val context: Context) {
    /*
    suspend fun get(id:Int):Post?{
        try {
            val session = SessionManager(context);
            val token=session.getToken()
            val apiUrl = BuildConfig.BACKEND_API_URL+"/posts/get?postId=$id"
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
    }*/

    fun getCommentPager(id:Int): Flow<PagingData<Comment>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { CommentPagingSource(context,id) }
        ).flow
    }
    suspend fun create(commentText:String,postId:Int):Boolean{
        try {
            val session = SessionManager(context);
            /*
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
            */
            SocketManager.sendComment(postId = postId,commentText=commentText);
            /*val gson= Gson()
            val response= gson.fromJson(result, CommentCreateResponse::class.java)
            if (response.error==false) {
                return true
            }*/
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false;
    }
}


