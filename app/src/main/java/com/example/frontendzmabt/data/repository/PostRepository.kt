package com.example.frontendzmabt.data.repository


import com.example.frontendzmabt.BuildConfig
import kotlinx.coroutines.Dispatchers
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.frontendzmabt.data.API
import com.example.frontendzmabt.data.SessionManager
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

data class GeneralResponse(
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

data class GetPostResponse(
    val post:Post,
    val postImages: List<PostImage>
)

data class PostUser(
    val id: Int,
    val username: String
)

data class Post(
    val id: Int,
    val userId: Int,
    val placeId: Int,
    val description: String,
    val createdAt: String,
    val updatedAt: String?,
    val stars: Int,
    val user: PostUser? = null
)
data class PostImage(
    val id:Int,
    val postId:Int,
    val imagePath:String,

    )
class PostRepository(private val context: Context) {

    suspend fun get(id: Int): GetPostResponse? {
        return try {
            val token = SessionManager(context).getToken() ?: ""
            val apiUrl = BuildConfig.BACKEND_API_URL + BuildConfig.API_VERSION + "/posts/get?postId=$id"
            val result = withContext(Dispatchers.IO) { API.callApi(apiUrl, token, "GET", "") }
            Gson().fromJson(result, GetPostResponse::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun rate(
        rating: Int,
        postId:Int
    ): Boolean {
        try {
            val session = SessionManager(context)
            val token = session.getToken()

            if (token.isNullOrEmpty()) return false

            val url = "${BuildConfig.BACKEND_API_URL+BuildConfig.API_VERSION}/ratings/set"

            val requestBody = mapOf(
                "postId" to postId,
                "stars" to rating,
            )
            val result = withContext(Dispatchers.IO) {
                API.callApi(url, token, "POST", requestBody)
            }
            val response = Gson().fromJson(result, GeneralResponse::class.java)
            if (response.error == false) {
                return true
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }

    suspend fun create(
        postText: String,
        rating: Int,
        longitude: Double,
        latitude: Double,
        imageUri: Uri?
    ): Boolean {
        try {
            val session = SessionManager(context)
            val token = session.getToken()

            if (token.isNullOrEmpty()) return false

            val url = "${BuildConfig.BACKEND_API_URL+BuildConfig.API_VERSION}/posts/create"

            val client = OkHttpClient()

            val multipartBuilder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("postText", postText)
                .addFormDataPart("rating", rating.toString())
                .addFormDataPart("longitude", longitude.toString())
                .addFormDataPart("latitude", latitude.toString())

            if (imageUri != null) {
                val imageRequestBody = uriToRequestBody(context, imageUri)
                if (imageRequestBody != null) {
                    multipartBuilder.addFormDataPart("image", "upload.jpg", imageRequestBody)
                }
            }

            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $token")
                .post(multipartBuilder.build())
                .build()

            val response = withContext(Dispatchers.IO) { client.newCall(request).execute() }
            val responseBody = response.body?.string()
            val parsed = Gson().fromJson(responseBody, GeneralResponse::class.java)

            return parsed.error == false

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }
    suspend fun delete(postId: Int): Boolean {
        return try {
            val token = SessionManager(context).getToken() ?: return false
            val url = "${BuildConfig.BACKEND_API_URL}${BuildConfig.API_VERSION}/posts/delete?postId=$postId"
            val result = withContext(Dispatchers.IO) {
                API.callApi(url, token, "DELETE", "")
            }
            Gson().fromJson(result, GeneralResponse::class.java)?.error == false
        } catch (e: Exception) { e.printStackTrace(); false }
    }

    suspend fun update(postId: Int, postText: String, rating: Int): Boolean {
        return try {
            val token = SessionManager(context).getToken() ?: return false
            val url = "${BuildConfig.BACKEND_API_URL}${BuildConfig.API_VERSION}/posts/update"
            val result = withContext(Dispatchers.IO) {
                API.callApi(url, token, "PATCH", mapOf(
                    "postId" to postId,
                    "postText" to postText,
                    "rating" to rating
                ))
            }
            Gson().fromJson(result, GeneralResponse::class.java)?.error == false
        } catch (e: Exception) { e.printStackTrace(); false }
    }

    fun getPlacePostsPager(placeId: Int): Flow<PagingData<Post>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { PlacePagingSource(context, placeId) }
        ).flow
    }

    fun getPostsPager(id:Int,isUser:Boolean): Flow<PagingData<Post>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { PostPagingSource(context,id,isUser) }
        ).flow
    }
    fun uriToRequestBody(context: Context, uri: Uri): RequestBody? {
        val inputStream = context.contentResolver.openInputStream(uri)

        val bytes = inputStream?.readBytes()

        return bytes?.toRequestBody("image/*".toMediaTypeOrNull())
    }
}


