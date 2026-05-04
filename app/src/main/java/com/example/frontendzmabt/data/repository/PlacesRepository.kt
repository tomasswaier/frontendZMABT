package com.example.frontendzmabt.data.repository



import com.example.frontendzmabt.BuildConfig
import kotlinx.coroutines.Dispatchers
import android.content.Context
import com.example.frontendzmabt.data.API
import com.example.frontendzmabt.data.SessionManager
import com.google.gson.Gson
import kotlinx.coroutines.withContext

data class Place(
    val id: Int,
    val aiDescription: String,
    val longitude: Float,
    val latitude: Float
)
data class GetPlaceResponse(
    val place: Place?,
)
class PlacesRepository(private val context: Context) {
    suspend fun getInfo(context:Context,id:Int): Place?{
        try {
            val session = SessionManager(context)
            val token = session.getToken()

            if (token.isNullOrEmpty()) return null
            println("placeId:"+id)
            val url = "${BuildConfig.BACKEND_API_URL+BuildConfig.API_VERSION}/place/get?placeId=$id"
            val method="GET";

            val result = withContext(Dispatchers.IO) {
                API.callApi(url, token, method, "")
            }
            println(result)
            val gson= Gson()
            val response= gson.fromJson(result, GetPlaceResponse::class.java)
            //println(result)
            return response.place

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }
    suspend fun get(context:Context): List<Place>?{
        try {
            val session = SessionManager(context)
            var url="";
            var method="";
            url = "${BuildConfig.BACKEND_API_URL+BuildConfig.API_VERSION}/place/getAll"
            method="GET";

            val result = withContext(Dispatchers.IO) {
                API.callApi(url, null, method, "")
            }
            val gson= Gson()
            val type = object : com.google.gson.reflect.TypeToken<List<Place>>() {}.type
            val response: List<Place> = gson.fromJson(result, type)
            //println(result)
            return response

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }
}


