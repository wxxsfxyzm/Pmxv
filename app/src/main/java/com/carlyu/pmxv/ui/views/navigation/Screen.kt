package com.carlyu.pmxv.ui.views.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object HomeScreen : Screen(
        route = "home_screen",
        title = "Home",
        icon = Icons.Outlined.Home
    )

    data object Favourite : Screen(
        route = "favourite_screen",
        title = "Favorite",
        icon = Icons.Outlined.Favorite
    )

    data object Settings : Screen(
        route = "settings_screen",
        title = "Settings",
        icon = Icons.Filled.Settings
    )

}