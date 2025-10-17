package com.example.ppm_lab6.data

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    @Volatile private var INSTANCE: AppDatabase? = null

    fun get(context: Context): AppDatabase = INSTANCE ?: synchronized(this) {
        INSTANCE ?: Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "ppm_lab6.db"
        ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
    }
}

