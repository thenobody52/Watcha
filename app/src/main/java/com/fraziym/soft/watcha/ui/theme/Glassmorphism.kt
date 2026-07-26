package com.fraziym.soft.watcha.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.glassmorphism(
    shape: Shape = RoundedCornerShape(16.dp),
    borderWidth: Dp = 1.dp,
    isDark: Boolean = isSystemInDarkTheme()
): Modifier {
    val backgroundColor = if (isDark) GlassDarkBg else GlassLightBg
    val borderColor = if (isDark) DarkBorder else LightBorder

    return this
        .clip(shape)
        .background(backgroundColor, shape)
        .border(borderWidth, borderColor, shape)
}

@Composable
fun Modifier.glassCard(
    shape: Shape = RoundedCornerShape(20.dp),
    isDark: Boolean = isSystemInDarkTheme()
): Modifier {
    val bg = if (isDark) OrganicSurface else LightSurface
    val border = if (isDark) DarkBorder else LightBorder
    return this
        .clip(shape)
        .background(bg)
        .border(1.dp, border, shape)
}

