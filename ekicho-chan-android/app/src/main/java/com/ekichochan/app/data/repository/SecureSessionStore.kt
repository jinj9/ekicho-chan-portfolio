package com.ekichochan.app.data.repository

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import java.util.concurrent.CopyOnWriteArrayList

class SecureSessionStore(context: Context) : SessionStore {
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    private val tokenClearedListeners = CopyOnWriteArrayList<() -> Unit>()
    private val sessionExpiredListeners = CopyOnWriteArrayList<() -> Unit>()

    override fun readToken(): String? {
        val encoded = preferences.getString(TOKEN_KEY, null) ?: return null
        return runCatching {
            val packed = Base64.decode(encoded, Base64.NO_WRAP)
            require(packed.size > IV_LENGTH)
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, secretKey(), GCMParameterSpec(TAG_LENGTH_BITS, packed.copyOfRange(0, IV_LENGTH)))
            String(cipher.doFinal(packed.copyOfRange(IV_LENGTH, packed.size)), Charsets.UTF_8)
        }.getOrElse {
            clearToken()
            null
        }
    }

    override fun saveToken(token: String) {
        require(token.isNotBlank()) { "Session token must not be blank" }
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey())
        val packed = cipher.iv + cipher.doFinal(token.toByteArray(Charsets.UTF_8))
        preferences.edit().putString(TOKEN_KEY, Base64.encodeToString(packed, Base64.NO_WRAP)).apply()
    }

    override fun clearToken() {
        preferences.edit().remove(TOKEN_KEY).apply()
        tokenClearedListeners.forEach { it() }
    }

    override fun addTokenClearedListener(listener: () -> Unit) {
        tokenClearedListeners += listener
    }

    override fun addSessionExpiredListener(listener: () -> Unit) {
        sessionExpiredListeners += listener
    }

    override fun expireToken() {
        clearToken()
        sessionExpiredListeners.forEach { it() }
    }

    private fun secretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE).apply { load(null) }
        (keyStore.getKey(KEY_ALIAS, null) as? SecretKey)?.let { return it }
        return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE).run {
            init(KeyGenParameterSpec.Builder(KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build())
            generateKey()
        }
    }

    companion object {
        const val PREFERENCES_NAME = "auth_session"
        private const val TOKEN_KEY = "encrypted_token"
        private const val KEY_ALIAS = "ekicho_chan_auth_session"
        private const val ANDROID_KEY_STORE = "AndroidKeyStore"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val IV_LENGTH = 12
        private const val TAG_LENGTH_BITS = 128
    }
}
