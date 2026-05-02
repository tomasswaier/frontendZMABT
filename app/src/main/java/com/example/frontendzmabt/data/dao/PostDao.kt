package com.example.frontendzmabt.data.dao

import androidx.room.*
import com.example.frontendzmabt.data.model.Post
import com.example.frontendzmabt.data.model.PostNoUser
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    @Query("SELECT * FROM posts")
    fun getAll(): Flow<List<PostNoUser>>

    @Query("SELECT * FROM posts WHERE userId = :userId")
    fun getByUser(userId: Int): Flow<List<PostNoUser>>

    @Query("SELECT * FROM posts WHERE placeId = :placeId")
    fun getByPlace(placeId: Int): Flow<List<PostNoUser>>

    @Query("SELECT * FROM posts WHERE id = :id")
    suspend fun getById(id: Int): PostNoUser?

    @Query("DELETE FROM posts")
    suspend fun deleteAll()
    @Upsert
    suspend fun upsertAll(posts: List<PostNoUser>)

    @Delete
    suspend fun delete(post: PostNoUser)
}