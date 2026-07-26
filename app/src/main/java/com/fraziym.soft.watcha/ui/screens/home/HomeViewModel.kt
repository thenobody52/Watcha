package com.fraziym.soft.watcha.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fraziym.soft.watcha.data.repository.MediaRepository
import com.fraziym.soft.watcha.domain.model.MediaItemModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val continueWatchingList: List<MediaItemModel> = emptyList(),
    val recentlyAddedList: List<MediaItemModel> = emptyList(),
    val totalVideos: Int = 0,
    val totalAudios: Int = 0,
    val searchQuery: String = "",
    val isLoading: Boolean = false
)

class HomeViewModel(private val repository: MediaRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val uiState: StateFlow<HomeUiState> = combine(
        repository.continueWatching,
        repository.recentlyAdded,
        repository.allVideos,
        repository.allAudio,
        _searchQuery
    ) { continueList, recentList, videos, audios, query ->
        val filteredRecent = if (query.isBlank()) {
            recentList
        } else {
            recentList.filter { it.title.contains(query, ignoreCase = true) }
        }

        HomeUiState(
            continueWatchingList = continueList,
            recentlyAddedList = filteredRecent,
            totalVideos = videos.size,
            totalAudios = audios.size,
            searchQuery = query,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
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
