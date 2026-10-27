package com.ekichochan.app.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ekichochan.app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface PasswordResetState { data object Request : PasswordResetState; data object Sending : PasswordResetState; data object Confirm : PasswordResetState; data object Resetting : PasswordResetState; data object Complete : PasswordResetState; data class Error(val message: String) : PasswordResetState }

class PasswordResetViewModel(private val repository: AuthRepository) : ViewModel() {
    private val mutableState = MutableStateFlow<PasswordResetState>(PasswordResetState.Request)
    val state = mutableState.asStateFlow()
    fun request(email: String) = viewModelScope.launch { mutableState.value = PasswordResetState.Sending; mutableState.value = runCatching { repository.requestPasswordReset(email) }.fold({ PasswordResetState.Confirm }, { PasswordResetState.Error("再設定メールを送信できませんでした。") }) }
    fun confirm(token: String, password: String) = viewModelScope.launch { mutableState.value = PasswordResetState.Resetting; mutableState.value = runCatching { repository.confirmPasswordReset(token, password) }.fold({ PasswordResetState.Complete }, { PasswordResetState.Error("再設定トークンが正しくないか、有効期限が切れています。") }) }
}

