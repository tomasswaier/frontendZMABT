package com.example.frontendzmabt.data.dao

import androidx.room.*
import com.example.frontendzmabt.data.model.CachedPosts
import kotlinx.coroutines.flow.Flow

@Dao
interface CachedPostDao {
    @Query("SELECT * FROM cachedPosts")
    suspend fun getAll(): List<CachedPosts>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: CachedPosts)

    @Delete
    suspend fun delete(post: CachedPosts)

    @Query("DELETE FROM cachedPosts")
    suspend fun deleteAll()
}