package com.ekichochan.app.ui.login

import com.ekichochan.app.R

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ekichochan.app.ui.theme.WebGold
import com.ekichochan.app.data.repository.AuthApiException
import com.ekichochan.app.data.repository.AuthSessionManager
import com.ekichochan.app.data.repository.AuthSessionState

private enum class AuthForm { LOGIN, REGISTER, VERIFY }

@Composable
fun LoginScreen(registrationViewModel: RegistrationViewModel, authSessionManager: AuthSessionManager,
    onLoginSuccess: () -> Unit, onForgotPassword: () -> Unit, modifier: Modifier = Modifier) {
    var form by rememberSaveable { mutableStateOf(AuthForm.LOGIN) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmation by remember { mutableStateOf("") }
    var token by remember { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmationVisible by rememberSaveable { mutableStateOf(false) }
    var validationIssue by remember { mutableStateOf<RegistrationIssue?>(null) }
    var loginError by remember { mutableStateOf<String?>(null) }
    var verificationComplete by remember { mutableStateOf(false) }
    val registrationState by registrationViewModel.state.collectAsStateWithLifecycle()
    val authState by authSessionManager.state.collectAsStateWithLifecycle()
    val busy = registrationState is RegistrationUiState.Registering || registrationState is RegistrationUiState.Confirming || authState == AuthSessionState.SigningIn
    val sessionExpired = authState == AuthSessionState.SessionExpired

    LaunchedEffect(registrationState) {
        when (registrationState) {
            RegistrationUiState.AwaitingVerification -> {
                password = ""; confirmation = ""; form = AuthForm.VERIFY
            }
            RegistrationUiState.Verified -> {
                token = ""; form = AuthForm.LOGIN; verificationComplete = true
                registrationViewModel.consumeVerified()
            }
            else -> Unit
        }
    }
    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthSessionState.SignedIn -> if (form == AuthForm.LOGIN) { password = ""; onLoginSuccess() }
            is AuthSessionState.Unavailable -> if (form == AuthForm.LOGIN) {
                loginError = if ((state.cause as? AuthApiException)?.statusCode == 401)
                    "メールアドレスまたはパスワードを確認してください。"
                else "ログインできませんでした。Spring Bootとの接続を確認してください。"
            }
            else -> Unit
        }
    }

    Column(
        modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(when (form) {
            AuthForm.LOGIN -> R.string.web_login
            AuthForm.REGISTER -> R.string.web_register
            AuthForm.VERIFY -> R.string.auth_verify_title
        }), style = MaterialTheme.typography.headlineSmall)
        Card(border = BorderStroke(2.dp, WebGold)) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                when (form) {
                    AuthForm.LOGIN -> LoginForm(email, password, passwordVisible,
                        { email = it }, { password = it }, { passwordVisible = !passwordVisible },
                        {
                            loginError = when {
                                !Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$").matches(email.trim()) -> "メールアドレスの形式を確認してください。"
                                password.isBlank() -> "パスワードを入力してください。"
                                password.length > 128 -> "パスワードは128文字以下で入力してください。"
                                else -> null
                            }
                            if (loginError == null) { authSessionManager.clearError(); authSessionManager.login(email, password) }
                        }, { form = AuthForm.REGISTER; password = ""; loginError = null; validationIssue = null },
                        onForgotPassword, busy)
                    AuthForm.REGISTER -> RegistrationForm(
                        name, email, password, confirmation, passwordVisible, confirmationVisible,
                        validationIssue, busy, { name = it }, { email = it }, { password = it },
                        { confirmation = it }, { passwordVisible = !passwordVisible },
                        { confirmationVisible = !confirmationVisible }, {
                            validationIssue = validateRegistration(name, email, password, confirmation)
                            if (validationIssue == null) registrationViewModel.register(email, name, password)
                        }, { form = AuthForm.LOGIN; password = ""; confirmation = ""; validationIssue = null })
                    AuthForm.VERIFY -> VerificationForm(token, busy, { token = it }, {
                        validationIssue = if (token.isBlank()) RegistrationIssue.TOKEN_REQUIRED else null
                        if (validationIssue == null) registrationViewModel.confirmEmail(token)
                    }, { registrationViewModel.returnToRegistration(); form = AuthForm.REGISTER })
                }
                val message = when (validationIssue) {
                    RegistrationIssue.NAME_INVALID -> stringResource(R.string.auth_name_invalid)
                    RegistrationIssue.EMAIL_INVALID -> stringResource(R.string.web_email_invalid)
                    RegistrationIssue.PASSWORD_INVALID -> stringResource(R.string.auth_password_invalid)
                    RegistrationIssue.PASSWORD_MISMATCH -> stringResource(R.string.web_password_mismatch)
                    RegistrationIssue.TOKEN_REQUIRED -> stringResource(R.string.auth_token_required)
                    null -> (registrationState as? RegistrationUiState.Error)?.message
                }
                if (message != null) Text(message, color = MaterialTheme.colorScheme.error)
                if (form == AuthForm.LOGIN && loginError != null) Text(loginError!!, color = MaterialTheme.colorScheme.error)
                if (form == AuthForm.LOGIN && sessionExpired) Text(
                    "セッションの有効期限が切れました。再度ログインしてください。",
                    color = MaterialTheme.colorScheme.error
                )
                if (busy) LinearProgressIndicator(Modifier.fillMaxWidth())
            }
        }
        if (form == AuthForm.VERIFY) Text(stringResource(R.string.auth_mailpit_help), style = MaterialTheme.typography.bodySmall)
    }

    if (verificationComplete) AlertDialog(onDismissRequest = { verificationComplete = false },
        title = { Text(stringResource(R.string.auth_verify_complete_title)) }, text = { Text(stringResource(R.string.auth_verify_complete)) },
        confirmButton = { TextButton(onClick = { verificationComplete = false }) { Text(stringResource(R.string.web_close)) } })
}

