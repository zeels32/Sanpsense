package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = BentoPurpleLight,
    onPrimary = BentoPurpleDark,
    primaryContainer = Color(0xFF381E72),
    onPrimaryContainer = BentoPurpleLight,
    secondary = BentoAiBlueLight,
    onSecondary = Color(0xFF003258),
    secondaryContainer = Color(0xFF172554),
    onSecondaryContainer = Color(0xFFBFDBFE),
    background = BentoBgDark,
    onBackground = BentoTextPrimaryDark,
    surface = BentoSurfaceDark,
    onSurface = BentoTextPrimaryDark,
    surfaceVariant = BentoCardMutedDark,
    onSurfaceVariant = BentoTextSecondaryDark,
    outline = BentoBorderDark,
    outlineVariant = Color(0xFF282834)
  )

private val LightColorScheme =
  lightColorScheme(
    primary = BentoPurplePrimary,
    onPrimary = Color.White,
    primaryContainer = BentoPurpleContainer,
    onPrimaryContainer = BentoPurpleDark,
    secondary = BentoAiBluePrimary,
    onSecondary = Color.White,
    secondaryContainer = BentoCardAiBlue,
    onSecondaryContainer = BentoAiBlueText,
    background = BentoBgLight,
    onBackground = BentoTextPrimaryLight,
    surface = BentoSurfaceLight,
    onSurface = BentoTextPrimaryLight,
    surfaceVariant = BentoCardMutedLight,
    onSurfaceVariant = BentoTextSecondaryLight,
    outline = BentoBorderLight,
    outlineVariant = Color(0xFFEDE8EC)
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  val bentoColors = if (darkTheme) BentoDarkColors else BentoLightColors

  CompositionLocalProvider(LocalBentoColors provides bentoColors) {
    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
  }
}

