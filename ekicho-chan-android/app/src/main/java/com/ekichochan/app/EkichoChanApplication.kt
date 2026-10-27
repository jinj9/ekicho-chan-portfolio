package com.ekichochan.app

import android.app.Application
import com.ekichochan.app.data.remote.createAuthApi
import com.ekichochan.app.data.remote.createStationApi
import com.ekichochan.app.data.repository.AuthRepository
import com.ekichochan.app.data.repository.AuthSessionManager
import com.ekichochan.app.data.repository.SecureSessionStore
import com.ekichochan.app.data.repository.RemoteAccountStationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class EkichoChanApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    lateinit var authRepository: AuthRepository
        private set
    lateinit var authSessionManager: AuthSessionManager
        private set
    lateinit var accountStationRepository: RemoteAccountStationRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val sessionStore = SecureSessionStore(this)
        authRepository = AuthRepository(createAuthApi(sessionStore = sessionStore), sessionStore)
        accountStationRepository = RemoteAccountStationRepository(
            createStationApi(sessionStore = sessionStore))
        authSessionManager = AuthSessionManager(authRepository, applicationScope, sessionStore).also { it.restore() }
    }
}
