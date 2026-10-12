package com.stadio.kitabu.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val KitabuColorScheme = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = GreenOnPrimary,
    primaryContainer = GreenPrimaryContainer,
    onPrimaryContainer = GreenOnPrimaryContainer,
    secondary = SageSecondary,
    secondaryContainer = SageSecondaryContainer,
    onSecondaryContainer = SageOnSecondaryContainer,
    tertiary = TealTertiary,
    tertiaryContainer = TealTertiaryContainer,
    background = LeafBackground,
    surface = LeafSurface,
    onSurface = LeafOnSurface,
    surfaceVariant = LeafSurfaceVariant,
    onSurfaceVariant = LeafOnSurfaceVariant,
    outline = LeafOutline,
    surfaceContainer = LeafNavBar,
    surfaceContainerLow = LeafSurface
)

/**
 * App theme: green colour scheme + serif/sans-serif typography.
 */
@Composable
fun KitabuTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = KitabuColorScheme,
        typography = KitabuTypography,
        content = content
    )
}
