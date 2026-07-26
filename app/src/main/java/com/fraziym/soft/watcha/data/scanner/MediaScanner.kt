package com.fraziym.soft.watcha.data.scanner

import android.content.ContentResolver
import android.content.Context
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import com.fraziym.soft.watcha.data.local.entity.MediaEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MediaScanner(private val context: Context) {

    private var storageObserver: ContentObserver? = null

    fun registerStorageObserver(onChangeCallback: () -> Unit) {
        if (storageObserver != null) return
        val handler = Handler(Looper.getMainLooper())
        storageObserver = object : ContentObserver(handler) {
            override fun onChange(selfChange: Boolean) {
                super.onChange(selfChange)
                onChangeCallback()
            }
        }
        val resolver = context.contentResolver
        try {
            resolver.registerContentObserver(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, true, storageObserver!!)
            resolver.registerContentObserver(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true, storageObserver!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun unregisterStorageObserver() {
        storageObserver?.let {
            try {
                context.contentResolver.unregisterContentObserver(it)
            } catch (_: Exception) {}
            storageObserver = null
        }
    }

    suspend fun scanDeviceMedia(): List<MediaEntity> = withContext(Dispatchers.IO) {
        val mediaList = mutableListOf<MediaEntity>()

        // 1. Scan Videos via MediaStore
        try {
            val videoResolver: ContentResolver = context.contentResolver
            val videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            val videoProjection = arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media.MIME_TYPE
            )

            videoResolver.query(
                videoUri,
                videoProjection,
                null,
                null,
                "${MediaStore.Video.Media.DATE_ADDED} DESC"
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)
                val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
                val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)
                val mimeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val title = cursor.getString(titleColumn) ?: "Video $id"
                    val path = cursor.getString(pathColumn) ?: ""
                    val duration = cursor.getLong(durationColumn)
                    val size = cursor.getLong(sizeColumn)
                    val dateAdded = cursor.getLong(dateColumn)
                    val mimeType = cursor.getString(mimeColumn) ?: "video/mp4"

                    // Use SmartMediaDetector to verify playable video streams & filter code/corrupted files
                    val validation = SmartMediaDetector.validateMediaFile(context, path, reportedMime = mimeType)
                    if (validation.classification != MediaClassification.VIDEO) {
                        continue // Skip non-media or non-video files
                    }

                    val ext = path.substringAfterLast('.', "mp4").uppercase()
                    val folderPath = path.substringBeforeLast('/', "")
                    val folderName = folderPath.substringAfterLast('/', "Internal Storage")

                    val resLabel = if (validation.height >= 2160) "4K Ultra HD"
                        else if (validation.height >= 1440) "1440p QHD"
                        else if (validation.height >= 1080) "1080p Full HD"
                        else if (validation.height >= 720) "720p HD"
                        else if (validation.height > 0) "${validation.height}p"
                        else "HD Video"

                    val finalDuration = if (validation.durationMs > 0) validation.durationMs else duration

                    mediaList.add(
                        MediaEntity(
                            id = "vid_$id",
                            title = title,
                            uri = path,
                            path = path,
                            durationMs = finalDuration,
                            sizeBytes = size,
                            dateAdded = dateAdded,
                            mimeType = validation.mimeType,
                            format = ext,
                            isVideo = true,
                            resolution = if (validation.isHdr) "$resLabel • HDR" else resLabel,
                            folderPath = folderPath,
                            folderName = folderName
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 2. Scan Audios via MediaStore
        try {
            val audioResolver: ContentResolver = context.contentResolver
            val audioUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val audioProjection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.DATE_ADDED,
                MediaStore.Audio.Media.MIME_TYPE
            )

            audioResolver.query(
                audioUri,
                audioProjection,
                null,
                null,
                "${MediaStore.Audio.Media.TITLE} ASC"
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
                val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
                val mimeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val title = cursor.getString(titleColumn) ?: "Audio $id"
                    val path = cursor.getString(pathColumn) ?: ""
                    val duration = cursor.getLong(durationColumn)
                    val size = cursor.getLong(sizeColumn)
                    val dateAdded = cursor.getLong(dateColumn)
                    val mimeType = cursor.getString(mimeColumn) ?: "audio/mp3"

                    val validation = SmartMediaDetector.validateMediaFile(context, path, reportedMime = mimeType)
                    if (validation.classification != MediaClassification.AUDIO) {
                        continue // Skip non-audio or invalid files
                    }

                    val ext = path.substringAfterLast('.', "mp3").uppercase()
                    val folderPath = path.substringBeforeLast('/', "")
                    val folderName = folderPath.substringAfterLast('/', "Music")

                    val finalDuration = if (validation.durationMs > 0) validation.durationMs else duration

                    mediaList.add(
                        MediaEntity(
                            id = "aud_$id",
                            title = title,
                            uri = path,
                            path = path,
                            durationMs = finalDuration,
                            sizeBytes = size,
                            dateAdded = dateAdded,
                            mimeType = validation.mimeType,
                            format = ext,
                            isVideo = false,
                            resolution = if (ext == "FLAC" || ext == "WAV") "Lossless Audio" else "Audio Track",
                            folderPath = folderPath,
                            folderName = folderName
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return@withContext mediaList
    }
}
