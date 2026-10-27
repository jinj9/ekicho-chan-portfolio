package com.ekichochan.app.data.sample

import com.ekichochan.app.data.model.StationMasterResponse

/** UI確認専用。実際の取得履歴やガチャ結果とは連動しない。 */
object SampleCollectionData {
    fun findStation(stationId: Int?): StationMasterResponse? =
        stations.find { station -> station.id == stationId }

    private val firstStation = StationMasterResponse(
        id = 101,
        stationCd = "sample-101",
        stationName = "はじまり駅（学習用）",
        stationEnglishName = null,
        latitude = null,
        longitude = null,
        prefectureCd = 0,
        locationName = "架空の地域",
        appearanceRateCd = null,
        stationInformationText = "旅のはじまりをイメージした架空の駅です。",
        imageData = null,
        imageType = null,
        isObtained = true
    )

    val stations: List<StationMasterResponse> = listOf(
        firstStation,
        firstStation.copy(
            id = 102,
            stationCd = "sample-102",
            stationName = "海辺駅（学習用）",
            stationInformationText = "海の近くにある駅をイメージした架空のデータです。"
        ),
        firstStation.copy(
            id = 103,
            stationCd = "sample-103",
            stationName = "山あい駅（学習用）",
            stationInformationText = "山に囲まれた駅をイメージした架空のデータです。"
        )
    )
}
