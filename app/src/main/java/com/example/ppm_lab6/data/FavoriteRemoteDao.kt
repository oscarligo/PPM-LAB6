package com.example.ppm_lab6.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteRemoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: FavoriteRemoteEntity)

    @Query("DELETE FROM favorite_remote WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT COUNT(*) FROM favorite_remote WHERE id = :id")
    fun isFavoriteFlow(id: Long): Flow<Int>

    @Query("SELECT * FROM favorite_remote ORDER BY updatedAt DESC")
    fun observeAll(): Flow<List<FavoriteRemoteEntity>>
}

