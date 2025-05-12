package com.carlyu.pmxv.ui.components.widgets


import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ModeNight
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.carlyu.pmxv.R
import com.carlyu.pmxv.models.data.view.ThemeStyleType
import com.carlyu.pmxv.ui.components.icons.AppIcons
import com.carlyu.pmxv.ui.theme.PmxvTheme

@Composable
fun SettingsNormalItems(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val vibrator = context.getSystemService(Vibrator::class.java)
    ListItem(
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
            )
        },
        headlineContent = { Text(text = title) },
        supportingContent = { Text(text = subtitle) },
        modifier = Modifier.clickable {
            onClick()
            vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
        }
    )
}

@Composable
fun SettingsNormalItemsWithDialogRadioButton(
    icon: ImageVector,
    title: String,
    subtitle: String,
    dialogTitle: String,
    dialogRadioButtonOptionsList: List<String>,
    selectedOption: MutableState<Int>,
    onOptionSelected: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val vibrator = context.getSystemService(Vibrator::class.java)
    val showDialog = remember { mutableStateOf(false) }

    if (showDialog.value) {
        Dialog(
            onDismissRequest = { showDialog.value = false }
        ) {
            Card {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = dialogTitle,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(12.dp)
                    )
                    dialogRadioButtonOptionsList.forEachIndexed { index, option ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(0.dp)
                                .selectable(
                                    selected = (index == selectedOption.value),
                                    onClick = {
                                        selectedOption.value = index
                                        showDialog.value = false // 关闭对话框
                                        onOptionSelected(option)
                                    }
                                ),
                            verticalAlignment = Alignment.CenterVertically // 添加这一行
                        ) {
                            RadioButton(
                                selected = (index == selectedOption.value),
                                onClick = {
                                    selectedOption.value = index
                                    showDialog.value = false
                                    onOptionSelected(option)
                                }
                            )
                            Text(
                                text = option,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }

                    }
                    Spacer(modifier = Modifier.padding(5.dp))
                }
            }
        }
    }

    ListItem(
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
            )
        },
        headlineContent = { Text(text = title) },
        supportingContent = { Text(text = subtitle) },
        modifier = Modifier.clickable {
            vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            showDialog.value = true
        }
    )
}


@Composable
fun SettingsItemSwitch(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val vibrator = context.getSystemService(Vibrator::class.java)
    ListItem(
        modifier = Modifier
            .clickable {
                onCheckedChange(!checked)
                vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            },
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
            )
        },
        headlineContent = { Text(text = title) },
        supportingContent = { Text(text = subtitle) },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = { isChecked ->
                    onCheckedChange(isChecked)
                    vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
                },
                modifier = Modifier.clickable { }
            )
        }
    )
}

@Composable
fun ThemeStyleSection(
    modifier: Modifier = Modifier,
    themeStyle: ThemeStyleType,
    changeThemeStyle: (ThemeStyleType) -> Unit,
    onClick: () -> Unit?
) {
    val context = LocalContext.current
    val vibrator = context.getSystemService(Vibrator::class.java)
    ListItem(
        leadingContent = {
            Icon(
                imageVector = Icons.Default.ModeNight,
                contentDescription = null,
                modifier = Modifier
            )
        },
        headlineContent = { Text(stringResource(R.string.dark_mode)) },
        supportingContent = {
            Column(modifier = modifier) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(space = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    InputChip(
                        selected = themeStyle == ThemeStyleType.LIGHT,
                        onClick = {
                            vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
                            if (themeStyle != ThemeStyleType.LIGHT)
                                changeThemeStyle(ThemeStyleType.LIGHT)
                        },
                        label = { Text(text = stringResource(R.string.dark_mode_light)) },
                        leadingIcon = {
                            Icon(
                                modifier = Modifier.size(size = AssistChipDefaults.IconSize),
                                imageVector = AppIcons.LightMode,
                                contentDescription = null
                            )
                        }
                    )
                    InputChip(
                        selected = themeStyle == ThemeStyleType.DARK,
                        onClick = {
                            vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
                            if (themeStyle != ThemeStyleType.DARK)
                                changeThemeStyle(ThemeStyleType.DARK)
                        },
                        label = { Text(stringResource(R.string.dark_mode_dark)) },
                        leadingIcon = {
                            Icon(
                                modifier = Modifier.size(size = AssistChipDefaults.IconSize),
                                imageVector = AppIcons.DarkMode,
                                contentDescription = null
                            )
                        }
                    )
                }
                InputChip(
                    selected = themeStyle == ThemeStyleType.FOLLOW_SYSTEM,
                    onClick = {
                        vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
                        if (themeStyle != ThemeStyleType.FOLLOW_SYSTEM)
                            changeThemeStyle(ThemeStyleType.FOLLOW_SYSTEM)
                    },
                    label = { Text(text = stringResource(R.string.dark_mode_system)) },
                    leadingIcon = {
                        Icon(
                            modifier = Modifier.size(size = AssistChipDefaults.IconSize),
                            imageVector = AppIcons.Android,
                            contentDescription = null
                        )
                    }
                )
            }
        },

        modifier = Modifier.clickable {
            onClick()
            vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
        })

}

@Preview(showBackground = true, name = "SettingsNormalItems Preview")
@Composable
fun SettingsNormalItemsPreview() {
    SettingsNormalItems(
        icon = AppIcons.LightMode,
        title = "Light Mode",
        subtitle = "Light Mode"
    ) {}
}

@Preview(showBackground = true, name = "SettingsNormalItemsWithDialogRadioButton Preview")
@Composable
fun SettingsNormalItemsWithDialogRadioButtonPreview() {
    val selectedOption = remember { mutableIntStateOf(0) }
    PmxvTheme(darkTheme = false, dynamicColor = true) {
        SettingsNormalItemsWithDialogRadioButton(
            icon = AppIcons.LightMode,
            title = "Light Mode",
            subtitle = "Light Mode",
            dialogTitle = "Select Theme Style",
            dialogRadioButtonOptionsList = listOf("Light", "Dark", "Follow System"),
            selectedOption = selectedOption
        ) {}
    }
}

@Preview(showBackground = true, name = "SettingsItemSwitch Preview")
@Composable
fun SettingsItemSwitchPreview() {
    PmxvTheme(
        darkTheme = false,
        dynamicColor = true
    ) {
        SettingsItemSwitch(
            icon = AppIcons.LightMode,
            title = "Light Mode",
            subtitle = "Light Mode",
            checked = true
        ) {}
    }
}

@Preview(showBackground = true, name = "ThemeStyleSection Preview")
@Composable
fun ThemeStyleSectionPreview() {
    PmxvTheme(
        darkTheme = false,
        dynamicColor = true
    ) {
        ThemeStyleSection(
            themeStyle = ThemeStyleType.LIGHT,
            changeThemeStyle = {},
            onClick = {}
        )
    }
}



