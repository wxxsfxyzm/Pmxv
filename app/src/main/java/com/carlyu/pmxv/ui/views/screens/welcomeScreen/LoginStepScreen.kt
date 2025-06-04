package com.carlyu.pmxv.ui.views.screens.welcomeScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.carlyu.pmxv.ui.views.uistate.SettingsWizardState
import kotlinx.coroutines.launch

@Composable
fun LoginStepScreen(
    modifier: Modifier = Modifier,
    apiLoginForm: SettingsWizardState.ProxmoxApiLoginForm,
    onServerAddressChange: (String) -> Unit,
    onNodeNameChange: (String) -> Unit,
    onApiTokenIdChange: (String) -> Unit,
    onApiTokenSecretChange: (String) -> Unit,
    onAccountNameChange: (String) -> Unit,
    isLoading: Boolean
) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    val serverAddressRequester = remember { BringIntoViewRequester() }
    val nodeNameRequester = remember { BringIntoViewRequester() }
    val apiTokenIdRequester = remember { BringIntoViewRequester() }
    val apiTokenSecretRequester = remember { BringIntoViewRequester() }
    val accountNameRequester = remember { BringIntoViewRequester() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "测试1",//stringResource(R.string.settings_wizard_login_title), // Define this string
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "测试2",//stringResource(R.string.settings_wizard_login_description), // Define this string
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Server Address
        OutlinedTextField(
            value = apiLoginForm.serverAddress,
            onValueChange = onServerAddressChange,
            label = { Text(text = "服务器地址"/*stringResource(R.string.setup_login_server_address*/) }, // Define this string
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .bringIntoViewRequester(serverAddressRequester)
                .onFocusEvent { if (it.isFocused) coroutineScope.launch { serverAddressRequester.bringIntoView() } },
            enabled = !isLoading,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Uri,
                imeAction = ImeAction.Next
            )
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Node Name
        OutlinedTextField(
            value = apiLoginForm.nodeName,
            onValueChange = onNodeNameChange,
            label = { Text(text = "node name"/*stringResource(R.string.setup_login_node_name)*/) }, // Define this string
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .bringIntoViewRequester(nodeNameRequester)
                .onFocusEvent { if (it.isFocused) coroutineScope.launch { nodeNameRequester.bringIntoView() } },
            enabled = !isLoading,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Account Name (User-friendly name for the connection)
        OutlinedTextField(
            value = apiLoginForm.accountName,
            onValueChange = onAccountNameChange,
            label = { Text(text = "Account Name"/*stringResource(R.string.setup_login_account_name)*/) }, // Define this string
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .bringIntoViewRequester(accountNameRequester)
                .onFocusEvent { if (it.isFocused) coroutineScope.launch { accountNameRequester.bringIntoView() } },
            enabled = !isLoading,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )
        Spacer(modifier = Modifier.height(12.dp))

        // API Token ID
        OutlinedTextField(
            value = apiLoginForm.apiTokenId,
            onValueChange = onApiTokenIdChange,
            label = { Text(text = "API Token Id"/*stringResource(R.string.setup_login_api_token_id)*/) }, // Define this string
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .bringIntoViewRequester(apiTokenIdRequester)
                .onFocusEvent { if (it.isFocused) coroutineScope.launch { apiTokenIdRequester.bringIntoView() } },
            enabled = !isLoading,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            placeholder = { Text("user@realm!tokenid") }
        )
        Spacer(modifier = Modifier.height(12.dp))

        // API Token Secret
        OutlinedTextField(
            value = apiLoginForm.apiTokenSecret,
            onValueChange = onApiTokenSecretChange,
            label = { Text(text = "API Token Secret"/*stringResource(R.string.setup_login_api_token_secret)*/) }, // Define this string
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .bringIntoViewRequester(apiTokenSecretRequester)
                .onFocusEvent { if (it.isFocused) coroutineScope.launch { apiTokenSecretRequester.bringIntoView() } },
            enabled = !isLoading
        )

        // This Spacer at the bottom is crucial.
        Spacer(modifier = Modifier.height(250.dp)) // Adjust height as needed
    }
}