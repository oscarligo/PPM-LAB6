package com.example.ppm_lab6.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentSearchDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: RecentSearch)

    @Query("SELECT * FROM recent_search ORDER BY updatedAt DESC LIMIT :limit")
    fun observeRecent(limit: Int = 10): Flow<List<RecentSearch>>

    @Query(
        "DELETE FROM recent_search WHERE query NOT IN (SELECT query FROM recent_search ORDER BY updatedAt DESC LIMIT :limit)"
    )
    suspend fun trim(limit: Int = 10)
}

