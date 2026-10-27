package com.ekichochan.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthUser(
    val accountId: String,
    val email: String,
    val displayName: String,
    val role: String,
)

@Serializable data class LoginRequest(val email: String, val password: String)
@Serializable data class RegistrationRequest(val email: String, val displayName: String, val password: String)
@Serializable data class EmailVerificationRequest(val token: String)
@Serializable data class PasswordResetRequest(val email: String)
@Serializable data class PasswordResetConfirmRequest(val token: String, val newPassword: String)
@Serializable data class PasswordChangeRequest(val currentPassword: String, val newPassword: String)
@Serializable data class DisplayNameUpdateRequest(val displayName: String)
@Serializable data class AccountDeletionRequest(val currentPassword: String)
