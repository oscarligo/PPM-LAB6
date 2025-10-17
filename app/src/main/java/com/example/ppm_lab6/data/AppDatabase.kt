package com.example.ppm_lab6.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [RecentSearch::class, FavoriteRemoteEntity::class, FavoriteLocalEntity::class, CachedPhotoEntity::class, SearchIndexEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recentSearchDao(): RecentSearchDao
    abstract fun favoriteRemoteDao(): FavoriteRemoteDao
    abstract fun favoriteLocalDao(): FavoriteLocalDao
    abstract fun cachedPhotoDao(): CachedPhotoDao
    abstract fun searchIndexDao(): SearchIndexDao
}
