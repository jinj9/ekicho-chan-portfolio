package com.ekichochan.app.data.remote

import com.ekichochan.app.BuildConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

// Debug専用のテスト。Release用のテストには含めない。
class ApiClientConfigTest {
    @Test
    fun debugBuildUsesConfiguredHttpOrHttpsUrl() {
        // local.propertiesでローカルHTTPと本番HTTPSを切り替えられる。
        assertTrue(
            BuildConfig.API_BASE_URL.startsWith("http://") ||
                BuildConfig.API_BASE_URL.startsWith("https://")
        )
        assertTrue(BuildConfig.API_BASE_URL.endsWith("/"))
    }

    @Test
    fun createsApiUsingConfiguredUrlWithoutSendingRequest() {
        val api = createStationApi()

        assertNotNull(api)
    }

    @Test
    fun rejectsUnsetBaseUrl() {
        val error = assertThrows(IllegalArgumentException::class.java) {
            createStationApi(baseUrl = "")
        }

        assertEquals(
            "API_BASE_URLが未設定です。ビルド種別に対応する接続先を設定してください。",
            error.message
        )
    }

    @Test
    fun rejectsWhitespaceBaseUrl() {
        assertThrows(IllegalArgumentException::class.java) {
            createStationApi(baseUrl = "   ")
        }
    }
}
