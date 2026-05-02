package com.example.frontendzmabt.data.repository

import android.content.Context
import com.example.frontendzmabt.BuildConfig
import com.example.frontendzmabt.data.API
import com.example.frontendzmabt.data.SessionManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class Place(
    val id: Int,
    val latitude: Double,
    val longitude: Double,
    val aiDescription: String?,
    val createdAt: String
)

data class PlaceInfoResponse(
    val place: Place?,
    val rating: Double?
)

class PlaceRepository(private val context: Context) {

    suspend fun getAll(): List<Place> {
        return try {
            val token = SessionManager(context).getToken() ?: ""
            val url = "${BuildConfig.BACKEND_API_URL}${BuildConfig.API_VERSION}/places"
            val result = withContext(Dispatchers.IO) { API.callApi(url, token, "GET", null) }
            val type = object : TypeToken<List<Place>>() {}.type
            Gson().fromJson(result, type) ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getInfo(placeId: Int): PlaceInfoResponse? {
        return try {
            val token = SessionManager(context).getToken() ?: return null
            val url = "${BuildConfig.BACKEND_API_URL}${BuildConfig.API_VERSION}/places/info?placeId=$placeId"
            val result = withContext(Dispatchers.IO) { API.callApi(url, token, "GET", null) }
            Gson().fromJson(result, PlaceInfoResponse::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
