package com.example.frontendzmabt.data.repository


import com.example.frontendzmabt.BuildConfig
import kotlinx.coroutines.Dispatchers
import android.content.Context
import android.net.Uri
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

data class GetPostResponse(
    val post:Post,
    val postImages: List<PostImage>
)

data class Post(
    val id: Int,
    val userId: Int,
    val placeId: Int,
    val description: String,
    val createdAt: String,
    val updatedAt: String?,
    val stars: Int
)
data class PostImage(
    val id:Int,
    val postId:Int,
    val imagePath:String,

    )
class PostRepository(private val context: Context) {

    suspend fun get(id:Int): GetPostResponse?{
        try {
            val session = SessionManager(context);
            val token=session.getToken()
            val apiUrl = BuildConfig.BACKEND_API_URL+BuildConfig.API_VERSION+"/posts/get?postId=$id"
            if (token==null|| token=="") {
                return null
            }
            val result = withContext(Dispatchers.IO) {
                API.callApi(apiUrl, token, "GET", "")
            }
            println(result)
            val gson= Gson()
            val response= gson.fromJson(result, GetPostResponse::class.java)
            return response
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null;
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

            var requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("postText", postText)
                .addFormDataPart("rating", rating.toString())
                .addFormDataPart("longitude", longitude.toString())
                .addFormDataPart("latitude", latitude.toString())

            if(imageUri!=null){
                val imageRequestBody = uriToRequestBody(context, imageUri)

                val imagePart = MultipartBody.Part.createFormData(
                    "image",
                    "upload.jpg",
                    imageRequestBody
                )
                requestBody
                    .addFormDataPart(
                        "image",
                        "upload.jpg",
                        imageRequestBody
                    )
            }
            val xd=requestBody.build()

            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $token")
                .post(xd)
                .build()

            val response = withContext(Dispatchers.IO) {
                client.newCall(request).execute()
            }

            val responseBody = response.body?.string()

            println(responseBody)

            val gson = Gson()
            val parsed = gson.fromJson(responseBody, PostCreateResponse::class.java)

            return parsed.error == false

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }
    fun getPostsPager(id:Int,isUser:Boolean): Flow<PagingData<Post>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { PostPagingSource(context,id,isUser) }
        ).flow
    }
    fun uriToRequestBody(context: Context, uri: Uri): RequestBody {
        val inputStream = context.contentResolver.openInputStream(uri)!!
        val bytes = inputStream.readBytes()
        return bytes.toRequestBody("image/*".toMediaTypeOrNull())
    }
}


