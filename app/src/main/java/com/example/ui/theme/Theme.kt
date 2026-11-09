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
    primary = ElectricCyan,
    onPrimary = Color(0xFF00363D),
    primaryContainer = Color(0xFF004F58),
    onPrimaryContainer = Color(0xFF8CF8FF),

    secondary = CircuitEmerald,
    onSecondary = Color(0xFF003823),
    secondaryContainer = Color(0xFF005235),
    onSecondaryContainer = Color(0xFF8BF5BE),

    tertiary = AmberWarning,
    onTertiary = Color(0xFF452B00),
    tertiaryContainer = Color(0xFF633F00),
    onTertiaryContainer = Color(0xFFFFDDB3),

    background = CircuitNavyDark,
    onBackground = TechWhite,
    surface = CircuitNavySurface,
    onSurface = TechWhite,
    surfaceVariant = CircuitNavySurfaceVariant,
    onSurfaceVariant = TechTextSecondary,
    outline = CircuitBorder,
    error = DangerRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF006874),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF97F0FF),
    onPrimaryContainer = Color(0xFF001F24),

    secondary = Color(0xFF006D46),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF8EF6C0),
    onSecondaryContainer = Color(0xFF002112),

    tertiary = Color(0xFF855400),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFDDB7),
    onTertiaryContainer = Color(0xFF2B1700),

    background = Color(0xFFF1F5F9),
    onBackground = Color(0xFF0F172A),
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1),
    error = DangerRed,
    onError = Color.White
)

@Composable
fun CircuitGuruTheme(
    darkTheme: Boolean = true, // Default to deep dark lab mode for circuit glowing wires
    dynamicColor: Boolean = false, // Keep signature circuit look
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
