package com.example.ppm_lab6.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_remote")
data class FavoriteRemoteEntity(
    @PrimaryKey val id: Long,
    val json: String,
    val updatedAt: Long
)

