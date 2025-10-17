package com.example.ppm_lab6.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_local")
data class FavoriteLocalEntity(
    @PrimaryKey val uri: String,
    val updatedAt: Long
)

