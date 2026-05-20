package com.example.frontendzmabt.data.repository.testRepository


import com.example.frontendzmabt.BuildConfig
import kotlinx.coroutines.Dispatchers
import android.content.Context
import android.net.Uri
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class TestPostRepository : PostRepositoryInterface{

    private val fakePosts = List(1000) { index ->
        Post(
            id = index,
            placeId = 1,
            description = "Post $index",
            createdAt = "0",
            updatedAt = null,
            stars = 5,
            userId = index
        )
    }
    override fun getCachedPosts(id: Int, placeId: Int, isUser: Boolean): Flow<List<PostNoUser>> {
        return flowOf(
            listOf(
                PostNoUser(
                    id = 1,
                    placeId = placeId,
                    description = "Test Cached Post 1",
                    createdAt = "0",
                    updatedAt = null,
                    stars = 5,
                    userId=1
                ),
                PostNoUser(
                    id = 2,
                    placeId = placeId,
                    description = "Test Cached Post 2",
                    createdAt = "0",
                    updatedAt = null,
                    stars = 4,
                    userId=1
                )
            )
        )
    }

    override suspend fun get(id: Int): GetPostResponse? {
        return GetPostResponse(
            post=Post(
                    id = 1,
                    placeId = 1,
                    description = "Test Cached Post 1",
                    createdAt = "0",
                    updatedAt = null,
                    stars = 5,
                    userId=1),
            postImages = emptyList()
            //error = false,
            //post = fakePosts.first()
        )
    }

    override  suspend fun rate(rating: Int, postId: Int): Boolean {
        return true
    }

    override suspend fun delete(postId: Int): Boolean {
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

    override fun getPostsPager(id: Int, placeId: Int, isUser: Boolean): Flow<PagingData<Post>> {
        return flowOf(
            PagingData.from(fakePosts)
        )
    }

    fun uriToRequestBody(context: Context, uri: Uri): RequestBody? {
        return null
    }
}

