package com.carlyu.pmxv.ui.components.scaffold

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.carlyu.pmxv.R
import com.carlyu.pmxv.models.data.view.BottomSheetContentType
import com.carlyu.pmxv.ui.components.bottomsheet.BottomSheetCheckUpdateContent
import com.carlyu.pmxv.ui.theme.PmxvTheme
import com.carlyu.pmxv.ui.views.navigation.Screen
import com.carlyu.pmxv.ui.views.navigation.bottomNavScreens
import com.carlyu.pmxv.ui.views.screens.dashboardScreen.DashboardScreen
import com.carlyu.pmxv.ui.views.screens.mainViewScreen.PreferenceScreen
import com.carlyu.pmxv.ui.views.uistate.SettingsState
import com.carlyu.pmxv.ui.views.viewmodels.SettingsViewModel

/**
 * 主界面布局
 * @param viewModel 设置 ViewModel
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainViewScaffoldLayout(
    viewModel: SettingsViewModel
) {
    // val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val currentScreen = bottomNavScreens.find { it.route == currentDestination?.route }
        ?: Screen.DashboardScreen // 如果没有匹配项（例如，在初始化期间），则默认显示主屏幕。

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
                    Text(
                        text =
                            stringResource(id = currentScreen.title)
                    )
                },
            )
        },
        bottomBar = {
            BottomBar(
                navController = navController,
                currentDestination = currentDestination
            )
        }
    ) { paddingValues ->
        AppNavHost(
            navController = navController,
            viewModel = viewModel,
            modifier = Modifier.padding(paddingValues)
        )
        // 统一处理BottomSheet状态
        // Bottom Sheet 逻辑应该在Scaffold层级，或者确保其z-index更高
        // 当前它在Scaffold的content lambda的最后，所以它会绘制在screens之上。
        if (uiState is SettingsState.Success) {
            val successState = uiState as SettingsState.Success
            if (successState.bottomSheetVisible) {
                ModalBottomSheet(
                    modifier = Modifier.fillMaxHeight(), // 可扩展至状态栏
                    onDismissRequest = { viewModel.dismissBottomSheet() },
                    sheetState = sheetState,
                    // containerColor = MaterialTheme.colorScheme.surfaceContainerLow, // 可以尝试不同的surface颜色
                    scrimColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f),
                ) {
                    // Column确保内容从顶部开始，并且可以在底部留出导航栏空间（如果需要）
                    Column() {
                        when (val content = successState.bottomSheetContent) {
                            is BottomSheetContentType.CheckUpdates ->
                                BottomSheetCheckUpdateContent(
                                    settingsViewModel = viewModel,
                                    content = content
                                )

                            is BottomSheetContentType.Confirmation -> {
                                // Column(Modifier.padding(16.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                                //     Text("确认操作示例", style = MaterialTheme.typography.titleLarge)
                                //     Spacer(Modifier.height(16.dp))
                                //     Button(onClick = { viewModel.dismissBottomSheet() }) { Text("关闭") }
                                // }
                                Unit // Placeholder if content is null
                            }

                            null -> Unit // Placeholder if content is null
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.DashboardScreen.route, // Set your start destination
        modifier = modifier
    ) {
        bottomNavScreens.forEach { screen ->
            composable(
                route = screen.route,
                enterTransition = {
                    val initialIdx =
                        bottomNavScreens.indexOfFirst { it.route == initialState.destination.route }
                    val targetIdx =
                        bottomNavScreens.indexOfFirst { it.route == targetState.destination.route }

                    // If target index is greater than initial index, the new screen comes from the right
                    if (targetIdx > initialIdx) {
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> fullWidth },
                            animationSpec = tween(300)
                        ) +
                                fadeIn(animationSpec = tween(300))
                    }
                    // If target index is less than initial index, the new screen comes from the left
                    else if (targetIdx < initialIdx) {
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> -fullWidth },
                            animationSpec = tween(300)
                        ) +
                                fadeIn(animationSpec = tween(300))
                    }
                    // If indices are the same (e.g., initial load or navigating back to the same destination), just fade in
                    else {
                        fadeIn(animationSpec = tween(200))
                    }
                },
                exitTransition = {
                    // initialState.destination is the destination that is exiting
                    // targetState.destination is the destination that is entering
                    val initialIdx =
                        bottomNavScreens.indexOfFirst { it.route == initialState.destination.route } // Screen exiting
                    val targetIdx =
                        bottomNavScreens.indexOfFirst { it.route == targetState.destination.route }   // Screen entering

                    // If the entering screen's index is greater than the exiting screen's index (moving right overall)
                    // The exiting screen slides out to the left
                    if (targetIdx > initialIdx) {
                        slideOutHorizontally(
                            targetOffsetX = { fullWidth -> -fullWidth },
                            animationSpec = tween(300)
                        ) +
                                fadeOut(animationSpec = tween(300))
                    }
                    // If the entering screen's index is less than the exiting screen's index (moving left overall)
                    // The exiting screen slides out to the right
                    else if (targetIdx < initialIdx) {
                        slideOutHorizontally(
                            targetOffsetX = { fullWidth -> fullWidth },
                            animationSpec = tween(300)
                        ) +
                                fadeOut(animationSpec = tween(300))
                    }
                    // If indices are the same, just fade out
                    else {
                        fadeOut(animationSpec = tween(200))
                    }
                },
                // Pop transitions are for when you use the back button.
                // With your current popUpTo(inclusive = true) logic, the back stack is cleared,
                // so hitting back from a main screen exits the app, and these pop transitions mostly aren't used for *main* screen navigation.
                // If you had deeper navigation and used back, you'd define popEnter/popExit.
                // For symmetrical animation on back:
                // popEnterTransition = { targetState.destination.route.let { targetRoute ->
                //    val targetIdx = bottomNavScreens.indexOfFirst { it.route == targetRoute }
                //    if (targetIdx > initialState.destination.route?.let { bottomNavScreens.indexOfFirst { s -> s.route == it } } ?: -1) {
                //        slideInHorizontally(initialOffsetX = { fullWidth -> -fullWidth }, animationSpec = tween(300)) + fadeIn(animationSpec = tween(300))
                //    } else {
                //        slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }, animationSpec = tween(300)) + fadeIn(animationSpec = tween(300))
                //    }
                // }},
                // popExitTransition = { initialState.destination.route.let { initialRoute ->
                //     val initialIdx = bottomNavScreens.indexOfFirst { it.route == initialRoute }
                //     if (targetState.destination.route?.let { bottomNavScreens.indexOfFirst { s -> s.route == it } } ?: -1 > initialIdx) {
                //         slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth }, animationSpec = tween(300)) + fadeOut(animationSpec = tween(300))
                //     } else {
                //         slideOutHorizontally(targetOffsetX = { fullWidth -> -fullWidth }, animationSpec = tween(300)) + fadeOut(animationSpec = tween(300))
                //     }
                // }}
            ) {
                // Content for each screen
                when (screen) {
                    Screen.DashboardScreen -> DashboardScreen()
                    Screen.Favourite -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("收藏页面 (Jetpack Navigation)") // Updated text for clarity after migration
                        }
                    }

                    Screen.Settings -> PreferenceScreen(settingsViewModel = viewModel)
                }
            }
        }
    }
}


/**
 * 底部导航栏
 * @param navController NavHostController for navigation
 * @param currentDestination Current NavDestination to determine selection
 */
