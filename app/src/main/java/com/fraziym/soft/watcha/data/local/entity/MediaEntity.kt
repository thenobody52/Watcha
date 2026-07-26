package com.fraziym.soft.watcha.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "media_items")
data class MediaEntity(
    @PrimaryKey val id: String,
    val title: String,
    val uri: String,
    val path: String,
    val durationMs: Long,
    val sizeBytes: Long,
    val dateAdded: Long,
    val mimeType: String,
    val format: String, // MP4, MKV, AVI, MP3, etc.
    val isVideo: Boolean,
    val resolution: String = "",
    val folderPath: String = "",
    val folderName: String = "",
    val isFavorite: Boolean = false,
    val isWatchLater: Boolean = false,
    val lastPlayedPositionMs: Long = 0L,
    val lastPlayedTimestamp: Long = 0L,
    val thumbnailUri: String? = null
)
