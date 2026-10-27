package com.ekichochan.app.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ekichochan.app.data.repository.AuthApiException
import com.ekichochan.app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface RegistrationUiState {
    data object Idle : RegistrationUiState
    data object Registering : RegistrationUiState
    data object AwaitingVerification : RegistrationUiState
    data object Confirming : RegistrationUiState
    data object Verified : RegistrationUiState
    data class Error(val message: String) : RegistrationUiState
}

class RegistrationViewModel(private val repository: AuthRepository) : ViewModel() {
    private val mutableState = MutableStateFlow<RegistrationUiState>(RegistrationUiState.Idle)
    val state: StateFlow<RegistrationUiState> = mutableState.asStateFlow()

    fun register(email: String, displayName: String, password: String) {
        if (mutableState.value == RegistrationUiState.Registering) return
        viewModelScope.launch {
            mutableState.value = RegistrationUiState.Registering
            mutableState.value = runCatching { repository.register(email, displayName, password) }
                .fold(
                    onSuccess = { RegistrationUiState.AwaitingVerification },
                    onFailure = { RegistrationUiState.Error(registrationError(it)) },
                )
        }
    }

    fun confirmEmail(token: String) {
        if (mutableState.value == RegistrationUiState.Confirming) return
        viewModelScope.launch {
            mutableState.value = RegistrationUiState.Confirming
            mutableState.value = runCatching { repository.confirmEmail(token) }
                .fold(
                    onSuccess = { RegistrationUiState.Verified },
                    onFailure = { RegistrationUiState.Error(verificationError(it)) },
                )
        }
    }

    fun returnToRegistration() { mutableState.value = RegistrationUiState.Idle }
    fun consumeVerified() { mutableState.value = RegistrationUiState.Idle }

    private fun registrationError(error: Throwable) = when ((error as? AuthApiException)?.statusCode) {
        409 -> "このメールアドレスは既に登録されています。"
        400 -> "入力内容を確認してください。"
        else -> "ユーザー登録に失敗しました。Spring Bootとの接続を確認してください。"
    }

    private fun verificationError(error: Throwable) = when ((error as? AuthApiException)?.statusCode) {
        400 -> "確認トークンが正しくないか、有効期限が切れています。"
        else -> "メールアドレスを確認できませんでした。Spring Bootとの接続を確認してください。"
    }
}

