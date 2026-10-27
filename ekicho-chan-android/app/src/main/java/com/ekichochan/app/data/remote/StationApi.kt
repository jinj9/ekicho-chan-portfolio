package com.ekichochan.app.data.remote

import com.ekichochan.app.data.model.StationMasterResponse
import com.ekichochan.app.data.model.CollectionStationPageResponse
import retrofit2.http.Query
import retrofit2.http.POST
import retrofit2.http.Body
import com.ekichochan.app.data.model.LocationCollectionRequest
import retrofit2.http.GET
import retrofit2.http.Path

interface StationApi {
    @GET("api/stationMaster/search/{id}")
    suspend fun getStationDetails(@Path("id") id: Int): StationMasterResponse

    // Read-only; unlike the gacha endpoint this does not save acquisitions.
    @GET("api/stationMaster/search/image/{id}")
    suspend fun getStationImage(@Path("id") id: Int): com.ekichochan.app.data.model.StationImageResponse

    @GET("api/stationMaster/store/stationList/{prefectureCd}")
    suspend fun getStationList(@Path("prefectureCd") prefectureCd: Int): List<StationMasterResponse>

    @GET("api/v1/stations/gacha")
    suspend fun drawGacha(): StationMasterResponse

    @GET("api/v1/stations/collection")
    suspend fun getCollection(@Query("page") page: Int = 0,
        @Query("size") size: Int = 48): CollectionStationPageResponse

    @GET("api/v1/stations/collection/{stationId}")
    suspend fun getCollectionStation(@Path("stationId") stationId: Int): StationMasterResponse

    @POST("api/v1/stations/location-get")
    suspend fun obtainAtLocation(@Body request: LocationCollectionRequest): StationMasterResponse
}
