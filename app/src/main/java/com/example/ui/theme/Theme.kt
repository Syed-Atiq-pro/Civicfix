package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SophisticatedDarkColorScheme = darkColorScheme(
  primary = Indigo500,
  onPrimary = Slate50,
  primaryContainer = Indigo600,
  onPrimaryContainer = Slate50,
  secondary = Cyan400,
  onSecondary = MidnightBackground,
  secondaryContainer = Cyan600,
  onSecondaryContainer = Slate50,
  tertiary = Emerald400,
  onTertiary = MidnightBackground,
  background = MidnightBackground,
  onBackground = Slate50,
  surface = Slate900,
  onSurface = Slate50,
  surfaceVariant = Slate800,
  onSurfaceVariant = Slate400,
  outline = GlassBorder,
  outlineVariant = GlassDarkBorder,
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Sophisticated Dark is dark-first
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = SophisticatedDarkColorScheme,
    typography = Typography,
    content = content
  )
}

