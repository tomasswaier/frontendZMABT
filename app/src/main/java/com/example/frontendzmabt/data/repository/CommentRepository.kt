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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

data class Comment(
    val id: Int,
    val userId: Int,
    val parentCommentId: Int,
    val content: String,
    val createdAt: String,
    val updatedAt: String?,
    val likeCount: Int,
    val isLiked: Boolean?,


)
interface CommentRepositoryInterface {

    fun getCommentPager(id: Int): Flow<PagingData<Comment>>

    fun create(commentText: String, postId: Int): Boolean

    suspend fun ChangeLikeStatus(
        context: Context,
        action: Boolean,
        commentId: Int
    ): Boolean
}
class CommentRepository(private val context: Context): CommentRepositoryInterface {
    override fun getCommentPager(id:Int): Flow<PagingData<Comment>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { CommentPagingSource(context,id) }
        ).flow
    }
    override fun create(commentText:String,postId:Int):Boolean{
        try {
            //val session = SessionManager(context);
            SocketManager.sendComment(postId = postId,commentText=commentText);
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false;
    }
    override suspend fun ChangeLikeStatus(context:Context,action:Boolean,commentId:Int):Boolean{
        try {
            val session = SessionManager(context)
            val token = session.getToken()

            if (token.isNullOrEmpty()) return false
            var url="";
            var method="";
            if (action) {
                url = "${BuildConfig.BACKEND_API_URL+BuildConfig.API_VERSION}/comments/like"
                method="PUT";
            }else{
                url = "${BuildConfig.BACKEND_API_URL+BuildConfig.API_VERSION}/comments/removeLike?commentId=$commentId"
                method="DELETE";
            }


            val requestBody = mapOf(
                "commentId" to commentId,
            )
            println("url;"+url+" method:"+method+" commendId:"+commentId+" action:"+action)
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


