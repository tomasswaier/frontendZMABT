package com.example.frontendzmabt.data.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostNoUser(
    @PrimaryKey
    val id: Int,
    val userId: Int,
    val placeId: Int,
    val description: String,
    val createdAt: String,
    val updatedAt: String?,
    val stars: Int,
)
