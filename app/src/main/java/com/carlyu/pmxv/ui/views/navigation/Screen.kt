package com.carlyu.pmxv.ui.views.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector
import com.carlyu.pmxv.R

sealed class Screen(
    val route: String,     // 用于导航的路由
    val title: Int,     // 显示在底部导航栏中的标题
    val icon: ImageVector, // 显示在底部导航栏中的图标
    val index: Int         // 用于在底部导航栏中显示的索引
) {
    data object DashboardScreen : Screen(
        route = "dashboard_screen",
        title = R.string.dashboard_screen_title,
        icon = Icons.Outlined.Home,
        index = 0
    )

    data object Favourite : Screen(
        route = "favourite_screen",
        title = R.string.management_screen_title,
        icon = Icons.Outlined.Favorite,
        index = 1
    )

    data object Settings : Screen(
        route = "settings_screen",
        title = R.string.settings_screen_title,
        icon = Icons.Filled.Settings,
        index = 2
    )

}