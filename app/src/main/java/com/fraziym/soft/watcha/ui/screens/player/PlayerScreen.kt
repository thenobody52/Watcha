package com.fraziym.soft.watcha.ui.screens.player

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.fraziym.soft.watcha.player.AspectRatioMode
import com.fraziym.soft.watcha.ui.components.PlayerOverlayControls

@OptIn(UnstableApi::class)
@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel,
    onEnterPip: () -> Unit = {},
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val state by viewModel.playerState.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("player_screen")
    ) {
        if (state.currentMedia == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No media loaded. Select a video or audio from Home/Library.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.padding(24.dp)
                )
            }
        } else {
            // Media3 ExoPlayer View
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = viewModel.playerManager.exoPlayer
                        useController = false
                        resizeMode = when (state.aspectRatioMode) {
                            AspectRatioMode.FIT -> AspectRatioFrameLayout.RESIZE_MODE_FIT
                            AspectRatioMode.CROP -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                            AspectRatioMode.STRETCH -> AspectRatioFrameLayout.RESIZE_MODE_FILL
                            AspectRatioMode.FILL -> AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
                        }
                        layoutParams = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                },
                update = { playerView ->
                    playerView.resizeMode = when (state.aspectRatioMode) {
                        AspectRatioMode.FIT -> AspectRatioFrameLayout.RESIZE_MODE_FIT
                        AspectRatioMode.CROP -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                        AspectRatioMode.STRETCH -> AspectRatioFrameLayout.RESIZE_MODE_FILL
                        AspectRatioMode.FILL -> AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Glass Overlay Controls
            PlayerOverlayControls(
                state = state,
                onTogglePlayPause = { viewModel.togglePlayPause() },
                onSeekTo = { viewModel.seekTo(it) },
                onDoubleTapSeekForward = { viewModel.doubleTapSeekForward() },
                onDoubleTapSeekBackward = { viewModel.doubleTapSeekBackward() },
                onSpeedChange = { viewModel.setPlaybackSpeed(it) },
                onAspectRatioToggle = { viewModel.toggleAspectRatio() },
                onSleepTimerSelect = { viewModel.setSleepTimer(it) },
                onPipClick = onEnterPip,
                onBackClick = onBackClick
            )
        }
    }
}
