package com.fraziym.soft.watcha.player

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.fraziym.soft.watcha.domain.model.MediaItemModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AspectRatioMode {
    FIT, CROP, STRETCH, FILL
}

data class PlayerState(
    val currentMedia: MediaItemModel? = null,
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val playbackSpeed: Float = 1.0f,
    val isMuted: Boolean = false,
    val volume: Float = 1.0f,
    val aspectRatioMode: AspectRatioMode = AspectRatioMode.FIT,
    val availableAudioTracks: List<String> = emptyList(),
    val availableSubtitleTracks: List<String> = emptyList(),
    val selectedAudioTrack: String? = null,
    val selectedSubtitleTrack: String? = null,
    val sleepTimerMinutesRemaining: Int = 0,
    val isRepeatOne: Boolean = false,
    val isHdrActive: Boolean = false,
    val hdrFormatName: String = "HDR"
)

@OptIn(UnstableApi::class)
class WatchaPlayerManager(private val context: Context) {

    private val trackSelector = DefaultTrackSelector(context)
    val exoPlayer: ExoPlayer = ExoPlayer.Builder(context)
        .setTrackSelector(trackSelector)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                .setUsage(C.USAGE_MEDIA)
                .build(),
            true
        )
        .setHandleAudioBecomingNoisy(true)
        .setSeekBackIncrementMs(10000L)
        .setSeekForwardIncrementMs(10000L)
        .build()

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Main)
    private var progressUpdateJob: Job? = null
    private var sleepTimerJob: Job? = null

    init {
        exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _playerState.value = _playerState.value.copy(isPlaying = isPlaying)
                if (isPlaying) {
                    startProgressTracker()
                } else {
                    stopProgressTracker()
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                val isBuffering = playbackState == Player.STATE_BUFFERING
                val duration = if (exoPlayer.duration > 0) exoPlayer.duration else 0L
                _playerState.value = _playerState.value.copy(
                    isBuffering = isBuffering,
                    durationMs = duration
                )
            }

            override fun onTracksChanged(tracks: Tracks) {
                updateTracksInfo(tracks)
            }
        })
    }

    fun playMedia(media: MediaItemModel, startPositionMs: Long = 0L) {
        val mediaItem = MediaItem.Builder()
            .setUri(Uri.parse(media.uri))
            .setMediaId(media.id)
            .build()

        _playerState.value = _playerState.value.copy(
            currentMedia = media,
            currentPositionMs = startPositionMs,
            durationMs = media.durationMs
        )

        exoPlayer.setMediaItem(mediaItem, startPositionMs)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    fun togglePlayPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        } else {
            exoPlayer.play()
        }
    }

    fun seekTo(positionMs: Long) {
        exoPlayer.seekTo(positionMs)
        _playerState.value = _playerState.value.copy(currentPositionMs = positionMs)
    }

    fun seekForward(seconds: Int = 10) {
        val newPos = (exoPlayer.currentPosition + (seconds * 1000L)).coerceAtMost(exoPlayer.duration)
        seekTo(newPos)
    }

    fun seekBackward(seconds: Int = 10) {
        val newPos = (exoPlayer.currentPosition - (seconds * 1000L)).coerceAtLeast(0L)
        seekTo(newPos)
    }

    fun setPlaybackSpeed(speed: Float) {
        exoPlayer.playbackParameters = PlaybackParameters(speed)
        _playerState.value = _playerState.value.copy(playbackSpeed = speed)
    }

    fun setAspectRatioMode(mode: AspectRatioMode) {
        _playerState.value = _playerState.value.copy(aspectRatioMode = mode)
    }

    fun setVolume(volume: Float) {
        val clamped = volume.coerceIn(0f, 1f)
        exoPlayer.volume = clamped
        _playerState.value = _playerState.value.copy(volume = clamped, isMuted = clamped == 0f)
    }

    fun toggleMute() {
        if (_playerState.value.isMuted) {
            setVolume(_playerState.value.volume.ifZero(1f))
        } else {
            exoPlayer.volume = 0f
            _playerState.value = _playerState.value.copy(isMuted = true)
        }
    }

    fun startSleepTimer(minutes: Int) {
        sleepTimerJob?.cancel()
        if (minutes <= 0) {
            _playerState.value = _playerState.value.copy(sleepTimerMinutesRemaining = 0)
            return
        }

        _playerState.value = _playerState.value.copy(sleepTimerMinutesRemaining = minutes)
        sleepTimerJob = scope.launch {
            var remainingSecs = minutes * 60
            while (remainingSecs > 0) {
                delay(1000L)
                remainingSecs--
                if (remainingSecs % 60 == 0) {
                    _playerState.value = _playerState.value.copy(sleepTimerMinutesRemaining = remainingSecs / 60)
                }
            }
            exoPlayer.pause()
            _playerState.value = _playerState.value.copy(sleepTimerMinutesRemaining = 0)
        }
    }

    private fun startProgressTracker() {
        progressUpdateJob?.cancel()
        progressUpdateJob = scope.launch {
            while (true) {
                if (exoPlayer.isPlaying) {
                    _playerState.value = _playerState.value.copy(
                        currentPositionMs = exoPlayer.currentPosition.coerceAtLeast(0L),
                        durationMs = exoPlayer.duration.coerceAtLeast(0L)
                    )
                }
                delay(500L)
            }
        }
    }

    private fun stopProgressTracker() {
        progressUpdateJob?.cancel()
    }

    private fun updateTracksInfo(tracks: Tracks) {
        val audioTracks = mutableListOf<String>()
        val subTracks = mutableListOf<String>()
        var isHdrDetected = _playerState.value.currentMedia?.resolution?.contains("HDR", ignoreCase = true) == true
        var hdrLabel = "HDR"

        for (trackGroup in tracks.groups) {
            val type = trackGroup.type
            for (i in 0 until trackGroup.length) {
                val format = trackGroup.getTrackFormat(i)
                val lang = format.language ?: "Undetermined"
                val label = format.label ?: "Track ${i + 1} ($lang)"
                if (type == C.TRACK_TYPE_AUDIO) {
                    audioTracks.add(label)
                } else if (type == C.TRACK_TYPE_TEXT) {
                    subTracks.add(label)
                } else if (type == C.TRACK_TYPE_VIDEO) {
                    val colorTransfer = format.colorInfo?.colorTransfer ?: -1
                    if (colorTransfer == C.COLOR_TRANSFER_ST2084) {
                        isHdrDetected = true
                        hdrLabel = "HDR10"
                    } else if (colorTransfer == C.COLOR_TRANSFER_HLG) {
                        isHdrDetected = true
                        hdrLabel = "HLG"
                    } else if (format.sampleMimeType?.contains("dolby", ignoreCase = true) == true) {
                        isHdrDetected = true
                        hdrLabel = "Dolby Vision"
                    }
                }
            }
        }

        _playerState.value = _playerState.value.copy(
            availableAudioTracks = audioTracks,
            availableSubtitleTracks = subTracks,
            isHdrActive = isHdrDetected,
            hdrFormatName = hdrLabel
        )
    }

    fun release() {
        stopProgressTracker()
        sleepTimerJob?.cancel()
        exoPlayer.release()
    }

    private fun Float.ifZero(default: Float): Float = if (this == 0f) default else this
}
