package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = BrandBlue,
    onPrimary = Color.White,
    primaryContainer = BrandBlueLight.copy(alpha = 0.35f),
    onPrimaryContainer = BrandBlueDark,
    secondary = SunAmber,
    onSecondary = Color(0xFF3E2723),
    secondaryContainer = SunGold.copy(alpha = 0.25f),
    onSecondaryContainer = Color(0xFF78350F),
    tertiary = LeafGreen,
    onTertiary = Color.White,
    tertiaryContainer = MintGreenLight,
    onTertiaryContainer = LeafGreenDark,
    background = KidBgLight,
    onBackground = KidTextPrimary,
    surface = KidCardBg,
    onSurface = KidTextPrimary,
    surfaceVariant = Color(0xFFEDF2F7),
    onSurfaceVariant = KidTextSecondary,
    outline = KidCardBorder,
    error = CandyRed,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = BrandBlueLight,
    onPrimary = Color(0xFF00227B),
    primaryContainer = BrandBlueDark,
    onPrimaryContainer = Color.White,
    secondary = SunGold,
    onSecondary = Color(0xFF3E2723),
    secondaryContainer = Color(0xFF78350F),
    onSecondaryContainer = SunGold,
    tertiary = LeafGreen,
    onTertiary = Color.Black,
    background = DarkBg,
    onBackground = Color(0xFFF1F5F9),
    surface = DarkSurface,
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Color(0xFF475569),
    error = CandyRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
