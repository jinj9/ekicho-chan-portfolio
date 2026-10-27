package com.ekichochan.app.ui.collection

import com.ekichochan.app.data.model.StationMasterResponse

fun filterCollection(stations: List<StationMasterResponse>, query: String): List<StationMasterResponse> {
    val keyword = query.trim()
    if (keyword.isEmpty()) return stations
    return stations.filter { station ->
        station.stationName?.contains(keyword, ignoreCase = true) == true ||
            station.locationName?.contains(keyword, ignoreCase = true) == true
    }
}
