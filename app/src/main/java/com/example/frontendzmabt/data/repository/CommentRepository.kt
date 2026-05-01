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
    val likeCount: Int,
    val isLiked: Boolean?,

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
    suspend fun create(commentText: String, postId: Int, commentId: Int? = null): Boolean {
        return try {
            SocketManager.sendComment(postId = postId, commentText = commentText, commentId = commentId)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getReplies(postId: Int, commentId: Int): List<Comment> {
        return try {
            val token = SessionManager(context).getToken() ?: return emptyList()
            val url = "${BuildConfig.BACKEND_API_URL}${BuildConfig.API_VERSION}/comments/getPage?page=1&postId=$postId&commentId=$commentId"
            val result = withContext(Dispatchers.IO) { API.callApi(url, token, "GET", null) }
            val type = object : com.google.gson.reflect.TypeToken<PaginatedResponse<Comment>>() {}.type
            val response: PaginatedResponse<Comment> = Gson().fromJson(result, type)
            response.data
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    suspend fun ChangeLikeStatus(context:Context,action:Boolean,commentId:Int):Boolean{
        try {
            val session = SessionManager(context)
            val token = session.getToken()

            if (token.isNullOrEmpty()) return false
            var url="";
            var method="";
            if (!action) {
                url = "${BuildConfig.BACKEND_API_URL+BuildConfig.API_VERSION}/comments/like"
                method="PUT";
            }else{
                url = "${BuildConfig.BACKEND_API_URL+BuildConfig.API_VERSION}/comments/removeLike?commentId=$commentId"
                method="DELETE";
            }


            val requestBody = mapOf(
                "commentId" to commentId,
            )
            if (token==null|| token=="") {
                return false
            }
            val result = withContext(Dispatchers.IO) {
                API.callApi(url, token, method, requestBody)
            }
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