@Composable
private fun LoginForm(email: String, password: String, visible: Boolean, onEmail: (String) -> Unit,
    onPassword: (String) -> Unit, onToggle: () -> Unit, onSubmit: () -> Unit, onRegister: () -> Unit,
    onForgotPassword: () -> Unit, busy: Boolean) {
    EmailField(email, onEmail)
    PasswordField(stringResource(R.string.auth_password), password, visible, onPassword, onToggle)
    Button(onSubmit, enabled = !busy, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.web_login)) }
    TextButton(onClick = onForgotPassword, enabled = !busy, modifier = Modifier.fillMaxWidth()) {
        Text("パスワードを忘れた方")
    }
    OutlinedButton(onRegister, enabled = !busy, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.web_to_register)) }
}

@Composable
private fun RegistrationForm(name: String, email: String, password: String, confirmation: String,
    visible: Boolean, confirmationVisible: Boolean, issue: RegistrationIssue?, busy: Boolean,
    onName: (String) -> Unit, onEmail: (String) -> Unit, onPassword: (String) -> Unit,
    onConfirmation: (String) -> Unit, onToggle: () -> Unit, onToggleConfirmation: () -> Unit,
    onSubmit: () -> Unit, onLogin: () -> Unit) {
    OutlinedTextField(name, onName, label = { Text(stringResource(R.string.web_name)) }, singleLine = true,
        isError = issue == RegistrationIssue.NAME_INVALID, modifier = Modifier.fillMaxWidth())
    EmailField(email, onEmail, issue == RegistrationIssue.EMAIL_INVALID)
    PasswordField(stringResource(R.string.auth_password), password, visible, onPassword, onToggle, issue == RegistrationIssue.PASSWORD_INVALID)
    Text(stringResource(R.string.auth_password_policy), style = MaterialTheme.typography.bodySmall)
    PasswordField(stringResource(R.string.web_password_confirm), confirmation, confirmationVisible, onConfirmation, onToggleConfirmation, issue == RegistrationIssue.PASSWORD_MISMATCH)
    Button(onSubmit, enabled = !busy, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.auth_register_submit)) }
    OutlinedButton(onLogin, enabled = !busy, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.web_to_login)) }
}

@Composable
private fun VerificationForm(token: String, busy: Boolean, onToken: (String) -> Unit, onSubmit: () -> Unit, onBack: () -> Unit) {
    Text(stringResource(R.string.auth_verify_instructions))
    OutlinedTextField(token, onToken, label = { Text(stringResource(R.string.auth_token)) }, singleLine = true, modifier = Modifier.fillMaxWidth())
    Button(onSubmit, enabled = !busy, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.auth_verify_submit)) }
    OutlinedButton(onBack, enabled = !busy, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.auth_back_to_registration)) }
}

@Composable
private fun EmailField(value: String, onChange: (String) -> Unit, error: Boolean = false) = OutlinedTextField(
    value, onChange, label = { Text(stringResource(R.string.web_email)) }, isError = error,
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), singleLine = true, modifier = Modifier.fillMaxWidth())

@Composable
private fun PasswordField(label: String, value: String, visible: Boolean, onChange: (String) -> Unit,
    onToggle: () -> Unit, error: Boolean = false) = OutlinedTextField(
    value, onChange, label = { Text(label) }, isError = error,
    visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), singleLine = true,
    trailingIcon = { TextButton(onClick = onToggle) { Text(stringResource(if (visible) R.string.auth_hide_password else R.string.auth_show_password)) } },
    modifier = Modifier.fillMaxWidth())
