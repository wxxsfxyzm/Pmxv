package com.carlyu.pmxv.ui.views.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.carlyu.pmxv.models.ExampleDataModel
import com.carlyu.pmxv.ui.views.viewmodels.MainViewModel


// DEBUG
// This screen is currently used to show the API response data for testing purposes
@Composable
fun FavouriteScreen(viewModel: MainViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val data by viewModel.data
    val isLoading by viewModel.isLoading
    val error by viewModel.error

    Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
/*        Text(
            text = "Favourite Screen",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
        )*/
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Error: $error")
                }
            }

            else -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(data) { item ->
                        DataItem(item)
                    }
                }
            }
        }
    }
}

@Composable
fun DataItem(dataModel: ExampleDataModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = dataModel.name, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = dataModel.description, style = MaterialTheme.typography.bodyMedium)
    }
}

/*fun getCameraId(context: Context) {
    val cameraManager = getSystemService(context, CameraManager::class.java)
    try {
        for (cameraId in cameraManager.cameraIdList) {
            val characteristics = cameraManager.getCameraCharacteristics(cameraId)
            val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
            if (facing == CameraCharacteristics.LENS_FACING_FRONT) {
                Log.d("Camera", "Front Camera: $cameraId")
            } else if (facing == CameraCharacteristics.LENS_FACING_BACK) {
                Log.d("Camera", "Back Camera: $cameraId")
            }
        }
    } catch (e: CameraAccessException) {
        e.printStackTrace()
    }*/
