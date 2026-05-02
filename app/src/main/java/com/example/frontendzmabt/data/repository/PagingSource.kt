package com.example.frontendzmabt.data.repository

import android.content.Context
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.frontendzmabt.BuildConfig
import com.example.frontendzmabt.data.API
import com.example.frontendzmabt.data.AppDatabase
import com.example.frontendzmabt.data.SessionManager
import com.example.frontendzmabt.data.model.Post
import com.example.frontendzmabt.data.model.PostNoUser
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PostPagingSource(
    private val context: Context,
    private val id: Int,
    private val placeId: Int,
    private val isUser: Boolean
) : PagingSource<Int, Post>() {

    private val db = AppDatabase.getInstance(context)

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Post> {
        return try {
            val page = params.key ?: 1
            val session = SessionManager(context)
            val token = session.getToken()

            val apiUrl = when {
                isUser -> BuildConfig.BACKEND_API_URL + BuildConfig.API_VERSION + "/posts/getPageUser?page=$page"
                id > 0 -> BuildConfig.BACKEND_API_URL + BuildConfig.API_VERSION + "/posts/getPage?page=$page&userId=$id"
                placeId > 0 -> BuildConfig.BACKEND_API_URL + BuildConfig.API_VERSION + "/posts/getPagePlace?page=$page&placeId=$placeId"
                else -> BuildConfig.BACKEND_API_URL + BuildConfig.API_VERSION + "/posts/getPageFyp?page=$page"
            }

            val result = withContext(Dispatchers.IO) {
                API.callApi(apiUrl, token, "GET", null)
            }

            val gson = Gson()
            val type = object : TypeToken<PaginatedResponse<Post>>() {}.type
            val response: PaginatedResponse<Post> = gson.fromJson(result, type)

            withContext(Dispatchers.IO) {
                if (page == 1) db.postDao().deleteAll()
                db.postDao().upsertAll(response.data.map {
                    PostNoUser(
                        id = it.id,
                        userId = it.userId,
                        placeId = it.placeId,
                        description = it.description,
                        createdAt = it.createdAt,
                        updatedAt = it.updatedAt,
                        stars = it.stars
                    )
                })
            }
            LoadResult.Page(
                data = response.data,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (page < response.meta.lastPage) page + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Post>): Int? {
        return state.anchorPosition
    }
}
class CommentPagingSource(
    private val context: Context,
    private val id:Int,
) : PagingSource<Int, Comment>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Comment> {
        return try {
            val page = params.key ?: 1
            /*val session = SessionManager(context)
            val token = session.getToken()

            if (token.isNullOrEmpty()) {
                return LoadResult.Page(emptyList(), null, null)
            }*/
            var apiUrl="";
            if ( this.id > 0){
                apiUrl = BuildConfig.BACKEND_API_URL+BuildConfig.API_VERSION + "/comments/getPage?page=$page&postId=$id"
            }

            val result = withContext(Dispatchers.IO) {
                API.callApi(apiUrl, null, "GET", null)
            }
            println(result)

            val gson = Gson()

            val type = object : TypeToken<PaginatedResponse<Comment>>() {}.type
            val response: PaginatedResponse<Comment> =
                gson.fromJson(result, type)

            LoadResult.Page(
                data = response.data,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (page < response.meta.lastPage) page + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Comment>): Int? {
        return state.anchorPosition
    }
}
