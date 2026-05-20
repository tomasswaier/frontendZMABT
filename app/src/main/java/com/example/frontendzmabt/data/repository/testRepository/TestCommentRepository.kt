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
import com.example.frontendzmabt.data.repository.Comment
import com.example.frontendzmabt.data.repository.CommentRepositoryInterface
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

class TestCommentRepository : CommentRepositoryInterface {

    private val fakeComments = listOf(
        Comment(
            id = 1,
            userId = 1,
            createdAt = "0",
            updatedAt = null,
            parentCommentId = 0,
            content = "Test Comment 1",
            likeCount = 1,
            isLiked = true,
        ),
        Comment(
            id = 2,
            userId = 2,
            content = "Test Comment 2",
            createdAt = "0",
            updatedAt = null,
            parentCommentId = 0,
            likeCount = 0,
            isLiked = false,
        )
    )

    override fun getCommentPager(id: Int): Flow<PagingData<Comment>> {

        return flowOf(
            PagingData.from(fakeComments)
        )
    }

    override fun create(
        commentText: String,
        postId: Int
    ): Boolean {

        return true
    }

    override suspend fun ChangeLikeStatus(
        context: Context,
        action: Boolean,
        commentId: Int
    ): Boolean {

        return true
    }
}
