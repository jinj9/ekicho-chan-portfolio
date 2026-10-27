package com.ekichochan.app.data.repository

import com.ekichochan.app.data.model.StationMasterResponse
import com.ekichochan.app.data.remote.createStationApi
import kotlinx.coroutines.CancellationException

data class StationInfo(val code: String?, val prefecture: String?, val description: String?)

sealed interface StationInfoState {
    data object Loading : StationInfoState
    data object Failed : StationInfoState
    data class Ready(val info: StationInfo) : StationInfoState
}

class StationInfoRepository(
    private val fetch: (suspend (Int) -> StationMasterResponse)? = null
) {
    private val api by lazy { createStationApi() }

    suspend fun load(id: Int): StationInfoState = try {
        val response = if (fetch != null) fetch.invoke(id) else api.getStationDetails(id)
        check(response.id == id) { "Station ID mismatch" }
        StationInfoState.Ready(StationInfo(
            response.stationCd?.takeIf { it.isNotBlank() },
            response.locationName?.takeIf { it.isNotBlank() },
            response.stationInformationText?.takeIf { it.isNotBlank() }
        ))
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        StationInfoState.Failed
    }
}
