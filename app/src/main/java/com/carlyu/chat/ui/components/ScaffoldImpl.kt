package com.carlyu.chat.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.carlyu.chat.models.data.BottomSheetContent.About
import com.carlyu.chat.models.data.BottomSheetContent.CheckUpdates
import com.carlyu.chat.ui.views.navigation.Screen
import com.carlyu.chat.ui.views.screens.FavouriteScreen
import com.carlyu.chat.ui.views.screens.HomeScreen
import com.carlyu.chat.ui.views.screens.PreferenceScreen
import com.carlyu.chat.viewmodels.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldLayout() {
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val currentScreen = remember { mutableStateOf<Screen>(Screen.HomeScreen) }
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val sheetState = rememberModalBottomSheetState()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                ),
                title = {
                    Text("Chat Demo")
                },
            )
        },
        bottomBar = {
            BottomBar(
                currentScreen = currentScreen.value,
                onScreenSelected = { screen -> currentScreen.value = screen }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues)
        ) {
            // navigationGraph.Create()
            when (currentScreen.value) {
                Screen.HomeScreen -> HomeScreen()
                Screen.Favourite -> FavouriteScreen()
                Screen.Settings -> PreferenceScreen(settingsViewModel = settingsViewModel)
            }
        }
        if (settingsViewModel.bottomSheetState.value) {
            ModalBottomSheet(
                onDismissRequest = {
                    settingsViewModel.bottomSheetState.value = false
                },
                sheetState = sheetState
            ) {
                // Sheet content
                when (settingsViewModel.bottomSheetContent.value) {
                    CheckUpdates -> {
                        BottomSheetCheckUpdateContent(settingsViewModel, sheetState)
                    }

                    About -> {}
                    null -> {}
                }
            }
        }
    }
}


@Composable
fun BottomBar(
    //navController: NavHostController,
    //state: MutableState<Boolean>,
    //modifier: Modifier = Modifier
    currentScreen: Screen,
    onScreenSelected: (Screen) -> Unit
) {
    val screens = listOf(
        Screen.HomeScreen, Screen.Favourite, Screen.Settings
    )

    NavigationBar {
        screens.forEach { screen ->
            NavigationBarItem(
                label = { Text(text = screen.title) },
                icon = { Icon(imageVector = screen.icon, contentDescription = screen.title) },
                selected = currentScreen == screen,
                onClick = { onScreenSelected(screen) }
            )
        }
    }


    /*    NavigationBar(modifier = modifier) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            screens.forEach { screen ->
                val context = LocalContext.current
                NavigationBarItem(
                    label = {
                        Text(text = screen.title!!)
                    },
                    icon = {
                        Icon(imageVector = screen.icon!!, contentDescription = "Test")
                    },
                    selected = currentRoute == screen.route,
                    onClick = {
                        navController.navigate(screen.route) {
                            ToastUtils.showToast(context, "Navigating to ${screen.route}")
                            popUpTo(navController.graph.startDestinationId) {
                                //saveState = true
                                inclusive = true
                            }
                            launchSingleTop = false
                            //restoreState = true
                        }
                    },
                )
            }
        }*/
}
