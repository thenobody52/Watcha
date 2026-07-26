package com.fraziym.soft.watcha.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fraziym.soft.watcha.data.local.entity.WatchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM watch_history ORDER BY watchedTimestamp DESC")
    fun getWatchHistory(): Flow<List<WatchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: WatchHistoryEntity)

    @Query("DELETE FROM watch_history")
    suspend fun clearHistory()

    @Query("DELETE FROM watch_history WHERE historyId = :historyId")
    suspend fun deleteHistoryEntry(historyId: Long)
}
