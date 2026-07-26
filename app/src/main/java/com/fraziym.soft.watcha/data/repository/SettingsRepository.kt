package com.fraziym.soft.watcha.data.repository

import com.fraziym.soft.watcha.data.local.SettingsDataStore
import com.fraziym.soft.watcha.data.local.UserPreferences
import kotlinx.coroutines.flow.Flow

class SettingsRepository(private val settingsDataStore: SettingsDataStore) {

    val userPreferences: Flow<UserPreferences> = settingsDataStore.userPreferences

    suspend fun setThemeMode(mode: String) {
        settingsDataStore.setThemeMode(mode)
    }

    suspend fun setHwDecodingEnabled(enabled: Boolean) {
        settingsDataStore.setHwDecodingEnabled(enabled)
    }

    suspend fun setResumePlayback(enabled: Boolean) {
        settingsDataStore.setResumePlayback(enabled)
    }

    suspend fun setDefaultSpeed(speed: Float) {
        settingsDataStore.setDefaultSpeed(speed)
    }

    suspend fun setDoubleTapSeekSeconds(seconds: Int) {
        settingsDataStore.setDoubleTapSeekSeconds(seconds)
    }

    suspend fun setBackgroundAudio(enabled: Boolean) {
        settingsDataStore.setBackgroundAudio(enabled)
    }

    suspend fun setAutoPipEnabled(enabled: Boolean) {
        settingsDataStore.setAutoPipEnabled(enabled)
    }

    suspend fun setGlassIntensity(intensity: Float) {
        settingsDataStore.setGlassIntensity(intensity)
    }

    suspend fun setSleepTimerMinutes(minutes: Int) {
        settingsDataStore.setSleepTimerMinutes(minutes)
    }
}
