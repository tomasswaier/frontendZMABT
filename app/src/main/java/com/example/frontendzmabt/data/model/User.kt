package com.example.frontendzmabt.data.model

data class User(
    val id: Number?,
    val username: String?,
    val email: String?,
    val bio: String? = null,
    val createdAt: String,
    val updatedAt: String
)