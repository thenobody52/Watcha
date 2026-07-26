package com.fraziym.soft.watcha

import android.app.Application
import com.fraziym.soft.watcha.data.local.SettingsDataStore
import com.fraziym.soft.watcha.data.local.WatchaDatabase
import com.fraziym.soft.watcha.data.repository.MediaRepository
import com.fraziym.soft.watcha.data.repository.SettingsRepository
import com.fraziym.soft.watcha.data.scanner.MediaScanner
import com.fraziym.soft.watcha.player.WatchaPlayerManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WatchaApp : Application() {

    lateinit var database: WatchaDatabase
        private set

    lateinit var settingsDataStore: SettingsDataStore
        private set

    lateinit var mediaRepository: MediaRepository
        private set

    lateinit var settingsRepository: SettingsRepository
        private set

    lateinit var playerManager: WatchaPlayerManager
        private set

    override fun onCreate() {
        super.onCreate()

        database = WatchaDatabase.getInstance(this)
        settingsDataStore = SettingsDataStore(this)

        val scanner = MediaScanner(this)
        mediaRepository = MediaRepository(
            mediaDao = database.mediaDao(),
            playlistDao = database.playlistDao(),
            historyDao = database.historyDao(),
            scanner = scanner
        )

        settingsRepository = SettingsRepository(settingsDataStore)
        playerManager = WatchaPlayerManager(this)

        // Automatically scan library on cold start & listen to storage changes
        val appScope = CoroutineScope(Dispatchers.IO)
        appScope.launch {
            mediaRepository.scanMediaLibrary()
        }
        mediaRepository.startRealtimeStorageMonitoring(appScope)
    }
}
