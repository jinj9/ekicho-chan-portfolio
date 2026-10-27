package com.ekichochan.app.data.repository

import com.ekichochan.app.data.remote.createStationApi

data class LocationStation(val id: Int, val name: String)

fun interface LocationStationRepository {
    suspend fun getStations(prefectureCd: Int): List<LocationStation>
}

class RemoteLocationStationRepository : LocationStationRepository {
    // Lazy creation lets missing release configuration become a UI error, not a crash.
    private val api by lazy { createStationApi() }
    override suspend fun getStations(prefectureCd: Int) = api.getStationList(prefectureCd).map {
        LocationStation(it.id, it.stationName ?: "駅名未設定")
    }
}
