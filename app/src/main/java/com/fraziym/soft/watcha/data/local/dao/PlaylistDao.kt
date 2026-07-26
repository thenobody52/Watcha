package com.fraziym.soft.watcha.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fraziym.soft.watcha.data.local.entity.MediaEntity
import com.fraziym.soft.watcha.data.local.entity.PlaylistEntity
import com.fraziym.soft.watcha.data.local.entity.PlaylistItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlists ORDER BY createdAt DESC")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createPlaylist(playlist: PlaylistEntity): Long

    @Query("DELETE FROM playlists WHERE id = :playlistId")
    suspend fun deletePlaylist(playlistId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMediaToPlaylist(item: PlaylistItemEntity)

    @Query("DELETE FROM playlist_items WHERE playlistId = :playlistId AND mediaId = :mediaId")
    suspend fun removeMediaFromPlaylist(playlistId: Long, mediaId: String)

    @Query("""
        SELECT m.* FROM media_items m
        INNER JOIN playlist_items p ON m.id = p.mediaId
        WHERE p.playlistId = :playlistId
        ORDER BY p.orderIndex ASC
    """)
    fun getMediaForPlaylist(playlistId: Long): Flow<List<MediaEntity>>
}
