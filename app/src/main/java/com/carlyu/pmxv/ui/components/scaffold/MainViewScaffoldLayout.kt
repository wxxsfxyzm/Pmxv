package com.carlyu.pmxv.ui.components.scaffold

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carlyu.pmxv.R
import com.carlyu.pmxv.models.data.BottomSheetContent
import com.carlyu.pmxv.ui.components.bottomsheet.BottomSheetCheckUpdateContent
import com.carlyu.pmxv.ui.theme.PmxvTheme
import com.carlyu.pmxv.ui.views.navigation.Screen
import com.carlyu.pmxv.ui.views.screens.mainViewScreen.HomeScreen
import com.carlyu.pmxv.ui.views.screens.mainViewScreen.PreferenceScreen
import com.carlyu.pmxv.ui.views.uistate.SettingsUiState
import com.carlyu.pmxv.ui.views.viewmodels.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainViewScaffoldLayout(
    viewModel: SettingsViewModel
) {
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val currentScreen = remember { mutableStateOf<Screen>(Screen.HomeScreen) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
                    Text(stringResource(id = R.string.app_name))
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
            when (currentScreen.value) {
                Screen.HomeScreen -> HomeScreen()
                Screen.Favourite -> TODO("middle Screen")//FavouriteScreen()
                Screen.Settings -> PreferenceScreen(settingsViewModel = viewModel)
            }
        }
        // 统一处理BottomSheet状态
        when (uiState) {
            is SettingsUiState.Success -> {
                val successState = uiState as SettingsUiState.Success
                if (successState.bottomSheetVisible) {
                    ModalBottomSheet(
                        modifier = Modifier.fillMaxHeight(),
                        contentWindowInsets = { WindowInsets.systemBars },
                        //containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                        onDismissRequest = { viewModel.dismissBottomSheet() },
                        scrimColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f),
                        sheetState = sheetState,
                    ) {
                        when (val content = successState.bottomSheetContent) {
                            is BottomSheetContent.CheckUpdates ->
                                BottomSheetCheckUpdateContent(
                                    settingsViewModel = viewModel,
                                    content = content
                                )

                            is BottomSheetContent.Confirmation ->
                                Unit
                            // 添加其他类型处理
                            null -> Unit
                        }
                    }
                }
            }

            else -> Unit
        }

    }
}

@Composable
private fun BottomBar(
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
}

@Preview(showBackground = true, name = "All Set Step Preview")
@Composable
fun BottomBarPreview(){
    PmxvTheme(darkTheme = false, dynamicColor = true){
        BottomBar(
            currentScreen = Screen.Settings,
            onScreenSelected = {}
        )
    }
}
