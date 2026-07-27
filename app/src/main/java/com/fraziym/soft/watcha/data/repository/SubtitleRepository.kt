package com.fraziym.soft.watcha.data.repository

import com.fraziym.soft.watcha.domain.model.SubtitleItem
import java.io.File

interface SubtitleRepository {
    /**
     * Scans the local directory for subtitle files matching the video file.
     */
    suspend fun findLocalSubtitles(videoFile: File): List<SubtitleItem>

    /**
     * Downloads subtitles from an online source.
     */
    suspend fun downloadSubtitle(videoFile: File, languageCode: String): SubtitleItem?

    /**
     * Checks if the video file has embedded subtitles.
     */
    suspend fun hasEmbeddedSubtitles(videoFile: File): Boolean
}
