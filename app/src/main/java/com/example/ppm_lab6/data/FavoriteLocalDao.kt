package com.example.ppm_lab6.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteLocalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: FavoriteLocalEntity)

    @Query("DELETE FROM favorite_local WHERE uri = :uri")
    suspend fun delete(uri: String)

    @Query("SELECT * FROM favorite_local ORDER BY updatedAt DESC")
    fun observeAll(): Flow<List<FavoriteLocalEntity>>
}

