package com.example.frontendzmabt.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cachedPosts")
data class CachedPosts(
    @PrimaryKey
    val id: Int,
    val placeId: Int,
    val description: String,
    val createdAt: String,
    val updatedAt: String?,
    val stars: Int,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0

)
