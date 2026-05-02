package com.example.frontendzmabt.data

data class User(
    val id: Number?,
    val username: String?,
    val email: String?,
    val bio: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)
