package com.fraziym.soft.watcha.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watch_history")
data class WatchHistoryEntity(
    @PrimaryKey(autoGenerate = true) val historyId: Long = 0,
    val mediaId: String,
    val mediaTitle: String,
    val mediaUri: String,
    val isVideo: Boolean,
    val positionMs: Long,
    val durationMs: Long,
    val watchedTimestamp: Long = System.currentTimeMillis()
)
