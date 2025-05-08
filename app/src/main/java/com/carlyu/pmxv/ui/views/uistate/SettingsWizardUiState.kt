package com.carlyu.pmxv.ui.views.uistate

data class SettingsWizardUiState(
    val currentStep: Int = 0,
    val loginForm: LoginForm = LoginForm(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    data class LoginForm(
        val username: String = "",
        val password: String = ""
    )
}