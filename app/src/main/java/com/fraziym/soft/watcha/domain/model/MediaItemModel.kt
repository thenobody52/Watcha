package com.fraziym.soft.watcha.domain.model

data class MediaItemModel(
    val id: String,
    val title: String,
    val uri: String,
    val path: String,
    val durationMs: Long,
    val sizeBytes: Long,
    val dateAdded: Long,
    val mimeType: String,
    val format: String,
    val isVideo: Boolean,
    val resolution: String = "",
    val folderPath: String = "",
    val folderName: String = "",
    val isFavorite: Boolean = false,
    val isWatchLater: Boolean = false,
    val lastPlayedPositionMs: Long = 0L,
    val lastPlayedTimestamp: Long = 0L,
    val thumbnailUri: String? = null
) {
    val durationFormatted: String
        get() {
            val totalSeconds = durationMs / 1000
            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            val seconds = totalSeconds % 60
            return if (hours > 0) {
                String.format("%d:%02d:%02d", hours, minutes, seconds)
            } else {
                String.format("%02d:%02d", minutes, seconds)
            }
        }

    val sizeFormatted: String
        get() {
            val mb = sizeBytes / (1024.0 * 1024.0)
            return if (mb >= 1000) {
                String.format("%.2f GB", mb / 1024.0)
            } else {
                String.format("%.1f MB", mb)
            }
        }
}
