package com.ekichochan.app.data.repository

import com.ekichochan.app.data.model.AuthUser
import com.ekichochan.app.data.model.LoginRequest
import com.ekichochan.app.data.model.RegistrationRequest
import com.ekichochan.app.data.model.EmailVerificationRequest
import com.ekichochan.app.data.remote.AuthApi
import com.ekichochan.app.data.remote.AuthTokenInterceptor

class AuthRepository(private val api: AuthApi, private val sessionStore: SessionStore) {
    suspend fun register(email: String, displayName: String, password: String) {
        val response = api.register(RegistrationRequest(email.trim(), displayName.trim(), password))
        if (!response.isSuccessful) throw AuthApiException(response.code())
    }

    suspend fun confirmEmail(token: String) {
        val response = api.confirmEmail(EmailVerificationRequest(token.trim()))
        if (!response.isSuccessful) throw AuthApiException(response.code())
    }

    suspend fun login(email: String, password: String): AuthUser {
        val response = api.login(LoginRequest(email, password))
        if (!response.isSuccessful) throw AuthApiException(response.code())
        val user = response.body() ?: throw AuthApiException(response.code(), "Login response body is empty")
        val token = response.headers()[AuthTokenInterceptor.AUTH_HEADER]
            ?.takeIf(String::isNotBlank) ?: throw AuthApiException(response.code(), "Authentication token is missing")
        sessionStore.saveToken(token)
        return user
    }

    suspend fun restoreSession(): AuthUser? {
        if (sessionStore.readToken().isNullOrBlank()) return null
        val response = api.currentUser()
        if (response.code() == 401) {
            // 通常はInterceptorで消去済み。単独利用時にも期限切れを確実に通知する。
            if (!sessionStore.readToken().isNullOrBlank()) sessionStore.expireToken()
            throw SessionExpiredException()
        }
        if (!response.isSuccessful) throw AuthApiException(response.code())
        return response.body() ?: throw AuthApiException(response.code(), "Current user response body is empty")
    }

    suspend fun logout() {
        try {
            val response = api.logout()
            if (!response.isSuccessful && response.code() != 401) throw AuthApiException(response.code())
        } finally {
            sessionStore.clearToken()
        }
    }

    suspend fun changeDisplayName(displayName: String): AuthUser {
        val response = api.changeDisplayName(com.ekichochan.app.data.model.DisplayNameUpdateRequest(displayName.trim()))
        if (!response.isSuccessful) throw AuthApiException(response.code())
        return response.body() ?: throw AuthApiException(response.code(), "Account response body is empty")
    }

    suspend fun changePassword(currentPassword: String, newPassword: String) {
        val response = api.changePassword(com.ekichochan.app.data.model.PasswordChangeRequest(currentPassword, newPassword))
        if (!response.isSuccessful) throw AuthApiException(response.code())
        sessionStore.clearToken()
    }

    suspend fun deleteAccount(currentPassword: String) {
        val response = api.deleteAccount(
            com.ekichochan.app.data.model.AccountDeletionRequest(currentPassword))
        if (!response.isSuccessful) throw AuthApiException(response.code())
        sessionStore.clearToken()
    }

    suspend fun requestPasswordReset(email: String) {
        val response = api.requestPasswordReset(com.ekichochan.app.data.model.PasswordResetRequest(email.trim()))
        if (!response.isSuccessful) throw AuthApiException(response.code())
    }

    suspend fun confirmPasswordReset(token: String, newPassword: String) {
        val response = api.confirmPasswordReset(
            com.ekichochan.app.data.model.PasswordResetConfirmRequest(token.trim(), newPassword))
        if (!response.isSuccessful) throw AuthApiException(response.code())
    }
}

class AuthApiException(val statusCode: Int, message: String = "Authentication API request failed") :
    RuntimeException(message)

class SessionExpiredException : RuntimeException("Authentication session expired")
