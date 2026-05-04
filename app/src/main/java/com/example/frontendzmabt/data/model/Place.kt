package com.example.frontendzmabt.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "places")
data class Place(
    @PrimaryKey
    val id: Int,
    val latitude: Double,
    val longitude: Double,
    val aiDescription: String,
    val createdAt: String
)