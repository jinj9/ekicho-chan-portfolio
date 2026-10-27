package com.ekichochan.app.data.remote

import com.ekichochan.app.data.repository.SessionStore
import okhttp3.Interceptor
import okhttp3.Response

class AuthTokenInterceptor(private val sessionStore: SessionStore) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = sessionStore.readToken()
        val request = if (token.isNullOrBlank()) {
            chain.request()
        } else {
            chain.request().newBuilder().header(AUTH_HEADER, token).build()
        }
        return chain.proceed(request).also { response ->
            val path = request.url.encodedPath
            val verifiesCurrentPassword = path.endsWith("/auth/password/change") ||
                path.endsWith("/auth/account/delete")
            if (response.code == 401 && !token.isNullOrBlank() && !verifiesCurrentPassword) {
                sessionStore.expireToken()
            }
        }
    }

    companion object { const val AUTH_HEADER = "X-Auth-Token" }
}
