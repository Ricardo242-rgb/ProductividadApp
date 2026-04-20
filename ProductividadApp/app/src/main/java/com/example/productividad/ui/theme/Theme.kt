package com.example.productividad.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val GymDarkColorScheme = darkColorScheme(
    primary = AccentGreen,
    secondary = SuccessGreen,
    tertiary = AccentGreenHover,
    background = CharcoalBlack,
    surface = DarkGray,
    onPrimary = PureWhite,
    onBackground = PureWhite,
    onSurface = PureWhite
)

private val GymLightColorScheme = lightColorScheme(
    primary = AccentBlue,
    secondary = SuccessGreen,
    tertiary = AccentBlueHover,
    background = LightBackground,
    surface = LightSurface,
    onPrimary = PureWhite,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight
)

@Composable
fun ProductividadAppTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val scheme = if (darkTheme) GymDarkColorScheme else GymLightColorScheme
    MaterialTheme(
        colorScheme = scheme,
        typography = Typography,
        content = content
    )
}
