package com.ekichochan.app.data.remote

import com.ekichochan.app.data.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface AuthApi {
    @POST("api/v1/auth/register")
    suspend fun register(@Body request: RegistrationRequest): Response<Unit>

    @POST("api/v1/auth/email-verification/confirm")
    suspend fun confirmEmail(@Body request: EmailVerificationRequest): Response<Unit>

    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthUser>

    @GET("api/v1/auth/me")
    suspend fun currentUser(): Response<AuthUser>

    @POST("api/v1/auth/logout")
    suspend fun logout(): Response<Unit>

    @POST("api/v1/auth/password-reset/request")
    suspend fun requestPasswordReset(@Body request: PasswordResetRequest): Response<Unit>

    @POST("api/v1/auth/password-reset/confirm")
    suspend fun confirmPasswordReset(@Body request: PasswordResetConfirmRequest): Response<Unit>

    @POST("api/v1/auth/password/change")
    suspend fun changePassword(@Body request: PasswordChangeRequest): Response<Unit>

    @PUT("api/v1/auth/account/display-name")
    suspend fun changeDisplayName(@Body request: DisplayNameUpdateRequest): Response<AuthUser>

    @POST("api/v1/auth/account/delete")
    suspend fun deleteAccount(@Body request: AccountDeletionRequest): Response<Unit>
}
