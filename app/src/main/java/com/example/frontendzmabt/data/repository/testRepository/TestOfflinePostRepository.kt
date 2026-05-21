package com.example.frontendzmabt.data.repository.testRepository


import com.example.frontendzmabt.BuildConfig
import kotlinx.coroutines.Dispatchers
import android.content.Context
import android.net.Uri
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.frontendzmabt.data.API
import com.example.frontendzmabt.data.AppDatabase
import com.example.frontendzmabt.data.SessionManager
import com.example.frontendzmabt.data.model.CachedPosts
import com.example.frontendzmabt.data.model.Post
import com.example.frontendzmabt.data.model.PostNoUser
import com.example.frontendzmabt.data.model.PostUser
import com.example.frontendzmabt.data.model.User
import com.example.frontendzmabt.data.repository.GeneralResponse
import com.example.frontendzmabt.data.repository.GetPostResponse
import com.example.frontendzmabt.data.repository.PostImage
import com.example.frontendzmabt.data.repository.PostPagingSource
import com.example.frontendzmabt.data.repository.PostRepository
import com.example.frontendzmabt.data.repository.PostRepositoryInterface
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class TestOfflinePostRepository : PostRepositoryInterface {

    private val fakePosts = List(1000) { index ->
        PostNoUser(
            id = index,
            placeId = 1,
            description = "Cached post $index",
            createdAt = "0",
            updatedAt = null,
            stars = 5,
            userId = index
        )
    }

    override fun getPostsPager(
        id: Int,
        placeId: Int,
        isUser: Boolean
    ): Flow<PagingData<Post>> {

        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = {

                object : PagingSource<Int, Post>() {

                    override suspend fun load(
                        params: LoadParams<Int>
                    ): LoadResult<Int, Post> {

                        return LoadResult.Error(
                            Exception("No internet")
                        )
                    }

                    override fun getRefreshKey(
                        state: PagingState<Int, Post>
                    ): Int? = null
                }
            }
        ).flow
    }
    override  suspend fun rate(rating: Int, postId: Int): Boolean {
        return true
    }

    override suspend fun delete(postId: Int): Boolean {
        return true
    }

    override fun getCachedPosts(
        id: Int,
        placeId: Int,
        isUser: Boolean
    ): Flow<List<PostNoUser>> {

        return flowOf(
            fakePosts
        )
    }

    override suspend fun get(id: Int): GetPostResponse? {
        return null
    }

    override suspend fun create(
        postText: String,
        rating: Int,
        longitude: Double,
        latitude: Double,
        imageUri: Uri?,
        online: Boolean
    ): Boolean {
        return true
    }

    override suspend fun edit(
        postText: String,
        rating: Int,
        longitude: Double,
        latitude: Double,
        postId: Int
    ): Boolean {
        return true
    }
}
