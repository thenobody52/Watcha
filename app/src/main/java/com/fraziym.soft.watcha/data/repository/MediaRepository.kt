package com.fraziym.soft.watcha.data.repository

import com.fraziym.soft.watcha.data.local.dao.HistoryDao
import com.fraziym.soft.watcha.data.local.dao.MediaDao
import com.fraziym.soft.watcha.data.local.dao.PlaylistDao
import com.fraziym.soft.watcha.data.local.entity.MediaEntity
import com.fraziym.soft.watcha.data.local.entity.PlaylistEntity
import com.fraziym.soft.watcha.data.local.entity.PlaylistItemEntity
import com.fraziym.soft.watcha.data.local.entity.WatchHistoryEntity
import com.fraziym.soft.watcha.data.scanner.MediaScanner
import com.fraziym.soft.watcha.domain.model.MediaItemModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MediaRepository(
    private val mediaDao: MediaDao,
    private val playlistDao: PlaylistDao,
    private val historyDao: HistoryDao,
    private val scanner: MediaScanner
) {

    val allVideos: Flow<List<MediaItemModel>> = mediaDao.getAllVideos().map { entities ->
        entities.map { it.toDomainModel() }
    }

    val allAudio: Flow<List<MediaItemModel>> = mediaDao.getAllAudio().map { entities ->
        entities.map { it.toDomainModel() }
    }

    val favoriteMedia: Flow<List<MediaItemModel>> = mediaDao.getFavoriteMedia().map { entities ->
        entities.map { it.toDomainModel() }
    }

    val watchLaterMedia: Flow<List<MediaItemModel>> = mediaDao.getWatchLaterMedia().map { entities ->
        entities.map { it.toDomainModel() }
    }

    val continueWatching: Flow<List<MediaItemModel>> = mediaDao.getContinueWatchingMedia().map { entities ->
        entities.map { it.toDomainModel() }
    }

    val recentlyAdded: Flow<List<MediaItemModel>> = mediaDao.getRecentlyAddedMedia().map { entities ->
        entities.map { it.toDomainModel() }
    }

    val watchHistory: Flow<List<WatchHistoryEntity>> = historyDao.getWatchHistory()

    val playlists: Flow<List<PlaylistEntity>> = playlistDao.getAllPlaylists()

    suspend fun scanMediaLibrary() {
        mediaDao.deleteSamples()
        val scannedItems = scanner.scanDeviceMedia()
        if (scannedItems.isNotEmpty()) {
            mediaDao.insertAll(scannedItems)
        }
    }

    fun startRealtimeStorageMonitoring(coroutineScope: kotlinx.coroutines.CoroutineScope) {
        scanner.registerStorageObserver {
            coroutineScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                scanMediaLibrary()
            }
        }
    }

    suspend fun getMediaById(id: String): MediaItemModel? {
        return mediaDao.getMediaById(id)?.toDomainModel()
    }

    suspend fun toggleFavorite(id: String, isFavorite: Boolean) {
        mediaDao.updateFavorite(id, isFavorite)
    }

    suspend fun toggleWatchLater(id: String, isWatchLater: Boolean) {
        mediaDao.updateWatchLater(id, isWatchLater)
    }

    suspend fun updatePlaybackProgress(id: String, positionMs: Long) {
        val now = System.currentTimeMillis()
        mediaDao.updatePlaybackPosition(id, positionMs, now)

        val media = mediaDao.getMediaById(id)
        if (media != null && positionMs > 5000L) {
            historyDao.insertHistory(
                WatchHistoryEntity(
                    mediaId = media.id,
                    mediaTitle = media.title,
                    mediaUri = media.uri,
                    isVideo = media.isVideo,
                    positionMs = positionMs,
                    durationMs = media.durationMs,
                    watchedTimestamp = now
                )
            )
        }
    }

    suspend fun createPlaylist(name: String, description: String = ""): Long {
        return playlistDao.createPlaylist(PlaylistEntity(name = name, description = description))
    }

    suspend fun addToPlaylist(playlistId: Long, mediaId: String) {
        playlistDao.addMediaToPlaylist(
            PlaylistItemEntity(playlistId = playlistId, mediaId = mediaId, orderIndex = 0)
        )
    }

    suspend fun deletePlaylist(playlistId: Long) {
        playlistDao.deletePlaylist(playlistId)
    }

    suspend fun clearHistory() {
        historyDao.clearHistory()
    }

    private fun MediaEntity.toDomainModel(): MediaItemModel {
        return MediaItemModel(
            id = id,
            title = title,
            uri = uri,
            path = path,
            durationMs = durationMs,
            sizeBytes = sizeBytes,
            dateAdded = dateAdded,
            mimeType = mimeType,
            format = format,
            isVideo = isVideo,
            resolution = resolution,
            folderPath = folderPath,
            folderName = folderName,
            isFavorite = isFavorite,
            isWatchLater = isWatchLater,
            lastPlayedPositionMs = lastPlayedPositionMs,
            lastPlayedTimestamp = lastPlayedTimestamp,
            thumbnailUri = thumbnailUri
        )
    }
}
