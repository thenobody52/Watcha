package com.fraziym.soft.watcha.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fraziym.soft.watcha.data.local.entity.MediaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaDao {
    @Query("SELECT * FROM media_items ORDER BY title ASC")
    fun getAllMedia(): Flow<List<MediaEntity>>

    @Query("SELECT * FROM media_items WHERE isVideo = 1 ORDER BY dateAdded DESC")
    fun getAllVideos(): Flow<List<MediaEntity>>

    @Query("SELECT * FROM media_items WHERE isVideo = 0 ORDER BY title ASC")
    fun getAllAudio(): Flow<List<MediaEntity>>

    @Query("SELECT * FROM media_items WHERE id = :id LIMIT 1")
    suspend fun getMediaById(id: String): MediaEntity?

    @Query("SELECT * FROM media_items WHERE isFavorite = 1 ORDER BY title ASC")
    fun getFavoriteMedia(): Flow<List<MediaEntity>>

    @Query("SELECT * FROM media_items WHERE isWatchLater = 1 ORDER BY title ASC")
    fun getWatchLaterMedia(): Flow<List<MediaEntity>>

    @Query("SELECT * FROM media_items WHERE lastPlayedPositionMs > 0 ORDER BY lastPlayedTimestamp DESC LIMIT 10")
    fun getContinueWatchingMedia(): Flow<List<MediaEntity>>

    @Query("SELECT * FROM media_items ORDER BY dateAdded DESC LIMIT 10")
    fun getRecentlyAddedMedia(): Flow<List<MediaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<MediaEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: MediaEntity)

    @Update
    suspend fun update(item: MediaEntity)

    @Query("UPDATE media_items SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: String, isFavorite: Boolean)

    @Query("UPDATE media_items SET isWatchLater = :isWatchLater WHERE id = :id")
    suspend fun updateWatchLater(id: String, isWatchLater: Boolean)

    @Query("UPDATE media_items SET lastPlayedPositionMs = :positionMs, lastPlayedTimestamp = :timestamp WHERE id = :id")
    suspend fun updatePlaybackPosition(id: String, positionMs: Long, timestamp: Long)

    @Query("DELETE FROM media_items WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM media_items WHERE id LIKE 'sample_%'")
    suspend fun deleteSamples()

    @Query("DELETE FROM media_items")
    suspend fun deleteAllMedia()
}
