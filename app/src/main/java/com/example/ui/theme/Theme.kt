package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = GoldAccent,
    secondary = GoldMuted,
    background = AmmoBlack,
    surface = SurfaceDark,
    onPrimary = AmmoBlack,
    onSecondary = AmmoBlack,
    onBackground = TextLight,
    onSurface = TextLight,
    surfaceVariant = SecondaryDark,
    onSurfaceVariant = TextMuted
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force dark theme for sleep/bedtime eye safety
    dynamicColor: Boolean = false, // Disable dynamic colors to preserve our masterfully-crafted black/gold brand identity
    content: @Composable () -> Unit,
) {
    val colorScheme = DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
