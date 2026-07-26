package com.fraziym.soft.watcha.data.scanner

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import java.io.File
import java.io.FileInputStream

enum class MediaClassification {
    VIDEO,
    AUDIO,
    UNSUPPORTED_MEDIA,
    CORRUPTED_MEDIA,
    NON_MEDIA
}

data class ValidationResult(
    val classification: MediaClassification,
    val mimeType: String = "",
    val durationMs: Long = 0L,
    val width: Int = 0,
    val height: Int = 0,
    val bitrate: Int = 0,
    val isHdr: Boolean = false,
    val reason: String = ""
)

object SmartMediaDetector {

    private val TS_SOURCE_KEYWORDS = listOf(
        "import ", "export ", "interface ", "type ", "declare ", "function ",
        "/// <reference", "const ", "let ", "var ", "class ", "enum ", "namespace "
    )

    private val D_SOURCE_KEYWORDS = listOf(
        "module ", "import std.", "void main(", "int main(", "unittest ", "immutable(", "auto "
    )

    fun validateMediaFile(context: Context, path: String, uriString: String? = null, reportedMime: String? = null): ValidationResult {
        val file = File(path)
        val isNetworkOrContent = uriString != null && (uriString.startsWith("http://") || uriString.startsWith("https://") || uriString.startsWith("content://"))

        if (!isNetworkOrContent) {
            if (!file.exists() || !file.isFile || file.length() < 128) {
                return ValidationResult(MediaClassification.NON_MEDIA, reason = "File missing or under minimum size threshold")
            }
        }

        val extension = file.extension.lowercase()

        // 1. Specific checks for tricky extensions like .ts and .d
        if (!isNetworkOrContent && file.exists()) {
            if (extension == "ts") {
                val isTsCode = checkIsTypeScriptSource(file)
                if (isTsCode) {
                    return ValidationResult(MediaClassification.NON_MEDIA, reason = "TypeScript source file detected")
                }
            } else if (extension == "d") {
                val isDCode = checkIsDSource(file)
                if (isDCode) {
                    return ValidationResult(MediaClassification.NON_MEDIA, reason = "D source file detected")
                }
            }
        }

        // 2. Deep verification with MediaMetadataRetriever
        val retriever = MediaMetadataRetriever()
        return try {
            if (isNetworkOrContent && uriString != null) {
                retriever.setDataSource(context, Uri.parse(uriString))
            } else {
                retriever.setDataSource(path)
            }

            val hasVideo = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO) == "yes"
            val hasAudio = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO) == "yes"
            val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val duration = durationStr?.toLongOrNull() ?: 0L

            if (!hasVideo && !hasAudio) {
                return ValidationResult(MediaClassification.NON_MEDIA, reason = "No audio or video streams present")
            }

            if (duration <= 0 && !isNetworkOrContent) {
                return ValidationResult(MediaClassification.CORRUPTED_MEDIA, reason = "Zero or invalid duration")
            }

            val mime = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE)
                ?: reportedMime
                ?: if (hasVideo) "video/mp4" else "audio/mp3"

            val width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toIntOrNull() ?: 0
            val height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toIntOrNull() ?: 0
            val bitrate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)?.toIntOrNull() ?: 0

            val colorTransfer = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COLOR_TRANSFER)?.toIntOrNull() ?: -1
            val colorStandard = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COLOR_STANDARD)?.toIntOrNull() ?: -1
            
            // Color transfer values 6 (ST2084 / PQ) or 7 (HLG) indicate HDR
            val isHdr = colorTransfer == 6 || colorTransfer == 7 || colorStandard == 6

            val classification = if (hasVideo) MediaClassification.VIDEO else MediaClassification.AUDIO

            ValidationResult(
                classification = classification,
                mimeType = mime,
                durationMs = duration,
                width = width,
                height = height,
                bitrate = bitrate,
                isHdr = isHdr
            )
        } catch (e: Exception) {
            // Fallback for sample network URLs or files where retriever fails but extension is known
            if (isNetworkOrContent || reportedMime?.startsWith("video/") == true || reportedMime?.startsWith("audio/") == true) {
                val isVideo = reportedMime?.startsWith("video/") ?: (extension in listOf("mp4", "mkv", "webm", "mov", "avi"))
                ValidationResult(
                    classification = if (isVideo) MediaClassification.VIDEO else MediaClassification.AUDIO,
                    mimeType = reportedMime ?: if (isVideo) "video/mp4" else "audio/mp3",
                    durationMs = 0L
                )
            } else {
                ValidationResult(MediaClassification.CORRUPTED_MEDIA, reason = "Failed to parse container header: ${e.message}")
            }
        } finally {
            try {
                retriever.release()
            } catch (_: Exception) {}
        }
    }

    private fun checkIsTypeScriptSource(file: File): Boolean {
        return try {
            FileInputStream(file).use { fis ->
                val buffer = ByteArray(minOf(1024, file.length().toInt()))
                val bytesRead = fis.read(buffer)
                if (bytesRead <= 0) return false

                // Check MPEG-TS sync byte 0x47 (71)
                var tsSyncCount = 0
                for (i in 0 until bytesRead step 188) {
                    if (buffer[i] == 0x47.toByte()) {
                        tsSyncCount++
                    }
                }
                if (tsSyncCount >= 2) {
                    return false // Valid MPEG Transport Stream
                }

                val text = String(buffer, 0, bytesRead, Charsets.UTF_8)
                TS_SOURCE_KEYWORDS.any { keyword -> text.contains(keyword) }
            }
        } catch (e: Exception) {
            false
        }
    }

    private fun checkIsDSource(file: File): Boolean {
        return try {
            FileInputStream(file).use { fis ->
                val buffer = ByteArray(minOf(1024, file.length().toInt()))
                val bytesRead = fis.read(buffer)
                if (bytesRead <= 0) return false

                val text = String(buffer, 0, bytesRead, Charsets.UTF_8)
                D_SOURCE_KEYWORDS.any { keyword -> text.contains(keyword) }
            }
        } catch (e: Exception) {
            false
        }
    }
}
