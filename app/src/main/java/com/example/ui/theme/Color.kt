package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Brand Accents
val BentoPurplePrimary = Color(0xFF6750A4)
val BentoPurpleContainer = Color(0xFFEADDFF)
val BentoPurpleLight = Color(0xFFD0BCFF)
val BentoPurpleDark = Color(0xFF21005D)
val BentoPurpleText = Color(0xFF381E72)

val BentoAiBluePrimary = Color(0xFF0061A4)
val BentoAiBlueLight = Color(0xFF38BDF8)
val BentoCardAiBlue = Color(0xFFD1E4FF)
val BentoAiBlueText = Color(0xFF001D36)

val BentoGreenActive = Color(0xFF2E7D32)
val BentoGreenActiveDark = Color(0xFF4ADE80)

// Light Theme Palette
val BentoBgLight = Color(0xFFF8F9FA)
val BentoSurfaceLight = Color(0xFFFFFFFF)
val BentoCardMutedLight = Color(0xFFF1F3F5)
val BentoCardMetaLight = Color(0xFFE9ECEF)
val BentoTextPrimaryLight = Color(0xFF191C1E)
val BentoTextSecondaryLight = Color(0xFF49454F)
val BentoBorderLight = Color(0xFFE2DCE0)

// Dark Theme Palette
val BentoBgDark = Color(0xFF0F0F13)
val BentoSurfaceDark = Color(0xFF1B1B22)
val BentoCardMutedDark = Color(0xFF252530)
val BentoCardMetaDark = Color(0xFF2E2E3A)
val BentoTextPrimaryDark = Color(0xFFF3F4F6)
val BentoTextSecondaryDark = Color(0xFF9CA3AF)
val BentoBorderDark = Color(0xFF353542)

// Backward-compatibility defaults (Light)
val BentoBg = BentoBgLight
val BentoSurface = BentoSurfaceLight
val BentoCardMuted = BentoCardMutedLight
val BentoCardMeta = BentoCardMetaLight
val BentoTextPrimary = BentoTextPrimaryLight
val BentoTextSecondary = BentoTextSecondaryLight
val BentoBorder = BentoBorderLight

@Immutable
data class BentoColorScheme(
    val isDark: Boolean,
    val bg: Color,
    val surface: Color,
    val cardMuted: Color,
    val cardMeta: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val border: Color,
    val purplePrimary: Color,
    val purpleContainer: Color,
    val purpleText: Color,
    val purpleLight: Color,
    val purpleDark: Color,
    val aiBluePrimary: Color,
    val aiBlueContainer: Color,
    val aiBlueText: Color,
    val greenActive: Color,
    val greenContainer: Color,
    val cardHighlight: Color,
    val errorBg: Color,
    val errorBorder: Color,
    val errorText: Color,
    val navBarBg: Color,
    val navBarBorder: Color
) {
    val cardBg: Color get() = surface
    val cardAiBlue: Color get() = aiBlueContainer
}

val BentoLightColors = BentoColorScheme(
    isDark = false,
    bg = BentoBgLight,
    surface = BentoSurfaceLight,
    cardMuted = BentoCardMutedLight,
    cardMeta = BentoCardMetaLight,
    textPrimary = BentoTextPrimaryLight,
    textSecondary = BentoTextSecondaryLight,
    border = BentoBorderLight,
    purplePrimary = BentoPurplePrimary,
    purpleContainer = BentoPurpleContainer,
    purpleText = BentoPurpleText,
    purpleLight = BentoPurpleLight,
    purpleDark = BentoPurpleDark,
    aiBluePrimary = BentoAiBluePrimary,
    aiBlueContainer = BentoCardAiBlue,
    aiBlueText = BentoAiBlueText,
    greenActive = BentoGreenActive,
    greenContainer = Color(0xFFDCFCE7),
    cardHighlight = Color(0xFFF3E8FF),
    errorBg = Color(0xFFFFF1F1),
    errorBorder = Color(0xFFFFCDD2),
    errorText = Color(0xFFB71C1C),
    navBarBg = Color.White,
    navBarBorder = BentoBorderLight
)

val BentoDarkColors = BentoColorScheme(
    isDark = true,
    bg = BentoBgDark,
    surface = BentoSurfaceDark,
    cardMuted = BentoCardMutedDark,
    cardMeta = BentoCardMetaDark,
    textPrimary = BentoTextPrimaryDark,
    textSecondary = BentoTextSecondaryDark,
    border = BentoBorderDark,
    purplePrimary = BentoPurpleLight,
    purpleContainer = Color(0xFF381E72),
    purpleText = BentoPurpleLight,
    purpleLight = Color(0xFFEADDFF),
    purpleDark = Color(0xFF21005D),
    aiBluePrimary = BentoAiBlueLight,
    aiBlueContainer = Color(0xFF172554),
    aiBlueText = Color(0xFFBFDBFE),
    greenActive = BentoGreenActiveDark,
    greenContainer = Color(0xFF064E3B),
    cardHighlight = Color(0xFF261C3D),
    errorBg = Color(0xFF3B1214),
    errorBorder = Color(0xFF7F1D1D),
    errorText = Color(0xFFFCA5A5),
    navBarBg = BentoSurfaceDark,
    navBarBorder = BentoBorderDark
)

val LocalBentoColors = staticCompositionLocalOf { BentoLightColors }

object BentoTheme {
    val colors: BentoColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalBentoColors.current
}


