package com.fraziym.soft.watcha.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fraziym.soft.watcha.ui.components.FloatingGlassNavBar
import com.fraziym.soft.watcha.ui.screens.favorites.FavoritesScreen
import com.fraziym.soft.watcha.ui.screens.favorites.FavoritesViewModel
import com.fraziym.soft.watcha.ui.screens.home.HomeScreen
import com.fraziym.soft.watcha.ui.screens.home.HomeViewModel
import com.fraziym.soft.watcha.ui.screens.library.LibraryScreen
import com.fraziym.soft.watcha.ui.screens.library.LibraryViewModel
import com.fraziym.soft.watcha.ui.screens.player.PlayerScreen
import com.fraziym.soft.watcha.ui.screens.player.PlayerViewModel
import com.fraziym.soft.watcha.ui.screens.settings.SettingsScreen
import com.fraziym.soft.watcha.ui.screens.settings.SettingsViewModel

@Composable
fun WatchaMainNavigation(
    homeViewModel: HomeViewModel,
    libraryViewModel: LibraryViewModel,
    playerViewModel: PlayerViewModel,
    favoritesViewModel: FavoritesViewModel,
    settingsViewModel: SettingsViewModel,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    onEnterPip: () -> Unit = {},
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

    Scaffold(
        bottomBar = {
            if (currentRoute != Screen.Player.route) {
                FloatingGlassNavBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    isDark = isDarkTheme
                )
            }
        },
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        viewModel = homeViewModel,
                        onPlayMedia = { media ->
                            playerViewModel.playMedia(media)
                            navController.navigate(Screen.Player.route)
                        },
                        onThemeToggle = onThemeToggle,
                        isDarkTheme = isDarkTheme
                    )
                }

                composable(Screen.Library.route) {
                    LibraryScreen(
                        viewModel = libraryViewModel,
                        onPlayMedia = { media ->
                            playerViewModel.playMedia(media)
                            navController.navigate(Screen.Player.route)
                        },
                        onThemeToggle = onThemeToggle,
                        isDarkTheme = isDarkTheme
                    )
                }

                composable(Screen.Player.route) {
                    PlayerScreen(
                        viewModel = playerViewModel,
                        onEnterPip = onEnterPip,
                        onBackClick = { navController.navigateUp() }
                    )
                }

                composable(Screen.Favorites.route) {
                    FavoritesScreen(
                        viewModel = favoritesViewModel,
                        onPlayMedia = { media ->
                            playerViewModel.playMedia(media)
                            navController.navigate(Screen.Player.route)
                        },
                        onThemeToggle = onThemeToggle,
                        isDarkTheme = isDarkTheme
                    )
                }

                composable(Screen.Settings.route) {
                    SettingsScreen(
                        viewModel = settingsViewModel,
                        onThemeToggle = onThemeToggle,
                        isDarkTheme = isDarkTheme
                    )
                }
            }
        }
    }
}
