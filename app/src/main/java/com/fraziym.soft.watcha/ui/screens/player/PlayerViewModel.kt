package com.fraziym.soft.watcha.ui.screens.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fraziym.soft.watcha.data.repository.MediaRepository
import com.fraziym.soft.watcha.domain.model.MediaItemModel
import com.fraziym.soft.watcha.player.AspectRatioMode
import com.fraziym.soft.watcha.player.PlayerState
import com.fraziym.soft.watcha.player.WatchaPlayerManager
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlayerViewModel(
    val playerManager: WatchaPlayerManager,
    private val repository: MediaRepository
) : ViewModel() {

    val playerState: StateFlow<PlayerState> = playerManager.playerState

    fun playMedia(media: MediaItemModel) {
        viewModelScope.launch {
            val startPosition = if (media.lastPlayedPositionMs > 5000L) media.lastPlayedPositionMs else 0L
            playerManager.playMedia(media, startPosition)
        }
    }

    fun togglePlayPause() {
        playerManager.togglePlayPause()
    }

    fun seekTo(positionMs: Long) {
        playerManager.seekTo(positionMs)
        saveProgress(positionMs)
    }

    fun doubleTapSeekForward() {
        playerManager.seekForward(10)
        saveProgress(playerState.value.currentPositionMs)
    }

    fun doubleTapSeekBackward() {
        playerManager.seekBackward(10)
        saveProgress(playerState.value.currentPositionMs)
    }

    fun setPlaybackSpeed(speed: Float) {
        playerManager.setPlaybackSpeed(speed)
    }

    fun toggleAspectRatio() {
        val nextMode = when (playerState.value.aspectRatioMode) {
            AspectRatioMode.FIT -> AspectRatioMode.CROP
            AspectRatioMode.CROP -> AspectRatioMode.STRETCH
            AspectRatioMode.STRETCH -> AspectRatioMode.FILL
            AspectRatioMode.FILL -> AspectRatioMode.FIT
        }
        playerManager.setAspectRatioMode(nextMode)
    }

    fun setSleepTimer(minutes: Int) {
        playerManager.startSleepTimer(minutes)
    }

    private fun saveProgress(positionMs: Long) {
        val currentMedia = playerState.value.currentMedia
        if (currentMedia != null) {
            viewModelScope.launch {
                repository.updatePlaybackProgress(currentMedia.id, positionMs)
            }
        }
    }
}
