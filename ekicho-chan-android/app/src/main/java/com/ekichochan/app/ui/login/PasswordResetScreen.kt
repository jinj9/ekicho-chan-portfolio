package com.ekichochan.app.ui.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ekichochan.app.ui.theme.WebGold

@Composable
fun PasswordResetScreen(viewModel: PasswordResetViewModel, onBackToLogin: () -> Unit,
    modifier: Modifier = Modifier) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var email by remember { mutableStateOf("") }
    var token by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmation by remember { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var validationError by remember { mutableStateOf<String?>(null) }
    val busy = state == PasswordResetState.Sending || state == PasswordResetState.Resetting

    Column(
        modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("パスワード再設定", style = MaterialTheme.typography.headlineSmall)
        Card(border = BorderStroke(2.dp, WebGold)) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                when (state) {
                    PasswordResetState.Request, PasswordResetState.Sending -> {
                        Text("登録済みのメールアドレスを入力してください。再設定用トークンをメールで送信します。")
                        OutlinedTextField(email, { email = it }, label = { Text("メールアドレス") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true, modifier = Modifier.fillMaxWidth())
                        Button(onClick = {
                            validationError = if (Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$").matches(email.trim())) null
                                else "メールアドレスの形式を確認してください。"
                            if (validationError == null) viewModel.request(email)
                        }, enabled = !busy, modifier = Modifier.fillMaxWidth()) { Text("再設定メールを送信") }
                    }
                    PasswordResetState.Confirm, PasswordResetState.Resetting -> {
                        Text("メールに記載されたトークンと、新しいパスワードを入力してください。")
                        OutlinedTextField(token, { token = it }, label = { Text("確認トークン") },
                            singleLine = true, modifier = Modifier.fillMaxWidth())
                        ResetPasswordField("新しいパスワード", password, passwordVisible) { password = it }
                        ResetPasswordField("新しいパスワード（確認）", confirmation, passwordVisible) { confirmation = it }
                        TextButton(onClick = { passwordVisible = !passwordVisible }) {
                            Text(if (passwordVisible) "パスワードを隠す" else "パスワードを表示")
                        }
                        Text("半角英数字・記号のみで15～128文字", style = MaterialTheme.typography.bodySmall)
                        Button(onClick = {
                            validationError = when {
                                token.isBlank() -> "確認トークンを入力してください。"
                                !Regex("^[!-~]{15,128}$").matches(password) -> "新しいパスワードの形式を確認してください。"
                                password != confirmation -> "確認用パスワードが一致しません。"
                                else -> null
                            }
                            if (validationError == null) viewModel.confirm(token, password)
                        }, enabled = !busy, modifier = Modifier.fillMaxWidth()) { Text("パスワードを再設定") }
                    }
                    PasswordResetState.Complete -> {
                        Text("パスワードを再設定しました。新しいパスワードでログインしてください。")
                        Button(onClick = onBackToLogin, modifier = Modifier.fillMaxWidth()) { Text("ログインへ戻る") }
                    }
                    is PasswordResetState.Error -> Unit
                }
                if (validationError != null) Text(validationError!!, color = MaterialTheme.colorScheme.error)
                if (state is PasswordResetState.Error) Text((state as PasswordResetState.Error).message,
                    color = MaterialTheme.colorScheme.error)
                if (busy) LinearProgressIndicator(Modifier.fillMaxWidth())
                if (state != PasswordResetState.Complete) OutlinedButton(onClick = onBackToLogin,
                    enabled = !busy, modifier = Modifier.fillMaxWidth()) { Text("ログインへ戻る") }
            }
        }
    }
}

@Composable
private fun ResetPasswordField(label: String, value: String, visible: Boolean,
    onChange: (String) -> Unit) = OutlinedTextField(
    value, onChange, label = { Text(label) }, singleLine = true,
    visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
    modifier = Modifier.fillMaxWidth())
