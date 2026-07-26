package com.fraziym.soft.watcha.ui.screens.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fraziym.soft.watcha.domain.model.MediaItemModel
import com.fraziym.soft.watcha.ui.components.HeaderBar
import com.fraziym.soft.watcha.ui.components.MediaItemCard
import com.fraziym.soft.watcha.ui.theme.glassCard

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    onPlayMedia: (MediaItemModel) -> Unit,
    onThemeToggle: () -> Unit,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showCreatePlaylistDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        HeaderBar(
            title = "Favorites & Playlists",
            onThemeToggleClick = onThemeToggle,
            isDarkTheme = isDarkTheme
        )

        // Sub Tabs
        TabRow(
            selectedTabIndex = state.selectedTab.ordinal,
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        ) {
            FavoritesTab.values().forEach { tab ->
                Tab(
                    selected = state.selectedTab == tab,
                    onClick = { viewModel.setTab(tab) },
                    text = {
                        Text(
                            text = tab.name.replace("_", " "),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (state.selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    modifier = Modifier.testTag("fav_tab_${tab.name}")
                )
            }
        }

        // Action Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = when (state.selectedTab) {
                    FavoritesTab.FAVORITES -> "Bookmarked Favorites (${state.favoritesList.size})"
                    FavoritesTab.WATCH_LATER -> "Saved Watch Later (${state.watchLaterList.size})"
                    FavoritesTab.PLAYLISTS -> "Custom Playlists (${state.playlists.size})"
                    FavoritesTab.HISTORY -> "Playback History (${state.watchHistory.size})"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            if (state.selectedTab == FavoritesTab.PLAYLISTS) {
                Button(
                    onClick = { showCreatePlaylistDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Text("New", modifier = Modifier.padding(start = 4.dp))
                }
            } else if (state.selectedTab == FavoritesTab.HISTORY) {
                IconButton(onClick = { viewModel.clearHistory() }) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Clear History",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            when (state.selectedTab) {
                FavoritesTab.FAVORITES -> {
                    if (state.favoritesList.isEmpty()) {
                        item { EmptyState("No favorites bookmarked yet.") }
                    } else {
                        items(state.favoritesList, key = { "fav_${it.id}" }) { media ->
                            MediaItemCard(
                                media = media,
                                onPlayClick = onPlayMedia,
                                onFavoriteToggle = { viewModel.toggleFavorite(it) },
                                onWatchLaterToggle = { viewModel.toggleWatchLater(it) }
                            )
                        }
                    }
                }

                FavoritesTab.WATCH_LATER -> {
                    if (state.watchLaterList.isEmpty()) {
                        item { EmptyState("No watch later items saved.") }
                    } else {
                        items(state.watchLaterList, key = { "wl_${it.id}" }) { media ->
                            MediaItemCard(
                                media = media,
                                onPlayClick = onPlayMedia,
                                onFavoriteToggle = { viewModel.toggleFavorite(it) },
                                onWatchLaterToggle = { viewModel.toggleWatchLater(it) }
                            )
                        }
                    }
                }

                FavoritesTab.PLAYLISTS -> {
                    if (state.playlists.isEmpty()) {
                        item { EmptyState("No playlists created.") }
                    } else {
                        items(state.playlists, key = { "pl_${it.id}" }) { playlist ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .glassCard(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)),
                                shape = RoundedCornerShape(18.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlaylistPlay,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = playlist.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (playlist.description.isNotEmpty()) {
                                            Text(
                                                text = playlist.description,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    IconButton(onClick = { viewModel.deletePlaylist(playlist.id) }) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteSweep,
                                            contentDescription = "Delete",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                FavoritesTab.HISTORY -> {
                    if (state.watchHistory.isEmpty()) {
                        item { EmptyState("No watch history records.") }
                    } else {
                        items(state.watchHistory, key = { "hist_${it.historyId}" }) { hist ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .glassCard(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = hist.mediaTitle,
                                            style = MaterialTheme.typography.titleMedium,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "Position: ${hist.positionMs / 1000}s",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreatePlaylistDialog) {
        var playlistName by remember { mutableStateOf("") }
        var playlistDesc by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showCreatePlaylistDialog = false },
            title = { Text("Create New Playlist") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = playlistName,
                        onValueChange = { playlistName = it },
                        label = { Text("Playlist Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = playlistDesc,
                        onValueChange = { playlistDesc = it },
                        label = { Text("Description (Optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (playlistName.isNotBlank()) {
                            viewModel.createPlaylist(playlistName, playlistDesc)
                            showCreatePlaylistDialog = false
                        }
                    }
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreatePlaylistDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun EmptyState(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
