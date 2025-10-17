package com.example.ppm_lab6.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [RecentSearch::class, FavoriteRemoteEntity::class, FavoriteLocalEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recentSearchDao(): RecentSearchDao
    abstract fun favoriteRemoteDao(): FavoriteRemoteDao
    abstract fun favoriteLocalDao(): FavoriteLocalDao
}
