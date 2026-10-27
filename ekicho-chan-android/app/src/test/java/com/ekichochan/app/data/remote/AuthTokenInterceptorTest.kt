package com.ekichochan.app.data.remote

import com.ekichochan.app.data.repository.FakeSessionStore
import com.ekichochan.app.data.repository.SessionStore
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.*
import org.junit.Test

class AuthTokenInterceptorTest {
    @Test fun addsTokenHeaderToRequest() {
        val server = MockWebServer().apply { enqueue(MockResponse().setResponseCode(200)); start() }
        try {
            val store = FakeSessionStore("session-123")
            val client = OkHttpClient.Builder().addInterceptor(AuthTokenInterceptor(store)).build()

            client.newCall(Request.Builder().url(server.url("/me")).build()).execute().close()

            assertEquals("session-123", server.takeRequest().getHeader("X-Auth-Token"))
        } finally { server.shutdown() }
    }

    @Test fun clearsStoredTokenAfterUnauthorizedResponse() {
        val server = MockWebServer().apply { enqueue(MockResponse().setResponseCode(401)); start() }
        try {
            val store = FakeSessionStore("expired-token")
            val client = OkHttpClient.Builder().addInterceptor(AuthTokenInterceptor(store)).build()

            client.newCall(Request.Builder().url(server.url("/me")).build()).execute().close()

            assertNull(store.readToken())
        } finally { server.shutdown() }
    }

    @Test fun notifiesSessionExpirationAfterUnauthorizedResponse() {
        val server = MockWebServer().apply { enqueue(MockResponse().setResponseCode(401)); start() }
        try {
            val store = ExpirationTrackingStore("expired-token")
            val client = OkHttpClient.Builder().addInterceptor(AuthTokenInterceptor(store)).build()

            client.newCall(Request.Builder().url(server.url("/collection")).build()).execute().close()

            assertNull(store.readToken())
            assertTrue(store.expired)
        } finally { server.shutdown() }
    }

    @Test fun keepsSessionWhenAccountDeletionPasswordIsWrong() {
        val server = MockWebServer().apply { enqueue(MockResponse().setResponseCode(401)); start() }
        try {
            val store = ExpirationTrackingStore("session-123")
            val client = OkHttpClient.Builder().addInterceptor(AuthTokenInterceptor(store)).build()

            client.newCall(Request.Builder().url(server.url("/api/v1/auth/account/delete")).build())
                .execute().close()

            assertEquals("session-123", store.readToken())
            assertFalse(store.expired)
        } finally { server.shutdown() }
    }
}

private class ExpirationTrackingStore(initialToken: String?) : SessionStore {
    private var token = initialToken
    var expired = false
        private set
    override fun readToken() = token
    override fun saveToken(token: String) { this.token = token }
    override fun clearToken() { token = null }
    override fun expireToken() { clearToken(); expired = true }
}
