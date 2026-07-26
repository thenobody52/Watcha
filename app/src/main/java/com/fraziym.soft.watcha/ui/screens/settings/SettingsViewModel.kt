package com.fraziym.soft.watcha.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fraziym.soft.watcha.data.local.UserPreferences
import com.fraziym.soft.watcha.data.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val settingsRepository: SettingsRepository) : ViewModel() {

    val userPreferences: StateFlow<UserPreferences> = settingsRepository.userPreferences.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserPreferences()
    )

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            settingsRepository.setThemeMode(mode)
        }
    }

    fun setHwDecodingEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setHwDecodingEnabled(enabled)
        }
    }

    fun setResumePlayback(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setResumePlayback(enabled)
        }
    }

    fun setDefaultSpeed(speed: Float) {
        viewModelScope.launch {
            settingsRepository.setDefaultSpeed(speed)
        }
    }

    fun setDoubleTapSeekSeconds(seconds: Int) {
        viewModelScope.launch {
            settingsRepository.setDoubleTapSeekSeconds(seconds)
        }
    }

    fun setBackgroundAudio(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setBackgroundAudio(enabled)
        }
    }

    fun setAutoPipEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setAutoPipEnabled(enabled)
        }
    }

    fun setGlassIntensity(intensity: Float) {
        viewModelScope.launch {
            settingsRepository.setGlassIntensity(intensity)
        }
    }
}
