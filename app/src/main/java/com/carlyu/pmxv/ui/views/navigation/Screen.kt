package com.carlyu.pmxv.ui.views.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,     // 用于导航的路由
    val title: String,     // 显示在底部导航栏中的标题
    val icon: ImageVector, // 显示在底部导航栏中的图标
    val index: Int         // 用于在底部导航栏中显示的索引
) {
    data object HomeScreen : Screen(
        route = "home_screen",
        title = "Home",
        icon = Icons.Outlined.Home,
        index = 0
    )

    data object Favourite : Screen(
        route = "favourite_screen",
        title = "Favorite",
        icon = Icons.Outlined.Favorite,
        index = 1
    )

    data object Settings : Screen(
        route = "settings_screen",
        title = "Settings",
        icon = Icons.Filled.Settings,
        index = 2
    )

}