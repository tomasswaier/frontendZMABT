package com.example.frontendzmabt.data.dao


import androidx.room.*
import com.example.frontendzmabt.data.model.Place
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaceDao {
    @Query("SELECT * FROM places")
    fun getAll(): Flow<List<Place>>

    @Query("SELECT * FROM places WHERE id = :id")
    suspend fun getById(id: Int): Place?

    @Upsert
    suspend fun upsertAll(places: List<Place>)

    @Delete
    suspend fun delete(place: Place)
}