package com.carlyu.chat.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.carlyu.chat.viewmodels.SettingsViewModel
import com.carlyu.chat.views.FavouriteScreen
import com.carlyu.chat.views.HomeScreen
import com.carlyu.chat.views.PreferenceScreen

class NavigationGraphs(
    private val navController: NavHostController,
    private val settingsViewModel: SettingsViewModel
) {

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun Create() {
        NavHost(
            navController, startDestination = Destinations.HomeScreen.route
        ) {
            composable(Destinations.HomeScreen.route) {
                HomeScreen()
            }
            composable(Destinations.Favourite.route) {
                FavouriteScreen()
            }
            composable(Destinations.Settings.route) {
                PreferenceScreen(settingsViewModel)
            }
        }
    }
}