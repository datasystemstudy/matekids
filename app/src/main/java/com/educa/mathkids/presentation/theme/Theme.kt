package com.educa.mathkids.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    onPrimary = SurfaceWhite,
    primaryContainer = BlueLight,
    onPrimaryContainer = BluePrimaryDark,
    secondary = OrangeSecondary,
    onSecondary = SurfaceWhite,
    secondaryContainer = OrangeLight,
    onSecondaryContainer = OrangeSecondaryDark,
    tertiary = MintGreen,
    onTertiary = SurfaceWhite,
    tertiaryContainer = BlueLight,
    onTertiaryContainer = BlueDark,
    background = BackgroundCream,
    onBackground = TextDark,
    surface = SurfaceWhite,
    onSurface = TextDark,
    surfaceVariant = SlateLight,
    onSurfaceVariant = TextMuted,
    outline = BorderSoft,
    error = CoralEncouragement,
    onError = SurfaceWhite
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF64B5F6),
    onPrimary = Color(0xFF0D47A1),
    primaryContainer = Color(0xFF1565C0),
    onPrimaryContainer = Color(0xFFE3F2FD),
    secondary = Color(0xFFFFB74D),
    onSecondary = Color(0xFFE65100),
    secondaryContainer = Color(0xFFF57C00),
    onSecondaryContainer = Color(0xFFFFF3E0),
    tertiary = Color(0xFF80CBC4),
    onTertiary = Color(0xFF004D40),
    tertiaryContainer = Color(0xFF00695C),
    onTertiaryContainer = Color(0xFFE0F2F1),
    background = Color(0xFF0F172A), // Slate 900
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF1E293B), // Slate 800
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF475569),
    error = Color(0xFFFF8A65),
    onError = Color(0xFF3E1200)
)

@Composable
fun MathKidsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
