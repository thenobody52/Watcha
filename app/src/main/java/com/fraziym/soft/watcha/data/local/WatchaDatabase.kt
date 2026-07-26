package com.fraziym.soft.watcha.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.fraziym.soft.watcha.data.local.dao.HistoryDao
import com.fraziym.soft.watcha.data.local.dao.MediaDao
import com.fraziym.soft.watcha.data.local.dao.PlaylistDao
import com.fraziym.soft.watcha.data.local.entity.MediaEntity
import com.fraziym.soft.watcha.data.local.entity.PlaylistEntity
import com.fraziym.soft.watcha.data.local.entity.PlaylistItemEntity
import com.fraziym.soft.watcha.data.local.entity.WatchHistoryEntity

@Database(
    entities = [
        MediaEntity::class,
        PlaylistEntity::class,
        PlaylistItemEntity::class,
        WatchHistoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class WatchaDatabase : RoomDatabase() {
    abstract fun mediaDao(): MediaDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile
        private var INSTANCE: WatchaDatabase? = null

        fun getInstance(context: Context): WatchaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WatchaDatabase::class.java,
                    "watcha_media_database.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
