package com.carlyu.pmxv.ui.views.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.carlyu.pmxv.models.ExampleDataModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    private val _data = mutableStateOf<List<ExampleDataModel>>(emptyList())
    val data: State<List<ExampleDataModel>> = _data

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    init {
        // fetchData()
    }

    /*    private fun fetchData() {
            viewModelScope.launch {
                _isLoading.value = true
                try {
                    _data.value = ""
                    _error.value = null
                } catch (e: Exception) {
                    _error.value = e.message
                } finally {
                    _isLoading.value = false
                }
            }
        }*/
}