package com.fraziym.soft.watcha.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fraziym.soft.watcha.ui.components.HeaderBar
import com.fraziym.soft.watcha.ui.theme.glassCard

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onThemeToggle: () -> Unit,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val prefs by viewModel.userPreferences.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        HeaderBar(
            title = "Settings",
            onThemeToggleClick = onThemeToggle,
            isDarkTheme = isDarkTheme
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Playback Section
            item {
                SettingsGroup(title = "Playback", icon = Icons.Default.PlayCircle) {
                    SwitchSettingItem(
                        title = "Resume Playback",
                        subtitle = "Remember last played position when reopening videos",
                        checked = prefs.resumePlayback,
                        onCheckedChange = { viewModel.setResumePlayback(it) },
                        testTag = "setting_resume_playback"
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    SwitchSettingItem(
                        title = "Background Audio",
                        subtitle = "Continue playing audio when app is minimized",
                        checked = prefs.backgroundAudio,
                        onCheckedChange = { viewModel.setBackgroundAudio(it) },
                        testTag = "setting_bg_audio"
                    )
                }
            }

            // Video & Decoding Section
            item {
                SettingsGroup(title = "Video & Performance", icon = Icons.Default.Videocam) {
                    SwitchSettingItem(
                        title = "Hardware Decoding",
                        subtitle = "Use GPU hardware acceleration for smooth playback",
                        checked = prefs.hwDecodingEnabled,
                        onCheckedChange = { viewModel.setHwDecodingEnabled(it) },
                        testTag = "setting_hw_decoding"
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    SwitchSettingItem(
                        title = "Auto Picture-in-Picture",
                        subtitle = "Automatically trigger PiP when pressing Home",
                        checked = prefs.autoPipEnabled,
                        onCheckedChange = { viewModel.setAutoPipEnabled(it) },
                        testTag = "setting_auto_pip"
                    )
                }
            }

            // Appearance Section
            item {
                SettingsGroup(title = "Appearance", icon = Icons.Default.Palette) {
                    SwitchSettingItem(
                        title = "Dark Theme",
                        subtitle = "Toggle between Dark and Light mode color scheme",
                        checked = isDarkTheme,
                        onCheckedChange = { onThemeToggle() },
                        testTag = "setting_dark_theme"
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Glassmorphism Blur Intensity",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${(prefs.glassIntensity * 100).toInt()}% Intensity",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Slider(
                            value = prefs.glassIntensity,
                            onValueChange = { viewModel.setGlassIntensity(it) },
                            valueRange = 0.2f..1.0f
                        )
                    }
                }
            }

            // Subtitles & Audio
            item {
                SettingsGroup(title = "Subtitles & Audio", icon = Icons.Default.Subtitles) {
                    InfoSettingItem(
                        title = "Subtitle Style",
                        subtitle = "Default font size: ${prefs.subtitleFontSize}sp • UTF-8 Encoding"
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    InfoSettingItem(
                        title = "Audio Engine",
                        subtitle = "High Fidelity ExoPlayer Audio Track Selector"
                    )
                }
            }

            // Storage & Downloads
            item {
                SettingsGroup(title = "Downloads & Storage", icon = Icons.Default.Download) {
                    InfoSettingItem(
                        title = "Storage Path",
                        subtitle = "Android/data/com.fraziym.soft.watcha/"
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    InfoSettingItem(
                        title = "Private Cache",
                        subtitle = "Application private cache for thumbnails and metadata"
                    )
                }
            }

            // Privacy Section
            item {
                SettingsGroup(title = "Privacy & Security", icon = Icons.Default.PrivacyTip) {
                    InfoSettingItem(
                        title = "Local Privacy First",
                        subtitle = "Zero data collection. All metadata stored locally on device."
                    )
                }
            }

            // About Section
            item {
                SettingsGroup(title = "About Watcha", icon = Icons.Default.Info) {
                    InfoSettingItem(
                        title = "Watcha Media Player v1.0.0",
                        subtitle = "Package: com.fraziym.soft.watcha\nDeveloper: Akik Forazi\nCompany: FRAZIYM Soft\nLicense: MIT License"
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun SettingsGroup(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .glassCard(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            content()
        }
    }
}

@Composable
private fun SwitchSettingItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(testTag),
            colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary)
        )
    }
}

@Composable
private fun InfoSettingItem(
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
