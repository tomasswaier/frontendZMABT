package com.example.frontendzmabt.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.frontendzmabt.data.dao.CachedPostDao
import com.example.frontendzmabt.data.dao.PlaceDao
import com.example.frontendzmabt.data.dao.PostDao
import com.example.frontendzmabt.data.model.CachedPosts
import com.example.frontendzmabt.data.model.Place
import com.example.frontendzmabt.data.model.PostNoUser

@Database(
    entities = [PostNoUser::class, Place::class, CachedPosts::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun placeDao(): PlaceDao
    abstract fun cachedPostDao(): CachedPostDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                                context.applicationContext,
                                AppDatabase::class.java,
                                "app_db"
                            )
                    .fallbackToDestructiveMigration(false)
                    .build().also { instance = it }
            }
        }
    }
}