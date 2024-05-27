package com.carlyu.chat.ui.views.screens

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.carlyu.chat.models.data.BottomSheetContent
import com.carlyu.chat.ui.components.AppIconsImpl
import com.carlyu.chat.ui.components.SettingsItemsImpl.SettingsItemSwitch
import com.carlyu.chat.ui.components.SettingsItemsImpl.SettingsNormalItems
import com.carlyu.chat.ui.components.SettingsItemsImpl.ThemeStyleSection
import com.carlyu.chat.ui.views.activities.AboutPageActivity
import com.carlyu.chat.viewmodels.SettingsViewModel

@Composable
fun PreferenceScreen(settingsViewModel: SettingsViewModel) {
    val context = LocalContext.current
    /*    BackHandler {
            (context as Activity).finish()
        }*/
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
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
        item {
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
                    checked = settingsViewModel.switchState1.value,
                    onCheckedChange = settingsViewModel::onSwitchChange1
                )
                SettingsItemSwitch(
                    icon = Icons.Default.Settings,
                    title = "Test Switch 2",
                    subtitle = "This is a test switch",
                    checked = settingsViewModel.switchState2.value,
                    onCheckedChange = settingsViewModel::onSwitchChange2
                )
                SettingsItemSwitch(
                    icon = AppIconsImpl.Settings,
                    title = "Test Switch 3",
                    subtitle = "This is a test switch",
                    checked = settingsViewModel.switchState3.value,
                    onCheckedChange = settingsViewModel::onSwitchChange3
                )
            }
        }
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "UI Display",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(16.dp)
                )
                SettingsItemSwitch(
                    icon = AppIconsImpl.Palette,
                    title = "Use Dynamic Colors",
                    subtitle = "Toggle On To Use Dynamic Colors",
                    checked = settingsViewModel.useDynamicColor.value,
                    onCheckedChange = settingsViewModel::toggleDynamicColor
                )
                ThemeStyleSection(
                    modifier = Modifier,
                    themeStyle = settingsViewModel.uiMode.value,
                    changeThemeStyle = settingsViewModel::changeThemeStyle,
                    onClick = {}
                )
            }
        }
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Other",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(16.dp)
                )
                SettingsNormalItems(
                    icon = AppIconsImpl.Infos,
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
                        settingsViewModel.bottomSheetState.value = true
                        settingsViewModel.bottomSheetContent.value = BottomSheetContent.CheckUpdates
                    }
                )
            }
        }
    }
}

