package com.fraziym.soft.watcha.ui.screens.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fraziym.soft.watcha.domain.model.MediaItemModel
import com.fraziym.soft.watcha.ui.components.FolderItemCard
import com.fraziym.soft.watcha.ui.components.HeaderBar
import com.fraziym.soft.watcha.ui.components.MediaItemCard

@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel,
    onPlayMedia: (MediaItemModel) -> Unit,
    onThemeToggle: () -> Unit,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        HeaderBar(
            title = "Library",
            onThemeToggleClick = onThemeToggle,
            isDarkTheme = isDarkTheme
        )

        // Tabs: Videos, Audio, Folders
        TabRow(
            selectedTabIndex = state.selectedTab.ordinal,
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        ) {
            LibraryTab.values().forEach { tab ->
                Tab(
                    selected = state.selectedTab == tab,
                    onClick = { viewModel.setTab(tab) },
                    text = {
                        Text(
                            text = tab.name,
                            fontWeight = if (state.selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    modifier = Modifier.testTag("tab_${tab.name}")
                )
            }
        }

        // Format Filter Chips & Actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val formats = listOf("MP4", "MKV", "AVI", "FLV", "WEBM", "MOV", "MP3", "FLAC")
            LazyRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(formats) { fmt ->
                    FilterChip(
                        selected = state.selectedFormatFilter == fmt,
                        onClick = { viewModel.setFormatFilter(fmt) },
                        label = { Text(fmt) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            IconButton(
                onClick = { viewModel.scanLibrary() },
                modifier = Modifier
                    .testTag("library_scan_button")
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
            ) {
                if (state.isScanning) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Scan Library",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Content List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 100.dp, top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            when (state.selectedTab) {
                LibraryTab.VIDEOS -> {
                    if (state.videos.isEmpty()) {
                        item {
                            EmptyLibraryState(
                                message = "No videos found in library",
                                onScan = { viewModel.scanLibrary() }
                            )
                        }
                    } else {
                        items(state.videos, key = { "lib_vid_${it.id}" }) { media ->
                            MediaItemCard(
                                media = media,
                                onPlayClick = onPlayMedia,
                                onFavoriteToggle = { viewModel.toggleFavorite(it) },
                                onWatchLaterToggle = { viewModel.toggleWatchLater(it) }
                            )
                        }
                    }
                }

                LibraryTab.AUDIO -> {
                    if (state.audios.isEmpty()) {
                        item {
                            EmptyLibraryState(
                                message = "No audio tracks found in library",
                                onScan = { viewModel.scanLibrary() }
                            )
                        }
                    } else {
                        items(state.audios, key = { "lib_aud_${it.id}" }) { media ->
                            MediaItemCard(
                                media = media,
                                onPlayClick = onPlayMedia,
                                onFavoriteToggle = { viewModel.toggleFavorite(it) },
                                onWatchLaterToggle = { viewModel.toggleWatchLater(it) }
                            )
                        }
                    }
                }

                LibraryTab.FOLDERS -> {
                    items(state.folders, key = { "folder_${it.folderName}" }) { folder ->
                        FolderItemCard(
                            folderName = folder.folderName,
                            folderPath = folder.folderPath,
                            itemCount = folder.items.size,
                            onClick = {
                                if (folder.items.isNotEmpty()) {
                                    onPlayMedia(folder.items.first())
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyLibraryState(message: String, onScan: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Button(
            onClick = onScan,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Scan Storage Now")
        }
    }
}