@Composable
private fun BottomBar(
    navController: NavHostController,
    currentDestination: androidx.navigation.NavDestination?
) {
    NavigationBar {
        bottomNavScreens.forEach { screen ->
            val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
            val animatedOffsetY by animateDpAsState(
                targetValue = if (selected) (-2).dp else 0.dp,
                animationSpec = tween(durationMillis = 200),
                label = "NavItemOffset"
            )
            val animatedIconSize by animateDpAsState(
                targetValue = if (selected) 26.dp else 24.dp,
                animationSpec = tween(200),
                label = "NavItemIconSize"
            )
            val animatedLabelScale by animateFloatAsState(
                targetValue = if (selected) 1.05f else 1.0f,
                animationSpec = tween(200),
                label = "NavItemLabelScale"
            )

            NavigationBarItem(
                modifier = Modifier
                    .offset(y = animatedOffsetY)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                label = {
                    Text(
                        // text = screen.title, // Consider using stringResource if title is dynamic
                        text = stringResource(
                            id = when (screen) { // Example using stringResource
                                Screen.DashboardScreen -> R.string.dashboard // Define these in strings.xml
                                Screen.Favourite -> R.string.management
                                Screen.Settings -> R.string.settings
                            }
                        ),
                        modifier = Modifier.graphicsLayer(
                            scaleX = animatedLabelScale,
                            scaleY = animatedLabelScale
                        )
                    )
                },
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = stringResource(id = screen.title),
                        modifier = Modifier.size(animatedIconSize)
                    )
                },
                selected = selected,
                onClick = {
                    if (!selected) {
                        // Navigate using the screen's route
                        navController.navigate(screen.route) {
                            // Pop up to the entire graph's root, effectively clearing the back stack for main screens
                            popUpTo(navController.graph.id) {
                                inclusive = true // Include the start destination itself
                            }
                            // Avoid building a stack of identical destinations when reselecting
                            launchSingleTop = true
                            // Not strictly needed with inclusive = true unless you handle savedState elsewhere
                            // restoreState = true
                        }
                    }
                },
                alwaysShowLabel = true,
            )
        }
    }
}

