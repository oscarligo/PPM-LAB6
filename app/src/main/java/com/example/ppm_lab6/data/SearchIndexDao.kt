package com.example.ppm_lab6.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchIndexDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<SearchIndexEntity>)

    @Query("DELETE FROM search_index WHERE query = :q AND page = :page")
    suspend fun clearPage(q: String, page: Int)

    @Query("DELETE FROM search_index WHERE query = :q")
    suspend fun clearQuery(q: String)

    @Query(
        "SELECT cached_photo.json FROM search_index " +
            "JOIN cached_photo ON cached_photo.id = search_index.photoId " +
            "WHERE query = :q " +
            "ORDER BY page ASC, position ASC"
    )
    fun observeJsonByQuery(q: String): Flow<List<String>>
}
