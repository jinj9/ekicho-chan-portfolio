package com.ekichochan.app.data.repository

import com.ekichochan.app.data.model.AuthUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthSessionState {
    data object Checking : AuthSessionState
    data object SigningIn : AuthSessionState
    data object SigningOut : AuthSessionState
    data object SignedOut : AuthSessionState
    data object SessionExpired : AuthSessionState
    data class SignedIn(val user: AuthUser) : AuthSessionState
    data class Unavailable(val cause: Throwable) : AuthSessionState
}

class AuthSessionManager(private val repository: AuthRepository, private val scope: CoroutineScope,
    sessionStore: SessionStore) {
    private val mutableState = MutableStateFlow<AuthSessionState>(AuthSessionState.Checking)
    val state: StateFlow<AuthSessionState> = mutableState.asStateFlow()

    init {
        sessionStore.addTokenClearedListener {
            scope.launch {
                if (mutableState.value is AuthSessionState.SignedIn) mutableState.value = AuthSessionState.SignedOut
            }
        }
        sessionStore.addSessionExpiredListener {
            scope.launch {
                if (mutableState.value != AuthSessionState.SigningIn &&
                    mutableState.value != AuthSessionState.SigningOut) {
                    mutableState.value = AuthSessionState.SessionExpired
                }
            }
        }
    }

    fun restore() {
        scope.launch {
            mutableState.value = runCatching { repository.restoreSession() }
                .fold(
                    onSuccess = { user -> user?.let(AuthSessionState::SignedIn) ?: AuthSessionState.SignedOut },
                    onFailure = { error -> if (error is SessionExpiredException)
                        AuthSessionState.SessionExpired else AuthSessionState.Unavailable(error) },
                )
        }
    }

    fun login(email: String, password: String) {
        if (mutableState.value == AuthSessionState.SigningIn) return
        scope.launch {
            mutableState.value = AuthSessionState.SigningIn
            mutableState.value = runCatching { repository.login(email.trim(), password) }
                .fold(
                    onSuccess = AuthSessionState::SignedIn,
                    onFailure = AuthSessionState::Unavailable,
                )
        }
    }

    fun logout() {
        if (mutableState.value == AuthSessionState.SigningOut) return
        scope.launch {
            mutableState.value = AuthSessionState.SigningOut
            mutableState.value = runCatching { repository.logout() }
                .fold(
                    onSuccess = { AuthSessionState.SignedOut },
                    onFailure = AuthSessionState::Unavailable,
                )
        }
    }

    fun clearError() {
        if (mutableState.value is AuthSessionState.Unavailable ||
            mutableState.value == AuthSessionState.SessionExpired) {
            mutableState.value = AuthSessionState.SignedOut
        }
    }

    fun updateUser(user: AuthUser) {
        mutableState.value = AuthSessionState.SignedIn(user)
    }
}