@Preview(name = "BottomBar Preview", showBackground = true)
@Composable
fun BottomBarPreview() {
    PmxvTheme(darkTheme = false, dynamicColor = true) {
        // BottomBar 需要 NavController 和 currentDestination
        // 对于复杂的预览，这可能有点棘手，通常会使用库或更高级的预览技术
        // 但对于基本外观，可以传递 null 或模拟值，尽管功能不完整
        // val navController = rememberNavController() // 仅用于满足参数要求
        // 模拟一个当前目的地
        // val navBackStackEntry by navController.currentBackStackEntryAsState() // 这在Preview中可能不会很好地工作
        // 更简单的方法是直接传递一个模拟的 NavDestination

        // 简单的做法是直接调用，并接受它在预览中可能功能不全
        // BottomBar(navController = navController, currentDestination = null)
        // 或者
        // BottomBar(navController = navController, currentDestination = navController.currentDestination)

        // 更可控的预览 (但仍旧不完全模拟导航状态):
        // 为了让某个项目被选中，我们需要模拟 currentDestination
        // 这部分比较复杂，因为 NavDestination 通常由导航库管理。
        // 对于 BottomBar 的纯视觉预览，可能只需关注其在不同选中状态下的外观。
        // 简单示例（注意：这不会有实际的导航行为）
        NavigationBar {
            // 直接创建几个 NavigationBarItem 来预览它们的外观
            val homeSelected = true
            val favSelected = false

            val animatedOffsetYHome by animateDpAsState(
                targetValue = if (homeSelected) (-4).dp else 0.dp,
                label = ""
            )
            val animatedIconSizeHome by animateDpAsState(
                targetValue = if (homeSelected) 26.dp else 24.dp,
                label = ""
            )
            NavigationBarItem(
                selected = homeSelected,
                onClick = { /*TODO*/ },
                modifier = Modifier.offset(y = animatedOffsetYHome),
                icon = {
                    Icon(
                        Screen.DashboardScreen.icon,
                        contentDescription = "Home",
                        modifier = Modifier.size(animatedIconSizeHome)
                    )
                },
                label = { Text(stringResource(id = R.string.home)) }
            )

            val animatedOffsetYFav by animateDpAsState(
                targetValue = if (favSelected) (-4).dp else 0.dp,
                label = ""
            )
            val animatedIconSizeFav by animateDpAsState(
                targetValue = if (favSelected) 26.dp else 24.dp,
                label = ""
            )
            NavigationBarItem(
                selected = favSelected,
                onClick = { /*TODO*/ },
                modifier = Modifier.offset(y = animatedOffsetYFav),
                icon = {
                    Icon(
                        Screen.Favourite.icon,
                        contentDescription = "Favourite",
                        modifier = Modifier.size(animatedIconSizeFav)
                    )
                },
                label = { Text(stringResource(id = R.string.favourite)) }
            )
            // ... etc for other items
        }
    }
}
