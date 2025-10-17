package com.example.ppm_lab6.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CachedPhotoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<CachedPhotoEntity>)

    @Query("DELETE FROM cached_photo WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Long>)
}

