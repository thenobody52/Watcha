package com.fraziym.soft.watcha.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fraziym.soft.watcha.player.PlayerState
import kotlinx.coroutines.delay

@Composable
fun PlayerOverlayControls(
    state: PlayerState,
    onTogglePlayPause: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onDoubleTapSeekForward: () -> Unit,
    onDoubleTapSeekBackward: () -> Unit,
    onSpeedChange: (Float) -> Unit,
    onAspectRatioToggle: () -> Unit,
    onSleepTimerSelect: (Int) -> Unit,
    onPipClick: () -> Unit,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var controlsVisible by remember { mutableStateOf(true) }
    var isLocked by remember { mutableStateOf(false) }

    var showSpeedDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    // Auto hide controls after 4s
    LaunchedEffect(controlsVisible, state.isPlaying) {
        if (controlsVisible && state.isPlaying && !isLocked) {
            delay(4000L)
            controlsVisible = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        controlsVisible = !controlsVisible
                    },
                    onDoubleTap = { offset ->
                        if (!isLocked) {
                            if (offset.x > size.width / 2) {
                                onDoubleTapSeekForward()
                            } else {
                                onDoubleTapSeekBackward()
                            }
                        }
                    }
                )
            }
    ) {
        AnimatedVisibility(
            visible = controlsVisible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.55f))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Top Header Bar (Matching Reference Images)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: Back Arrow + Media Title
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("player_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Text(
                            text = state.currentMedia?.title ?: "The Apartment Job S01 E01",
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )

                        if (state.isHdrActive || state.currentMedia?.resolution?.contains("HDR") == true) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = state.hdrFormatName,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                        }
                    }

                    // Right: Help + Setting Buttons (Icon + Text below)
                    if (!isLocked) {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable { showHelpDialog = true }
                                    .padding(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.HelpOutline,
                                    contentDescription = "Help",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Help",
                                    fontSize = 10.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable { showSettingsDialog = true }
                                    .padding(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Setting",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Setting",
                                    fontSize = 10.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }
                }

                // Middle Left: Tap to Lock Button
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 8.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { isLocked = !isLocked }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = if (isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                        contentDescription = "Lock controls",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = if (isLocked) "Tap to Unlock" else "Tap to Lock",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }

                // Center Playback Controls (10s Rewind | Center Play/Pause | 10s Forward)
                if (!isLocked) {
                    Row(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalArrangement = Arrangement.spacedBy(36.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onDoubleTapSeekBackward,
                            modifier = Modifier
                                .testTag("player_rewind_button")
                                .size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Replay10,
                                contentDescription = "Rewind 10s",
                                tint = Color.White,
                                modifier = Modifier.size(34.dp)
                            )
                        }

                        IconButton(
                            onClick = onTogglePlayPause,
                            modifier = Modifier
                                .testTag("player_play_pause_button")
                                .size(48.dp)
                        ) {
                            Icon(
                                imageVector = if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (state.isPlaying) "Pause" else "Play",
                                tint = Color.White,
                                modifier = Modifier.size(42.dp)
                            )
                        }

                        IconButton(
                            onClick = onDoubleTapSeekForward,
                            modifier = Modifier
                                .testTag("player_forward_button")
                                .size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Forward10,
                                contentDescription = "Forward 10s",
                                tint = Color.White,
                                modifier = Modifier.size(34.dp)
                            )
                        }
                    }
                }

                // Bottom Timeline & Actions (Matching Netflix Style)
                if (!isLocked) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 2.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Subtitle text (Matching Netflix/Player Subtitle overlay)
                        Text(
                            text = if (state.currentSubtitle.isNotEmpty()) state.currentSubtitle else "Okay!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        // Slider + Timestamps Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = formatTime(state.currentPositionMs),
                                fontSize = 11.sp,
                                color = Color.White,
                                modifier = Modifier.padding(end = 8.dp)
                            )

                            Slider(
                                value = state.currentPositionMs.toFloat(),
                                onValueChange = { onSeekTo(it.toLong()) },
                                valueRange = 0f..(state.durationMs.coerceAtLeast(1L).toFloat()),
                                colors = SliderDefaults.colors(
                                    thumbColor = Color.White,
                                    activeTrackColor = MaterialTheme.colorScheme.primary,
                                    inactiveTrackColor = Color.White.copy(alpha = 0.35f)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("player_timeline_slider")
                            )

                            Text(
                                text = formatTime(state.durationMs),
                                fontSize = 11.sp,
                                color = Color.White,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }

                        // Bottom Actions Row: Left Play/Next | Right Fit, Language, Speed, Rotate
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Bottom Left Controls: Play/Pause & Next
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Icon(
                                    imageVector = if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play/Pause",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clickable { onTogglePlayPause() }
                                )

                                Icon(
                                    imageVector = Icons.Default.SkipNext,
                                    contentDescription = "Next Episode/Track",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clickable { onDoubleTapSeekForward() }
                                )
                            }

                            // Bottom Right Controls: Fit | Language | 1x | Rotate
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(18.dp)
                            ) {
                                // Fit
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clickable { onAspectRatioToggle() }
                                        .padding(2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AspectRatio,
                                        contentDescription = "Fit",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = state.aspectRatioMode.name.lowercase().replaceFirstChar { it.uppercase() },
                                        fontSize = 11.sp,
                                        color = Color.White
                                    )
                                }

                                // Language (Subtitles & Audio)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clickable { showLanguageDialog = true }
                                        .padding(2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Subtitles,
                                        contentDescription = "Language",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Language",
                                        fontSize = 11.sp,
                                        color = Color.White
                                    )
                                }

                                // Speed (1x)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clickable { showSpeedDialog = true }
                                        .padding(2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Speed,
                                        contentDescription = "Speed",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${state.playbackSpeed}x",
                                        fontSize = 11.sp,
                                        color = Color.White
                                    )
                                }

                                // Rotate
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clickable { onAspectRatioToggle() }
                                        .padding(2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ScreenRotation,
                                        contentDescription = "Rotate",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Rotate",
                                        fontSize = 11.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Speed Selection Dialog
    if (showSpeedDialog) {
        AlertDialog(
            onDismissRequest = { showSpeedDialog = false },
            title = { Text("Playback Speed", fontSize = 15.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { speed ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSpeedChange(speed)
                                    showSpeedDialog = false
                                }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = state.playbackSpeed == speed,
                                onClick = {
                                    onSpeedChange(speed)
                                    showSpeedDialog = false
                                }
                            )
                            Text(
                                text = if (speed == 1.0f) "1.0x (Normal)" else "${speed}x",
                                fontSize = 13.sp,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSpeedDialog = false }) {
                    Text("Close", fontSize = 12.sp)
                }
            }
        )
    }

    // Language / Audio & Subtitles Dialog
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text("Audio & Subtitle Tracks", fontSize = 15.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Audio Tracks", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    if (state.availableAudioTracks.isEmpty()) {
                        Text("Default Audio", fontSize = 12.sp, modifier = Modifier.padding(vertical = 4.dp))
                    } else {
                        state.availableAudioTracks.forEach { track ->
                            Text("• $track", fontSize = 12.sp, modifier = Modifier.padding(vertical = 4.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Subtitles", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    if (state.availableSubtitleTracks.isEmpty()) {
                        Text("Off / English (Auto)", fontSize = 12.sp, modifier = Modifier.padding(vertical = 4.dp))
                    } else {
                        state.availableSubtitleTracks.forEach { track ->
                            Text("• $track", fontSize = 12.sp, modifier = Modifier.padding(vertical = 4.dp))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text("Close", fontSize = 12.sp)
                }
            }
        )
    }

    // Help Dialog
    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = { Text("Player Gestures & Tips", fontSize = 15.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("• Tap screen once to toggle player overlay controls.", fontSize = 12.sp)
                    Text("• Double tap left side to rewind 10 seconds.", fontSize = 12.sp)
                    Text("• Double tap right side to skip forward 10 seconds.", fontSize = 12.sp)
                    Text("• Tap 'Tap to Lock' on the left to lock controls during playback.", fontSize = 12.sp)
                    Text("• Tap 'Fit' or 'Rotate' to adapt video frame mode.", fontSize = 12.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = { showHelpDialog = false }) {
                    Text("Got it", fontSize = 12.sp)
                }
            }
        )
    }

    // Settings Dialog
    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = { Text("Player Settings", fontSize = 15.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable {
                            showSettingsDialog = false
                            showSpeedDialog = true
                        },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Playback Speed", fontSize = 13.sp)
                        Text("${state.playbackSpeed}x", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().clickable {
                            showSettingsDialog = false
                            onAspectRatioToggle()
                        },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Aspect Ratio", fontSize = 13.sp)
                        Text(state.aspectRatioMode.name, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().clickable {
                            showSettingsDialog = false
                            onPipClick()
                        },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Picture-in-Picture Mode", fontSize = 13.sp)
                        Text("Launch", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSettingsDialog = false }) {
                    Text("Done", fontSize = 12.sp)
                }
            }
        )
    }
}

private fun formatTime(ms: Long): String {
    val totalSecs = ms / 1000
    val hours = totalSecs / 3600
    val mins = (totalSecs % 3600) / 60
    val secs = totalSecs % 60
    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, mins, secs)
    } else {
        String.format("%02d:%02d", mins, secs)
    }
}
