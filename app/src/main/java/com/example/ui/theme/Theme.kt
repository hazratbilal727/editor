package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CapCutColorScheme = darkColorScheme(
  primary = CapCutTeal,
  onPrimary = Color.Black,
  primaryContainer = CapCutSurfaceVariant,
  onPrimaryContainer = CapCutTextPrimary,
  secondary = CapCutPurple,
  onSecondary = Color.White,
  background = CapCutBackground,
  onBackground = CapCutTextPrimary,
  surface = CapCutSurface,
  onSurface = CapCutTextPrimary,
  surfaceVariant = CapCutSurfaceVariant,
  onSurfaceVariant = CapCutTextSecondary
)

@Composable
fun CapCutTheme(
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = CapCutColorScheme,
    typography = Typography,
    content = content
  )
}
