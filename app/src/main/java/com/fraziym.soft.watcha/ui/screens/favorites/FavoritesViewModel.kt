package com.fraziym.soft.watcha.ui.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fraziym.soft.watcha.data.local.entity.PlaylistEntity
import com.fraziym.soft.watcha.data.local.entity.WatchHistoryEntity
import com.fraziym.soft.watcha.data.repository.MediaRepository
import com.fraziym.soft.watcha.domain.model.MediaItemModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class FavoritesTab {
    FAVORITES, WATCH_LATER, PLAYLISTS, HISTORY
}

data class FavoritesUiState(
    val selectedTab: FavoritesTab = FavoritesTab.FAVORITES,
    val favoritesList: List<MediaItemModel> = emptyList(),
    val watchLaterList: List<MediaItemModel> = emptyList(),
    val playlists: List<PlaylistEntity> = emptyList(),
    val watchHistory: List<WatchHistoryEntity> = emptyList()
)

class FavoritesViewModel(private val repository: MediaRepository) : ViewModel() {

    private val _selectedTab = MutableStateFlow(FavoritesTab.FAVORITES)

    val uiState: StateFlow<FavoritesUiState> = combine(
        repository.favoriteMedia,
        repository.watchLaterMedia,
        repository.playlists,
        repository.watchHistory,
        _selectedTab
    ) { favorites, watchLater, playlists, history, tab ->
        FavoritesUiState(
            selectedTab = tab,
            favoritesList = favorites,
            watchLaterList = watchLater,
            playlists = playlists,
            watchHistory = history
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FavoritesUiState()
    )

    fun setTab(tab: FavoritesTab) {
        _selectedTab.value = tab
    }

    fun createPlaylist(name: String, description: String = "") {
        viewModelScope.launch {
            repository.createPlaylist(name, description)
        }
    }

    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch {
            repository.deletePlaylist(playlistId)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    fun toggleFavorite(media: MediaItemModel) {
        viewModelScope.launch {
            repository.toggleFavorite(media.id, !media.isFavorite)
        }
    }

    fun toggleWatchLater(media: MediaItemModel) {
        viewModelScope.launch {
            repository.toggleWatchLater(media.id, !media.isWatchLater)
        }
    }
}
