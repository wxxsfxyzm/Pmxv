package com.carlyu.chat.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.carlyu.chat.ui.components.SettingsItems.SettingsItemSwitch
import com.carlyu.chat.ui.components.SettingsItems.SettingsNormalItems
import com.carlyu.chat.viewmodels.SettingsViewModel

@Composable
fun PreferenceScreen(settingsViewModel: SettingsViewModel) {
    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Category Title",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(16.dp)
            )
            SettingsItemSwitch(
                icon = Icons.Default.Settings,
                title = "Test Switch 1",
                subtitle = "This is a test switch",
                checked = settingsViewModel.switchState.value,
                onCheckedChange = settingsViewModel::onSwitchChange
            )
            SettingsItemSwitch(
                icon = Icons.Default.Settings,
                title = "Test Switch 2",
                subtitle = "This is a test switch",
                checked = settingsViewModel.switchState.value,
                onCheckedChange = settingsViewModel::onSwitchChange
            )
            SettingsItemSwitch(
                icon = Icons.Default.Settings,
                title = "Test Switch 3",
                subtitle = "This is a test switch",
                checked = settingsViewModel.switchState.value,
                onCheckedChange = settingsViewModel::onSwitchChange
            )
        }
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Other",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(16.dp)
            )
            SettingsNormalItems(
                icon = Icons.Filled.Info,
                title = "About",
                subtitle = "0.0.1",
                onClick = {}
            )
            SettingsNormalItems(
                icon = Icons.Default.SystemUpdate,
                title = "Check For Updates",
                subtitle = "Get the latest version of the app",
                onClick = {

                }
            )
        }
    }
}

