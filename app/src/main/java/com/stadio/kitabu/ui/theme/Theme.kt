package com.stadio.kitabu.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val KitabuLightColorScheme = lightColorScheme(
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

private val KitabuDarkColorScheme = darkColorScheme(
    primary = GreenPrimaryContainer,
    onPrimary = GreenPrimary,
    secondary = SageSecondaryContainer,
    background = LeafOnSurface,
    surface = LeafOnSurfaceVariant
)

/**
 * App theme: green colour scheme with light/dark theme support + typography.
 */
@Composable
fun KitabuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) KitabuDarkColorScheme else KitabuLightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = KitabuTypography,
        content = content
    )
}
