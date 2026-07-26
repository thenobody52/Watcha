package com.fraziym.soft.watcha

import android.app.PictureInPictureParams
import android.os.Build
import android.os.Bundle
import android.util.Rational
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.fraziym.soft.watcha.ui.navigation.WatchaMainNavigation
import com.fraziym.soft.watcha.ui.screens.favorites.FavoritesViewModel
import com.fraziym.soft.watcha.ui.screens.home.HomeViewModel
import com.fraziym.soft.watcha.ui.screens.library.LibraryViewModel
import com.fraziym.soft.watcha.ui.screens.player.PlayerViewModel
import com.fraziym.soft.watcha.ui.screens.settings.SettingsViewModel
import com.fraziym.soft.watcha.ui.theme.WatchaTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as WatchaApp

        setContent {
            val userPrefs by app.settingsRepository.userPreferences.collectAsState(initial = null)
            val systemInDark = isSystemInDarkTheme()

            var isDarkThemeOverride by remember { mutableStateOf<Boolean?>(null) }
            val currentIsDark = isDarkThemeOverride ?: when (userPrefs?.themeMode) {
                "LIGHT" -> false
                "DARK" -> true
                else -> systemInDark
            }

            val scope = rememberCoroutineScope()

            val homeViewModel = remember { HomeViewModel(app.mediaRepository) }
            val libraryViewModel = remember { LibraryViewModel(app.mediaRepository) }
            val playerViewModel = remember { PlayerViewModel(app.playerManager, app.mediaRepository) }
            val favoritesViewModel = remember { FavoritesViewModel(app.mediaRepository) }
            val settingsViewModel = remember { SettingsViewModel(app.settingsRepository) }

            WatchaTheme(darkTheme = currentIsDark) {
                WatchaMainNavigation(
                    homeViewModel = homeViewModel,
                    libraryViewModel = libraryViewModel,
                    playerViewModel = playerViewModel,
                    favoritesViewModel = favoritesViewModel,
                    settingsViewModel = settingsViewModel,
                    isDarkTheme = currentIsDark,
                    onThemeToggle = {
                        val next = !currentIsDark
                        isDarkThemeOverride = next
                        scope.launch {
                            app.settingsRepository.setThemeMode(if (next) "DARK" else "LIGHT")
                        }
                    },
                    onEnterPip = {
                        enterPipMode()
                    }
                )
            }
        }
    }

    private fun enterPipMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val params = PictureInPictureParams.Builder()
                    .setAspectRatio(Rational(16, 9))
                    .build()
                enterPictureInPictureMode(params)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
