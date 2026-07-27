package com.fraziym.soft.watcha.data.repository

import android.media.MediaMetadataRetriever
import com.fraziym.soft.watcha.domain.model.SubtitleItem
import java.io.File
import java.util.Locale

class SubtitleRepositoryImpl : SubtitleRepository {

    override suspend fun findLocalSubtitles(videoFile: File): List<SubtitleItem> {
        val parentDir = videoFile.parentFile ?: return emptyList()
        val videoBaseName = videoFile.nameWithoutExtension

        // Look for files with same base name + .srt, .vtt, .ass, .ssa
        val subtitleExtensions = listOf("srt", "vtt", "ass", "ssa")
        
        return parentDir.listFiles { _, name ->
            val file = File(name)
            val ext = file.extension.lowercase(Locale.ROOT)
            
            // Fuzzy match: check if name starts with or contains base name
            ext in subtitleExtensions && name.contains(videoBaseName, ignoreCase = true)
        }?.map { file ->
            SubtitleItem(
                id = file.absolutePath,
                name = file.name,
                language = "Unknown", // Could be parsed from filename if formatted correctly
                file = file
            )
        } ?: emptyList()
    }

    override suspend fun downloadSubtitle(videoFile: File, languageCode: String): SubtitleItem? {
        // TODO: Implement online downloader integration (e.g., OpenSubtitles API)
        return null
    }

    override suspend fun hasEmbeddedSubtitles(videoFile: File): Boolean {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(videoFile.absolutePath)
            val trackCount = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_NUM_TRACKS)?.toInt() ?: 0
            
            var hasSubtitles = false
            for (i in 0 until trackCount) {
                val trackType = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE) // This is simplified
                // Real implementation would need to check track type via ExoPlayer/MediaMetadataRetriever
                // For now, return false as a placeholder
            }
            hasSubtitles
        } catch (e: Exception) {
            false
        } finally {
            retriever.release()
        }
    }
}
