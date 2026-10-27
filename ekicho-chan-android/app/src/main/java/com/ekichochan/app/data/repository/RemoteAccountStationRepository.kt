package com.ekichochan.app.data.repository

import com.ekichochan.app.data.model.CollectionStationPageResponse
import com.ekichochan.app.data.model.StationMasterResponse
import com.ekichochan.app.data.remote.StationApi
import com.ekichochan.app.data.model.LocationCollectionRequest

class RemoteAccountStationRepository(private val api: StationApi) {
    suspend fun draw(): StationMasterResponse = api.drawGacha()
    suspend fun collection(): CollectionStationPageResponse = api.getCollection()
    suspend fun detail(stationId: Int): StationMasterResponse = api.getCollectionStation(stationId)
    suspend fun obtainAtLocation(stationId: Int, latitude: Double, longitude: Double): StationMasterResponse =
        api.obtainAtLocation(LocationCollectionRequest(stationId, latitude, longitude))
}
