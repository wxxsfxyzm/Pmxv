package com.carlyu.chat.ui.components


import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext

object SettingsItems {
    @Composable
    fun SettingsNormalItems(
        icon: ImageVector,
        title: String,
        subtitle: String,
        onClick: () -> Unit?
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
                    onCheckedChange = onCheckedChange,
                    modifier = Modifier.clickable {}
                )
            }
        )
    }
}