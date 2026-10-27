package com.ekichochan.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFCF79),
    secondary = Color(0xFFAECBDD),
    tertiary = Color(0xFFFFB4AB),
    background = Color(0xFF111827),
    surface = Color(0xFF202B3D),
    primaryContainer = Color(0xFF4C391D),
    onPrimaryContainer = Color(0xFFFFE5B5)
)

private val LightColorScheme = lightColorScheme(
    primary = WebRed,
    onPrimary = Color.White,
    secondary = WebNavy,
    tertiary = Color(0xFF765000),
    background = Color(0xFFBBE4FA),
    surface = Color(0xFFFFFCF5),
    primaryContainer = Color(0xFFFFEBC4),
    onPrimaryContainer = WebNavy,
    secondaryContainer = Color(0xFFE6F3F7),
    onSecondaryContainer = WebNavy

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun EkichoChanTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
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
