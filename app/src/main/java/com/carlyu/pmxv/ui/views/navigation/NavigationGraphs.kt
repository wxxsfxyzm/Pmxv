package com.carlyu.pmxv.ui.views.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.carlyu.pmxv.ui.views.viewmodels.SettingsViewModel

class NavigationGraphs(
    private val navController: NavHostController,
    private val settingsViewModel: SettingsViewModel,
) {

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