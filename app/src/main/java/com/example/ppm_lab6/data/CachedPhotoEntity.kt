package com.example.ppm_lab6.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_photo")
data class CachedPhotoEntity(
    @PrimaryKey val id: Long,
    val json: String,
    val updatedAt: Long
)

