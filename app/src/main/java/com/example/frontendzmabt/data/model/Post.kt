package com.example.frontendzmabt.data.model

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