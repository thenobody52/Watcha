package com.fraziym.soft.watcha.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.fraziym.soft.watcha.ui.navigation.Screen
import com.fraziym.soft.watcha.ui.theme.DarkBorder
import com.fraziym.soft.watcha.ui.theme.GlassDarkBg
import com.fraziym.soft.watcha.ui.theme.GlassLightBg
import com.fraziym.soft.watcha.ui.theme.LightBorder

enum class NavDestination(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val tag: String
) {
    HOME(Screen.Home.route, "Home", Icons.Filled.Home, Icons.Outlined.Home, "nav_home"),
    LIBRARY(Screen.Library.route, "Library", Icons.Filled.VideoLibrary, Icons.Outlined.VideoLibrary, "nav_library"),
    PLAYER(Screen.Player.route, "Player", Icons.Filled.PlayCircle, Icons.Outlined.PlayCircle, "nav_player"),
    FAVORITES(Screen.Favorites.route, "Favorites", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder, "nav_favorites"),
    SETTINGS(Screen.Settings.route, "Settings", Icons.Filled.Settings, Icons.Outlined.Settings, "nav_settings")
}

@Composable
fun FloatingGlassNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme()
) {
    val navShape = RoundedCornerShape(32.dp)
    val glassBg = if (isDark) GlassDarkBg.copy(alpha = 0.88f) else GlassLightBg.copy(alpha = 0.92f)
    val glassBorder = if (isDark) DarkBorder else LightBorder

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(navShape)
                .background(glassBg)
                .border(1.dp, glassBorder, navShape)
                .padding(horizontal = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavDestination.values().forEach { destination ->
                val isSelected = currentRoute == destination.route

                val activeColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    animationSpec = tween(250),
                    label = "nav_icon_color"
                )

                val pillBg by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.18f) else Color.Transparent,
                    animationSpec = tween(250),
                    label = "nav_pill_color"
                )

                Box(
                    modifier = Modifier
                        .testTag(destination.tag)
                        .clip(CircleShape)
                        .background(pillBg)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onNavigate(destination.route) }
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                            contentDescription = destination.label,
                            tint = activeColor,
                            modifier = Modifier.size(20.dp)
                        )
                        if (isSelected) {
                            Text(
                                text = destination.label,
                                style = MaterialTheme.typography.labelSmall,
                                color = activeColor
                            )
                        }
                    }
                }
            }
        }
    }
}
