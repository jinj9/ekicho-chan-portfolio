package com.ekichochan.app.data.repository

interface SessionStore {
    fun readToken(): String?
    fun saveToken(token: String)
    fun clearToken()
    fun addTokenClearedListener(listener: () -> Unit) = Unit
    fun addSessionExpiredListener(listener: () -> Unit) = Unit
    fun expireToken() = clearToken()
}
