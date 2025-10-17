package com.example.ppm_lab6.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_search")
data class RecentSearch(
    @PrimaryKey val query: String,
    val updatedAt: Long,
)

