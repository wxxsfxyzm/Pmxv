package com.carlyu.pmxv.ui.components.scaffold

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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

val bottomNavScreens = listOf(
    Screen.HomeScreen, Screen.Favourite, Screen.Settings
)

/**
 * 主界面布局
 * @param viewModel 设置 ViewModel
 */
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
                    Text(
                        when (currentScreen.value) {
                            Screen.HomeScreen -> stringResource(id = R.string.home_screen_title)
                            Screen.Favourite -> stringResource(id = R.string.favourite_screen_title)
                            Screen.Settings -> stringResource(id = R.string.settings_screen_title)
                        }
                    )
                },
            )
        },
        bottomBar = {
            BottomBar(
                currentScreen = currentScreen.value,
                onScreenSelected = { selectedScreen -> // 更新上一个屏幕索引
                    // 只有当屏幕确实改变时才更新currentScreen，以避免不必要的重组
                    if (currentScreen.value != selectedScreen) {
                        currentScreen.value = selectedScreen
                    }
                }
            )
        }
    ) { paddingValues ->
        AnimatedContent(
            targetState = currentScreen.value,
            modifier = Modifier.padding(paddingValues),
            transitionSpec = {
                // 使用 Screen.index 直接获取索引 (initialState 是切换前的屏幕, targetState 是切换后的屏幕)
                val currentIdx = targetState.index
                val previousIdx = initialState.index

                val enterTransition: EnterTransition
                val exitTransition: ExitTransition

                if (currentIdx > previousIdx) { // 向右滑动 (新屏幕在旧屏幕右边)
                    enterTransition = slideInHorizontally(
                        initialOffsetX = { fullWidth -> fullWidth },
                        animationSpec = tween(300)
                    ) + fadeIn(animationSpec = tween(300))
                    exitTransition = slideOutHorizontally(
                        targetOffsetX = { fullWidth -> -fullWidth },
                        animationSpec = tween(300)
                    ) + fadeOut(animationSpec = tween(300))
                } else if (currentIdx < previousIdx) { // 向左滑动 (新屏幕在旧屏幕左边)
                    enterTransition = slideInHorizontally(
                        initialOffsetX = { fullWidth -> -fullWidth },
                        animationSpec = tween(300)
                    ) + fadeIn(animationSpec = tween(300))
                    exitTransition = slideOutHorizontally(
                        targetOffsetX = { fullWidth -> fullWidth },
                        animationSpec = tween(300)
                    ) + fadeOut(animationSpec = tween(300))
                } else { // 相同屏幕或初始加载 (可以简化为淡入淡出)
                    enterTransition = fadeIn(animationSpec = tween(200))
                    exitTransition = fadeOut(animationSpec = tween(200))
                }
                enterTransition togetherWith exitTransition using SizeTransform(clip = false)
            },
            label = "ScreenTransition"
        ) { targetScreen ->
            when (targetScreen) {
                Screen.HomeScreen -> HomeScreen()
                Screen.Favourite -> {
                    // TODO("middle Screen")
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("收藏页面 (开发中)")
                    }
                }

                Screen.Settings -> PreferenceScreen(settingsViewModel = viewModel)
            }
        }
        // 统一处理BottomSheet状态
        // Bottom Sheet 逻辑应该在Scaffold层级，或者确保其z-index更高
        // 当前它在Scaffold的content lambda的最后，所以它会绘制在screens之上。
        if (uiState is SettingsUiState.Success) {
            val successState = uiState as SettingsUiState.Success
            if (successState.bottomSheetVisible) {
                ModalBottomSheet(
                    modifier = Modifier.fillMaxHeight(0.9f), // 避免完全填充，留出一点顶部空间
                    contentWindowInsets = { WindowInsets.systemBars }, // 使用 systemBars 通常是更安全的选择
                    onDismissRequest = { viewModel.dismissBottomSheet() },
                    sheetState = sheetState,
                    // containerColor = MaterialTheme.colorScheme.surfaceContainerLow, // 可以尝试不同的surface颜色
                    scrimColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f),
                ) {
                    // Column确保内容从顶部开始，并且可以在底部留出导航栏空间（如果需要）
                    Column(Modifier.navigationBarsPadding()) {
                        when (val content = successState.bottomSheetContent) {
                            is BottomSheetContent.CheckUpdates ->
                                BottomSheetCheckUpdateContent(
                                    settingsViewModel = viewModel,
                                    content = content
                                )

                            is BottomSheetContent.Confirmation -> {
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

/**
 * 底部导航栏
 * @param currentScreen 当前屏幕
 * @param onScreenSelected 选中屏幕的回调
 */
@Composable
private fun BottomBar(
    currentScreen: Screen,
    onScreenSelected: (Screen) -> Unit
) {
    NavigationBar {
        bottomNavScreens.forEach { screen ->
            val selected = currentScreen == screen
            val animatedOffsetY by animateDpAsState(
                targetValue = if (selected) (-4).dp else 0.dp, // 抬高 4 dp
                animationSpec = tween(durationMillis = 200),
                label = "NavItemOffset"
            )
            // 选中时稍微放大图标和文字，增加视觉反馈
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
                    .offset(y = animatedOffsetY) // 应用Y轴偏移
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)), // 给抬起部分一点圆角
                label = {
                    Text(
                        text = screen.title,
                        modifier = Modifier.graphicsLayer(
                            scaleX = animatedLabelScale,
                            scaleY = animatedLabelScale
                        )
                    )
                },
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.title,
                        modifier = Modifier.size(animatedIconSize)
                    )
                },
                selected = selected, // 选中状态
                onClick = { if (!selected) onScreenSelected(screen) }, // 只有在未选中时才触发，避免不必要的动画重置
                alwaysShowLabel = true, // 始终显示标签
            )
        }
    }
}

@Preview(showBackground = true, name = "BottomBar Selected Settings Preview")
@Composable
fun BottomBarPreview() { // Renamed for clarity, showing one state
    PmxvTheme(darkTheme = false, dynamicColor = true) {
        BottomBar(
            currentScreen = Screen.Settings, // 确保 Screen.Settings 存在且有 index
            onScreenSelected = {}
        )
    }
}
