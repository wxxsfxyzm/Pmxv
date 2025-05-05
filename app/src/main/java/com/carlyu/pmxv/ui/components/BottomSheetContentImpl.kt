package com.carlyu.pmxv.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carlyu.pmxv.viewmodels.SettingsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetCheckUpdateContent(settingsViewModel: SettingsViewModel, sheetState: SheetState) {
    val scope = rememberCoroutineScope()
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
        ) {
            Text(
                "Check Updates",
                modifier = Modifier.align(Alignment.Center),
                fontSize = 25.sp
            )
        }
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                "Under Development",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                "For Test Purposes Only",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Button(
                onClick = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            settingsViewModel.bottomSheetState.value = false
                        }
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Hide bottom sheet")
            }
        }
        Spacer(modifier = Modifier.padding(16.dp))
    }
}

