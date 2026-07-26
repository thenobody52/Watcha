package com.fraziym.soft.watcha.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "watcha_settings")

data class UserPreferences(
    val themeMode: String = "DARK", // DARK, LIGHT, SYSTEM
    val hwDecodingEnabled: Boolean = true,
    val resumePlayback: Boolean = true,
    val defaultSpeed: Float = 1.0f,
    val doubleTapSeekSeconds: Int = 10,
    val backgroundAudio: Boolean = false,
    val autoPipEnabled: Boolean = true,
    val subtitleFontSize: Int = 16,
    val glassIntensity: Float = 0.85f,
    val sleepTimerMinutes: Int = 0
)

class SettingsDataStore(private val context: Context) {

    companion object {
        val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
        val KEY_HW_DECODING = booleanPreferencesKey("hw_decoding")
        val KEY_RESUME_PLAYBACK = booleanPreferencesKey("resume_playback")
        val KEY_DEFAULT_SPEED = floatPreferencesKey("default_speed")
        val KEY_DOUBLE_TAP_SEEK = intPreferencesKey("double_tap_seek")
        val KEY_BG_AUDIO = booleanPreferencesKey("bg_audio")
        val KEY_AUTO_PIP = booleanPreferencesKey("auto_pip")
        val KEY_SUBTITLE_SIZE = intPreferencesKey("subtitle_size")
        val KEY_GLASS_INTENSITY = floatPreferencesKey("glass_intensity")
        val KEY_SLEEP_TIMER = intPreferencesKey("sleep_timer")
    }

    val userPreferences: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        UserPreferences(
            themeMode = prefs[KEY_THEME_MODE] ?: "DARK",
            hwDecodingEnabled = prefs[KEY_HW_DECODING] ?: true,
            resumePlayback = prefs[KEY_RESUME_PLAYBACK] ?: true,
            defaultSpeed = prefs[KEY_DEFAULT_SPEED] ?: 1.0f,
            doubleTapSeekSeconds = prefs[KEY_DOUBLE_TAP_SEEK] ?: 10,
            backgroundAudio = prefs[KEY_BG_AUDIO] ?: false,
            autoPipEnabled = prefs[KEY_AUTO_PIP] ?: true,
            subtitleFontSize = prefs[KEY_SUBTITLE_SIZE] ?: 16,
            glassIntensity = prefs[KEY_GLASS_INTENSITY] ?: 0.85f,
            sleepTimerMinutes = prefs[KEY_SLEEP_TIMER] ?: 0
        )
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { it[KEY_THEME_MODE] = mode }
    }

    suspend fun setHwDecodingEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_HW_DECODING] = enabled }
    }

    suspend fun setResumePlayback(enabled: Boolean) {
        context.dataStore.edit { it[KEY_RESUME_PLAYBACK] = enabled }
    }

    suspend fun setDefaultSpeed(speed: Float) {
        context.dataStore.edit { it[KEY_DEFAULT_SPEED] = speed }
    }

    suspend fun setDoubleTapSeekSeconds(seconds: Int) {
        context.dataStore.edit { it[KEY_DOUBLE_TAP_SEEK] = seconds }
    }

    suspend fun setBackgroundAudio(enabled: Boolean) {
        context.dataStore.edit { it[KEY_BG_AUDIO] = enabled }
    }

    suspend fun setAutoPipEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_AUTO_PIP] = enabled }
    }

    suspend fun setGlassIntensity(intensity: Float) {
        context.dataStore.edit { it[KEY_GLASS_INTENSITY] = intensity }
    }

    suspend fun setSleepTimerMinutes(minutes: Int) {
        context.dataStore.edit { it[KEY_SLEEP_TIMER] = minutes }
    }
}
