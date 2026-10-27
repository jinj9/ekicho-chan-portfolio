package com.ekichochan.app.data.repository

import com.ekichochan.app.data.model.*
import com.ekichochan.app.data.remote.AuthApi
import kotlinx.coroutines.test.runTest
import okhttp3.Headers
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Test
import retrofit2.Response

class AuthRepositoryTest {
    @Test fun loginStoresResponseToken() = runTest {
        val store = FakeSessionStore()
        val api = FakeAuthApi().apply {
            loginResponse = Response.success(user, Headers.headersOf("X-Auth-Token", "session-123"))
        }

        val result = AuthRepository(api, store).login("user@example.com", "password")

        assertEquals(user, result)
        assertEquals("session-123", store.readToken())
    }

    @Test fun restoreWithoutStoredTokenDoesNotCallServer() = runTest {
        val api = FakeAuthApi()

        assertNull(AuthRepository(api, FakeSessionStore()).restoreSession())
        assertEquals(0, api.currentUserCalls)
    }

    @Test fun unauthorizedRestoreClearsStoredToken() = runTest {
        val store = FakeSessionStore("expired-token")
        val api = FakeAuthApi().apply { currentUserResponse = Response.error(401, emptyBody()) }

        var expired = false
        try {
            AuthRepository(api, store).restoreSession()
        } catch (_: SessionExpiredException) {
            expired = true
        }
        assertTrue(expired)
        assertNull(store.readToken())
    }

    @Test fun logoutAlwaysClearsStoredToken() = runTest {
        val store = FakeSessionStore("session-123")

        AuthRepository(FakeAuthApi(), store).logout()

        assertNull(store.readToken())
    }

    @Test fun successfulAccountDeletionClearsStoredToken() = runTest {
        val store = FakeSessionStore("session-123")

        AuthRepository(FakeAuthApi(), store).deleteAccount("current-password")

        assertNull(store.readToken())
    }

    private val user = AuthUser("account-id", "user@example.com", "駅長", "USER")
}

internal class FakeSessionStore(initialToken: String? = null) : SessionStore {
    private var token = initialToken
    override fun readToken() = token
    override fun saveToken(token: String) { this.token = token }
    override fun clearToken() { token = null }
}

private fun emptyBody() = ByteArray(0).toResponseBody(null)

private class FakeAuthApi : AuthApi {
    lateinit var loginResponse: Response<AuthUser>
    var currentUserResponse: Response<AuthUser> = Response.success(null)
    var currentUserCalls = 0
    override suspend fun login(request: LoginRequest) = loginResponse
    override suspend fun currentUser(): Response<AuthUser> { currentUserCalls++; return currentUserResponse }
    override suspend fun register(request: RegistrationRequest) = Response.success(Unit)
    override suspend fun confirmEmail(request: EmailVerificationRequest) = Response.success(Unit)
    override suspend fun logout() = Response.success(Unit)
    override suspend fun requestPasswordReset(request: PasswordResetRequest) = Response.success(Unit)
    override suspend fun confirmPasswordReset(request: PasswordResetConfirmRequest) = Response.success(Unit)
    override suspend fun changePassword(request: PasswordChangeRequest) = Response.success(Unit)
    override suspend fun changeDisplayName(request: DisplayNameUpdateRequest) = Response.success(
        AuthUser("account-id", "user@example.com", request.displayName, "USER"))
    override suspend fun deleteAccount(request: AccountDeletionRequest) = Response.success(Unit)
}
