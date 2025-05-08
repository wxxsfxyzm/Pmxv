package com.carlyu.pmxv.ui.components.bottomsheet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carlyu.pmxv.models.data.BottomSheetContent
import com.carlyu.pmxv.ui.views.viewmodels.SettingsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetCheckUpdateContent(
    settingsViewModel: SettingsViewModel,
    content: BottomSheetContent
) {

    when (content) {
        is BottomSheetContent.CheckUpdates -> {
            RealCheckUpdateContent(settingsViewModel, content)
        }

        is BottomSheetContent.Confirmation -> {
            ConfirmationContent(settingsViewModel, content)
        }
        // 处理其他类型...
    }
}


@Composable
private fun RealCheckUpdateContent(
    settingsViewModel: SettingsViewModel,
    content: BottomSheetContent.CheckUpdates
) {
    val scope = rememberCoroutineScope()

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
        ) {
            Text(
                text = content.title,
                modifier = Modifier.align(Alignment.Center),
                fontSize = 25.sp
            )
        }
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = content.description,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = content.description,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = content.description,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = content.description,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = content.description,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = content.description,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = content.description,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = content.description,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = content.description,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = content.description,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = content.description,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = content.description,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = content.description,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = content.description,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = content.description,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = content.description,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = content.description,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = content.description,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = content.description,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = content.description,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = content.description,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = content.description,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = content.description,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = content.description,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.padding(16.dp))
            Button(
                onClick = {
                    scope.launch {
                        settingsViewModel.dismissBottomSheet()
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Close"
                )
            }
        }
        Spacer(modifier = Modifier.padding(16.dp))
    }
}

@Composable
private fun ConfirmationContent(
    settingsViewModel: SettingsViewModel,
    content: BottomSheetContent.Confirmation
) {
    // 实现确认对话框的内容...
}