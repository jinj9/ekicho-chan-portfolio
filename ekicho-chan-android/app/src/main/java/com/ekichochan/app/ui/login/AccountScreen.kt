package com.ekichochan.app.ui.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ekichochan.app.data.model.AuthUser
import com.ekichochan.app.ui.theme.WebGold

@Composable
fun AccountScreen(user: AuthUser, viewModel: AccountViewModel, modifier: Modifier = Modifier) {
    var displayName by remember(user.displayName) { mutableStateOf(user.displayName) }
    var passwordDialog by remember { mutableStateOf(false) }
    var deleteDialog by remember { mutableStateOf(false) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val loading = state == AccountOperationState.Loading

    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("マイページ", style = MaterialTheme.typography.headlineSmall)
        Card(border = BorderStroke(2.dp, WebGold)) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("メールアドレス", style = MaterialTheme.typography.labelMedium)
                Text(user.email)
                OutlinedTextField(displayName, { displayName = it }, label = { Text("ユーザー名") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Button(onClick = {
                    val length = displayName.trim().codePointCount(0, displayName.trim().length)
                    if (length in 1..30 && displayName.none(Char::isISOControl)) viewModel.changeDisplayName(displayName)
                }, enabled = !loading, modifier = Modifier.fillMaxWidth()) { Text("ユーザー名を変更") }
                OutlinedButton(onClick = { passwordDialog = true }, enabled = !loading, modifier = Modifier.fillMaxWidth()) { Text("パスワードを変更") }
                HorizontalDivider()
                Text(
                    "アカウントを削除すると、登録情報と取得した駅の記録は元に戻せません。",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
                OutlinedButton(
                    onClick = { deleteDialog = true },
                    enabled = !loading,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                ) { Text("アカウントを削除") }
                if (loading) LinearProgressIndicator(Modifier.fillMaxWidth())
                when (val value = state) {
                    is AccountOperationState.Saved -> Text(value.message, color = MaterialTheme.colorScheme.primary)
                    is AccountOperationState.Error -> Text(value.message, color = MaterialTheme.colorScheme.error)
                    else -> Unit
                }
            }
        }
    }
    if (passwordDialog) PasswordChangeDialog(loading, onDismiss = { passwordDialog = false }, onSubmit = { current, new ->
        passwordDialog = false
        viewModel.changePassword(current, new)
    })
    if (deleteDialog) AccountDeletionDialog(loading, onDismiss = { deleteDialog = false }, onSubmit = { current ->
        deleteDialog = false
        viewModel.deleteAccount(current)
    })
}

@Composable
private fun AccountDeletionDialog(
    loading: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit,
) {
    var currentPassword by remember { mutableStateOf("") }
    var confirmation by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("アカウントの削除") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "この操作は取り消せません。登録情報と取得した駅の記録がすべて削除されます。",
                    color = MaterialTheme.colorScheme.error,
                )
                AccountPasswordField("現在のパスワード", currentPassword, passwordVisible) {
                    currentPassword = it
                }
                TextButton(onClick = { passwordVisible = !passwordVisible }) {
                    Text(if (passwordVisible) "パスワードを隠す" else "パスワードを表示")
                }
                OutlinedTextField(
                    value = confirmation,
                    // Do not limit the composing text length. Japanese IMEs need to hold
                    // "さくじょ" before it is converted and committed as "削除".
                    onValueChange = { confirmation = it },
                    label = { Text("確認のため「削除」と入力") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                if (error != null) Text(error!!, color = MaterialTheme.colorScheme.error)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    error = when {
                        currentPassword.isBlank() -> "現在のパスワードを入力してください。"
                        confirmation != "削除" -> "確認欄に「削除」と入力してください。"
                        else -> null
                    }
                    if (error == null) onSubmit(currentPassword)
                },
                enabled = !loading && currentPassword.isNotBlank() && confirmation == "削除",
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            ) { Text("削除する") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("キャンセル") } },
    )
}

@Composable
private fun PasswordChangeDialog(loading: Boolean, onDismiss: () -> Unit, onSubmit: (String, String) -> Unit) {
    var current by remember { mutableStateOf("") }; var new by remember { mutableStateOf("") }; var confirmation by remember { mutableStateOf("") }
    var visible by remember { mutableStateOf(false) }; var error by remember { mutableStateOf<String?>(null) }
    AlertDialog(onDismissRequest = onDismiss, title = { Text("パスワード変更") }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            AccountPasswordField("現在のパスワード", current, visible) { current = it }
            AccountPasswordField("新しいパスワード", new, visible) { new = it }
            AccountPasswordField("新しいパスワード（確認）", confirmation, visible) { confirmation = it }
            TextButton(onClick = { visible = !visible }) { Text(if (visible) "パスワードを隠す" else "パスワードを表示") }
            Text("半角英数字・記号のみで15～128文字", style = MaterialTheme.typography.bodySmall)
            if (error != null) Text(error!!, color = MaterialTheme.colorScheme.error)
        }
    }, confirmButton = { Button(onClick = {
        error = when { current.isBlank() -> "現在のパスワードを入力してください。"; !Regex("^[!-~]{15,128}$").matches(new) -> "新しいパスワードの形式を確認してください。"; new != confirmation -> "確認用パスワードが一致しません。"; else -> null }
        if (error == null) onSubmit(current, new)
    }, enabled = !loading) { Text("変更する") } }, dismissButton = { TextButton(onClick = onDismiss) { Text("キャンセル") } })
}

@Composable
private fun AccountPasswordField(label: String, value: String, visible: Boolean, onChange: (String) -> Unit) = OutlinedTextField(
    value, onChange, label = { Text(label) }, singleLine = true,
    visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), modifier = Modifier.fillMaxWidth())
