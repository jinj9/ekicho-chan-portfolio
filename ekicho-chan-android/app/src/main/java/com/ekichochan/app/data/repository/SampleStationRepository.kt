package com.ekichochan.app.data.repository

import com.ekichochan.app.data.model.StationMasterResponse

/** APIやデータベースへ接続せず、学習用の架空データを返す。 */
class SampleStationRepository : StationRepository {
    override fun getStation(): StationMasterResponse {
        return StationMasterResponse(
            id = 1,
            stationCd = "00000001",
            stationName = "学習用の駅",
            stationEnglishName = "Sample Station",
            latitude = null,
            longitude = null,
            prefectureCd = 0,
            locationName = "学習用の地域",
            appearanceRateCd = "1",
            stationInformationText = "この駅はViewModelの役割を学ぶための架空のデータです。",
            imageData = null,
            imageType = null,
            isObtained = false
        )
    }
}
