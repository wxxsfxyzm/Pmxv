package com.carlyu.chat.ui.theme

import androidx.activity.ComponentActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat


@Composable
fun ChatdemoTheme(
    //settingsViewModel: SettingsViewModel,
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean,
    content: @Composable () -> Unit
) {
    // Color Scheme Logic
    val context = LocalContext.current

    val colorScheme = when {
        dynamicColor -> {
            if (darkTheme) dynamicDarkColorScheme(context = context)
            else dynamicLightColorScheme(context = context)
        }

        darkTheme -> darkColorScheme()

        else -> lightColorScheme()
    }

    // Status Bar Color Logic
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as ComponentActivity).window
            //window.statusBarColor = colorScheme.primary.toArgb() // change color status bar here
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    // MaterialTheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}