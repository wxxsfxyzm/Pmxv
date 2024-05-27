package com.carlyu.chat.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.carlyu.chat.viewmodels.SettingsViewModel

class NavigationGraphs(
    private val navController: NavHostController,
    private val settingsViewModel: SettingsViewModel,
) {

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun Create() {
        NavHost(
            navController, startDestination = Screen.HomeScreen.route
        ) {

            /*            composable(Screen.HomeScreen.route) {
                            HomeScreen()
                        }
                        composable(Screen.Favourite.route) {
                            FavouriteScreen()
                        }
                        composable(Screen.Settings.route) {
                            PreferenceScreen(settingsViewModel)
                        }*/
        }
    }
}