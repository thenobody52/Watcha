package com.fraziym.soft.watcha.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Library : Screen("library")
    object Player : Screen("player")
    object Favorites : Screen("favorites")
    object Settings : Screen("settings")
}
