package com.ekichochan.app.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ekichochan.app.data.repository.AuthApiException
import com.ekichochan.app.data.repository.AuthRepository
import com.ekichochan.app.data.repository.AuthSessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AccountOperationState {
    data object Idle : AccountOperationState
    data object Loading : AccountOperationState
    data class Saved(val message: String) : AccountOperationState
    data class Error(val message: String) : AccountOperationState
}

class AccountViewModel(private val repository: AuthRepository, private val sessions: AuthSessionManager) : ViewModel() {
    private val mutableState = MutableStateFlow<AccountOperationState>(AccountOperationState.Idle)
    val state = mutableState.asStateFlow()

    fun changeDisplayName(displayName: String) = runOperation {
        val updated = repository.changeDisplayName(displayName)
        sessions.updateUser(updated)
        "ユーザー名を変更しました。"
    }

    fun changePassword(currentPassword: String, newPassword: String) = runOperation {
        repository.changePassword(currentPassword, newPassword)
        "パスワードを変更しました。新しいパスワードでログインしてください。"
    }

    fun deleteAccount(currentPassword: String) = runOperation("アカウントを削除できませんでした。") {
        repository.deleteAccount(currentPassword)
        "アカウントを削除しました。"
    }

    fun clearMessage() { mutableState.value = AccountOperationState.Idle }

    private fun runOperation(
        fallbackMessage: String = "アカウント情報を変更できませんでした。",
        block: suspend () -> String,
    ) {
        if (mutableState.value == AccountOperationState.Loading) return
        viewModelScope.launch {
            mutableState.value = AccountOperationState.Loading
            mutableState.value = runCatching { block() }.fold(
                onSuccess = AccountOperationState::Saved,
                onFailure = { error -> AccountOperationState.Error(accountError(error, fallbackMessage)) })
        }
    }

    private fun accountError(error: Throwable, fallbackMessage: String) = when ((error as? AuthApiException)?.statusCode) {
        400 -> "入力内容を確認してください。"
        401 -> "現在のパスワードが正しくないか、セッションの有効期限が切れています。"
        else -> fallbackMessage
    }
}
