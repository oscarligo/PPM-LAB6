package com.example.ppm_lab6.data

import androidx.room.Entity

@Entity(
    tableName = "search_index",
    primaryKeys = ["query", "page", "position"]
)
data class SearchIndexEntity(
    val query: String,
    val page: Int,
    val position: Int,
    val photoId: Long
)

