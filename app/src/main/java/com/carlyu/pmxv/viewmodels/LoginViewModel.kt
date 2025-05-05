package com.carlyu.pmxv.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carlyu.pmxv.network.LoginService
import com.carlyu.pmxv.network.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val loginService: LoginService) : ViewModel() {
    fun login(
        baseurl: String,
        username: String,
        password: String,
        onLoginSuccess: () -> Unit,
        onError: (message: String) -> Unit
    ) {
        viewModelScope.launch {
            when (val result = loginService.login("$baseurl/login", username, password)) {
                is Result.Success -> {
                    onLoginSuccess()
                }

                is Result.Failure -> {
                    onError(result.message)
                }
            }
        }
    }
}

