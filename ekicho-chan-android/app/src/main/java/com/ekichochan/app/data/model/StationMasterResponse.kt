package com.ekichochan.app.data.model

import kotlinx.serialization.Serializable

/**
 * Spring BootのStationMasterResponseに対応するデータ型。
 * Java側で非null保証がない参照型は、nullableとして扱う。
 */
@Serializable
data class StationMasterResponse(
    val id: Int,
    val stationCd: String?,
    val stationName: String?,
    val stationEnglishName: String?,
    val latitude: Double?,
    val longitude: Double?,
    // 現在のガチャAPIでは未設定のため0が返る。
    val prefectureCd: Int,
    val locationName: String?,
    val appearanceRateCd: String?,
    val stationInformationText: String?,
    // 画像そのものではなく、Base64で表現された文字列。
    val imageData: String?,
    val imageType: String?,
    val isObtained: Boolean? = null,
    val firstObtainedAt: String? = null,
    val lastObtainedAt: String? = null,
    val obtainedCount: Int? = null,
    val firstObtainedMethod: String? = null,
    val lastObtainedMethod: String? = null,
    val alreadyObtained: Boolean? = null
)

@Serializable
data class CollectionStationPageResponse(
    val content: List<StationMasterResponse>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int
)

@Serializable
data class LocationCollectionRequest(
    val stationId: Int,
    val latitude: Double,
    val longitude: Double
)
