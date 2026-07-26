package com.fraziym.soft.watcha.ui.screens.library

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

enum class LibraryTab {
    VIDEOS, AUDIO, FOLDERS
}

enum class SortOption {
    NAME, DATE, SIZE
}

data class FolderGroup(
    val folderName: String,
    val folderPath: String,
    val items: List<MediaItemModel>
)

private data class Quad<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

data class LibraryUiState(
    val selectedTab: LibraryTab = LibraryTab.VIDEOS,
    val videos: List<MediaItemModel> = emptyList(),
    val audios: List<MediaItemModel> = emptyList(),
    val folders: List<FolderGroup> = emptyList(),
    val selectedFormatFilter: String? = null,
    val sortOption: SortOption = SortOption.NAME,
    val isScanning: Boolean = false
)

class LibraryViewModel(private val repository: MediaRepository) : ViewModel() {

    private val _selectedTab = MutableStateFlow(LibraryTab.VIDEOS)
    private val _selectedFormatFilter = MutableStateFlow<String?>(null)
    private val _sortOption = MutableStateFlow(SortOption.NAME)
    private val _isScanning = MutableStateFlow(false)

    @Suppress("UNCHECKED_CAST")
    val uiState: StateFlow<LibraryUiState> = combine(
        combine(repository.allVideos, repository.allAudio) { v, a -> Pair(v, a) },
        combine(_selectedTab, _selectedFormatFilter, _sortOption, _isScanning) { tab, filter, sort, scanning ->
            Quad(tab, filter, sort, scanning)
        }
    ) { (videos, audios), (tab, formatFilter, sortOpt, scanning) ->

        val filteredVideos = filterAndSort(videos, formatFilter, sortOpt)
        val filteredAudios = filterAndSort(audios, formatFilter, sortOpt)

        val folderMap = (videos + audios).groupBy { it.folderName }
        val folderGroups = folderMap.map { (name, items) ->
            FolderGroup(
                folderName = if (name.isEmpty()) "Internal Storage" else name,
                folderPath = items.firstOrNull()?.path ?: "/storage",
                items = items
            )
        }

        LibraryUiState(
            selectedTab = tab,
            videos = filteredVideos,
            audios = filteredAudios,
            folders = folderGroups,
            selectedFormatFilter = formatFilter,
            sortOption = sortOpt,
            isScanning = scanning
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LibraryUiState()
    )

    fun setTab(tab: LibraryTab) {
        _selectedTab.value = tab
    }

    fun setFormatFilter(format: String?) {
        _selectedFormatFilter.value = if (_selectedFormatFilter.value == format) null else format
    }

    fun setSortOption(option: SortOption) {
        _sortOption.value = option
    }

    fun scanLibrary() {
        viewModelScope.launch {
            _isScanning.value = true
            repository.scanMediaLibrary()
            _isScanning.value = false
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

    private fun filterAndSort(
        list: List<MediaItemModel>,
        formatFilter: String?,
        sortOpt: SortOption
    ): List<MediaItemModel> {
        var result = if (formatFilter != null) {
            list.filter { it.format.equals(formatFilter, ignoreCase = true) }
        } else list

        result = when (sortOpt) {
            SortOption.NAME -> result.sortedBy { it.title }
            SortOption.DATE -> result.sortedByDescending { it.dateAdded }
            SortOption.SIZE -> result.sortedByDescending { it.sizeBytes }
        }
        return result
    }
}
