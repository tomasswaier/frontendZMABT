package com.example.frontendzmabt.data.repository

import android.content.Context
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.frontendzmabt.BuildConfig
import com.example.frontendzmabt.data.API
import com.example.frontendzmabt.data.SessionManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PostPagingSource(
    private val context: Context,
    private val id:Int,
    private val isUser:Boolean
) : PagingSource<Int, PostBody>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PostBody> {
        return try {
            val page = params.key ?: 1
            val session = SessionManager(context)
            val token = session.getToken()

            if (token.isNullOrEmpty()) {
                return LoadResult.Page(emptyList(), null, null)
            }
            var apiUrl="";
            if (this.isUser) {
                apiUrl = BuildConfig.BACKEND_API_URL + "/posts/getUser?page=$page"
            }else if( this.id > 0){
                apiUrl = BuildConfig.BACKEND_API_URL + "/posts/get?page=$page"
            }else{
                apiUrl = BuildConfig.BACKEND_API_URL + "/posts/getFyp?page=$page"
            }

            val result = withContext(Dispatchers.IO) {
                API.callApi(apiUrl, token, "GET", null)
            }
            println(result)

            val gson = Gson()

            val type = object : TypeToken<PaginatedResponse<PostBody>>() {}.type
            val response: PaginatedResponse<PostBody> =
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

    override fun getRefreshKey(state: PagingState<Int, PostBody>): Int? {
        return state.anchorPosition
    }
}
