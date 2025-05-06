package com.carlyu.pmxv.ui.views.screens

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carlyu.pmxv.models.data.BottomSheetContent
import com.carlyu.pmxv.ui.components.AppIcons
import com.carlyu.pmxv.ui.components.SettingsItemSwitch
import com.carlyu.pmxv.ui.components.SettingsNormalItems
import com.carlyu.pmxv.ui.components.ThemeStyleSection
import com.carlyu.pmxv.ui.views.activities.AboutPageActivity
import com.carlyu.pmxv.ui.views.uistate.SettingsUiState
import com.carlyu.pmxv.ui.views.viewmodels.SettingsViewModel

@Composable
fun PreferenceScreen(settingsViewModel: SettingsViewModel) {
    val context = LocalContext.current
    val uiState by settingsViewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is SettingsUiState.Loading -> LoadingState()
        is SettingsUiState.Error -> ErrorState(message = state.message)
        is SettingsUiState.Success -> SuccessState(
            state = state,
            settingsViewModel = settingsViewModel,
            context = context
        )
    }
}

@Composable
private fun LoadingState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.padding(32.dp)
        )
    }
}

@Composable
private fun ErrorState(message: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Snackbar {
            Text(text = message)
        }
    }
}

@Composable
private fun SuccessState(
    state: SettingsUiState.Success,
    settingsViewModel: SettingsViewModel,
    context: android.content.Context
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            AccountSection(state, settingsViewModel)
        }
        item {
            SwitchItemsSection(state, settingsViewModel)
        }
        item {
            DisplaySection(state, settingsViewModel)
        }
        item {
            OtherSection(context, settingsViewModel)
        }
    }
}

@Composable
private fun AccountSection(
    state: SettingsUiState.Success,
    settingsViewModel: SettingsViewModel
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Account",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(16.dp)
        )
        SettingsNormalItems(
            icon = Icons.Default.AccountCircle,
            title = "账号注销",
            subtitle = "logout",
            onClick = { settingsViewModel.onLogoutClicked() }
        )
    }
}

@Composable
private fun SwitchItemsSection(
    state: SettingsUiState.Success,
    settingsViewModel: SettingsViewModel
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Switch Items",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(16.dp)
        )
        SettingsItemSwitch(
            icon = Icons.Default.Settings,
            title = "Test Switch 1",
            subtitle = "This is a test switch",
            checked = state.switchState1,
            onCheckedChange = settingsViewModel::onSwitchChange1
        )
        SettingsItemSwitch(
            icon = Icons.Default.Settings,
            title = "Test Switch 2",
            subtitle = "This is a test switch",
            checked = state.switchState2,
            onCheckedChange = settingsViewModel::onSwitchChange2
        )
        SettingsItemSwitch(
            icon = AppIcons.Settings,
            title = "Test Switch 3",
            subtitle = "This is a test switch",
            checked = state.switchState3,
            onCheckedChange = settingsViewModel::onSwitchChange3
        )
    }
}

@Composable
private fun DisplaySection(
    state: SettingsUiState.Success,
    settingsViewModel: SettingsViewModel
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "UI Display",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(16.dp)
        )
        SettingsItemSwitch(
            icon = AppIcons.Palette,
            title = "Use Dynamic Colors",
            subtitle = "Toggle On To Use Dynamic Colors",
            checked = state.useDynamicColor,
            onCheckedChange = settingsViewModel::toggleDynamicColor
        )
        ThemeStyleSection(
            modifier = Modifier,
            themeStyle = state.uiMode,
            changeThemeStyle = settingsViewModel::changeThemeStyle,
            onClick = {}
        )
    }
}

@Composable
private fun OtherSection(
    context: android.content.Context,
    settingsViewModel: SettingsViewModel
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Other",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(16.dp)
        )
        SettingsNormalItems(
            icon = AppIcons.Infos,
            title = "About",
            subtitle = "0.0.1",
            onClick = {
                val intent = Intent(context, AboutPageActivity::class.java)
                context.startActivity(intent)
            }
        )
        SettingsNormalItems(
            icon = Icons.Default.SystemUpdate,
            title = "Check For Updates",
            subtitle = "Get the latest version of the app",
            onClick = {
                settingsViewModel.showBottomSheet(
                    BottomSheetContent.CheckUpdates(
                        title = "版本检查",
                        description = "正在检测新版本..."
                    )
                )
            }
        )
    }
}

@Composable
private fun CustomBottomSheet(
    content: BottomSheetContent?,
    onDismiss: () -> Unit
) {
    // 实现具体的 BottomSheet UI
}