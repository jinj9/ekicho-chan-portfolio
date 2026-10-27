package com.ekichochan.app.data.remote

import com.ekichochan.app.BuildConfig
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import com.ekichochan.app.data.repository.SessionStore
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

// サーバー側で項目が追加されても、Android側が未使用の項目なら無視する。
internal val apiJson = Json {
    ignoreUnknownKeys = true
}

// 生成だけでは通信しない。接続するSTEPで一度生成し、Repositoryへ渡して使う。
private fun createRetrofit(baseUrl: String, sessionStore: SessionStore? = null): Retrofit {
    require(baseUrl.isNotBlank()) {
        "API_BASE_URLが未設定です。ビルド種別に対応する接続先を設定してください。"
    }
    val httpClient = OkHttpClient.Builder().apply {
        if (sessionStore != null) addInterceptor(AuthTokenInterceptor(sessionStore))
    }.build()

    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(httpClient)
        .addConverterFactory(apiJson.asConverterFactory("application/json".toMediaType()))
        .validateEagerly(true)
        .build()
}

fun createStationApi(baseUrl: String = BuildConfig.API_BASE_URL, sessionStore: SessionStore? = null): StationApi =
    createRetrofit(baseUrl, sessionStore).create(StationApi::class.java)

fun createAuthApi(baseUrl: String = BuildConfig.API_BASE_URL, sessionStore: SessionStore): AuthApi =
    createRetrofit(baseUrl, sessionStore).create(AuthApi::class.java)
