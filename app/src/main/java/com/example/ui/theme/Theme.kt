package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = SaffronPrimary,
  onPrimary = Color.White,
  primaryContainer = SaffronDark,
  onPrimaryContainer = Color.White,
  secondary = RoyalIndigo,
  onSecondary = Color.White,
  secondaryContainer = IndigoDark,
  onSecondaryContainer = IndigoLight,
  tertiary = PeacockCyan,
  onTertiary = Color.Black,
  background = DarkBackground,
  onBackground = DarkOnBackground,
  surface = DarkSurface,
  onSurface = DarkOnSurface,
  surfaceVariant = DarkSurfaceVariant,
  onSurfaceVariant = DarkOnSurface,
  outline = DarkOutline
)

private val LightColorScheme = lightColorScheme(
  primary = SaffronPrimary,
  onPrimary = Color.White,
  primaryContainer = SaffronLight,
  onPrimaryContainer = Color(0xFF3B1500),
  secondary = RoyalIndigo,
  onSecondary = Color.White,
  secondaryContainer = IndigoLight,
  onSecondaryContainer = Color.White,
  tertiary = PeacockCyan,
  onTertiary = Color.White,
  background = LightBackground,
  onBackground = LightOnBackground,
  surface = LightSurface,
  onSurface = LightOnSurface,
  surfaceVariant = LightSurfaceVariant,
  onSurfaceVariant = LightOnSurface,
  outline = LightOutline
)

@Composable
fun ApnaSocialTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep custom brand identity intact
  content: @Composable () -> Unit
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
